/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.execserver.runner.flow;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.mail.EmailManager;
import com.baifendian.swordfish.common.utils.graph.DAGGraph;
import com.baifendian.swordfish.common.utils.graph.Graph;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.FlowNodeRelation;
import com.baifendian.swordfish.dao.model.flow.FlowDag;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.exception.ExecTimeoutException;
import com.baifendian.swordfish.execserver.job.JobContext;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import com.baifendian.swordfish.execserver.parameter.SystemParamManager;
import com.baifendian.swordfish.execserver.runner.node.NodeRunner;
import com.baifendian.swordfish.execserver.utils.EnvHelper;
import com.baifendian.swordfish.execserver.utils.LoggerUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * flow 执行器 <p>
 */
public class FlowRunner implements Runnable {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String JOB_PREFIX = "FLOW";

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * {@link ExecutionFlow}
   */
  private final ExecutionFlow executionFlow;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService nodeExecutorService;

  /**
   * 所有提交过的节点, node name => ExecutionNode
   */
  private final Map<String, ExecutionNode> executionNodeMap = new ConcurrentHashMap<>();

  /**
   * 正在运行的 nodeRunner
   */
  private Map<NodeRunner, Future<Boolean>> activeNodeRunners = new ConcurrentHashMap<>();

  /**
   * 一个节点失败后的策略类型
   */
  private final FailurePolicyType failurePolicyType;

  /**
   * 最大重试次数
   */
  private final int maxTryTimes;

  /**
   * 该工作流的超时时间, 单位: 秒
   */
  private final int timeout;

  /**
   * 运行的起始时间 (ms)
   */
  private final long startTime;

  /**
   * 是否停止
   */
  private volatile boolean shutdown;

  /**
   * @param context
   */
  public FlowRunner(FlowRunnerContext context) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.executionFlow = context.getExecutionFlow();
    this.nodeExecutorService = context.getNodeExecutorService();
    this.maxTryTimes = context.getMaxTryTimes();
    this.timeout = context.getTimeout();
    this.failurePolicyType = context.getFailurePolicyType();
    this.startTime = executionFlow.getStartTime().getTime();
    this.shutdown = false;
  }

  /**
   * 线程入口
   */
  @Override
  public void run() {
    // 查看是否已经完成
    ExecutionFlow newExecutionFlow = flowDao.queryExecutionFlow(executionFlow.getId());

    // 能查到
    if (newExecutionFlow != null) {
      if (newExecutionFlow.getStatus().typeIsFinished()) {
        logger.info("flow is done: {}", executionFlow.getId());
        return;
      }

      // 不用从头执行
      // flowDao.deleteExecutionNodes(executionFlow.getId());
    } else { // 压根查不到
      logger.info("flow is not exist: {}", executionFlow.getId());
      return;
    }

    FlowStatus status = null;

    // 得到执行的目录
    String execLocalPath = BaseConfig
        .getFlowExecDir(executionFlow.getProjectId(), executionFlow.getFlowId(),
            executionFlow.getId());

    logger.info(
        "exec id:{}, current execution dir:{}, max try times:{}, timeout:{}, failure policy type:{}",
        executionFlow.getId(), execLocalPath, maxTryTimes, timeout, failurePolicyType);

    // 做变量替换, 并且更新
    if (StringUtils.isEmpty(executionFlow.getWorkflowDataSub())) {
      Map<String, String> systemParamMap = SystemParamManager
          .buildSystemParam(executionFlow.getType(), executionFlow.getScheduleTime());

      // 构建自定义参数, 比如定义了 ${abc} = ${sf.system.bizdate}, $[yyyyMMdd] 等情况
      Map<String, String> customParamMap = executionFlow.getUserDefinedParamMap();

      Map<String, String> allParamMap = new HashMap<>();

      if (systemParamMap != null) {
        allParamMap.putAll(systemParamMap);
      }

      if (customParamMap != null) {
        allParamMap.putAll(customParamMap);
      }

      executionFlow.setWorkflowDataSub(ParamHelper
          .resolvePlaceholders(executionFlow.getWorkflowData(), allParamMap));

      flowDao.updateExecutionFlowDataSub(executionFlow);
    }

    // 调度执行
    try {
      // 创建工作目录和用户
      EnvHelper.workDirAndUserCreate(execLocalPath, executionFlow.getProxyUser(), logger);

      // 解析工作流, 得到 DAG 信息
      FlowDag flowDag = JsonUtil.parseObject(executionFlow.getWorkflowData(), FlowDag.class);

      // 下载 workflow 的资源文件到本地 exec 目录
      String workflowHdfsFile = BaseConfig
          .getHdfsWorkflowFilename(executionFlow.getProjectId(), executionFlow.getWorkflowName());
      HdfsClient hdfsClient = HdfsClient.getInstance();

      if (hdfsClient.exists(workflowHdfsFile)) {
        logger.info("get hdfs workflow file:{}", workflowHdfsFile);

        String destPath = execLocalPath + File.separator + executionFlow.getWorkflowName() + ".zip";
        logger.info("Copy hdfs workflow: {} to local: {}", workflowHdfsFile, destPath);

        HdfsClient.getInstance().copyHdfsToLocal(workflowHdfsFile, destPath, false, true);

        // 资源文件解压缩处理 workflow 下的文件为 workflowName.zip
        File zipFile = new File(destPath);
        if (zipFile.exists()) {
          String cmd = String.format("unzip -o %s -d %s", destPath, execLocalPath);

          logger.info("call cmd:{}", cmd);

          Process process = Runtime.getRuntime().exec(cmd);
          int ret = process.waitFor();
          if (ret != 0) {
            logger.error("run cmd error:{}", cmd);
            logger.error(IOUtils.toString(process.getErrorStream(), Charset.forName("UTF-8")));
          }
        } else {
          logger.error("can't found workflow zip file:{}", zipFile.getPath());
        }
      } else {
        logger.debug("hdfs workflow file:{} not exists", workflowHdfsFile);
      }

      // 解析作业参数获取需要的 "项目级资源文件" 清单
      List<String> projectRes = genProjectResFiles(flowDag);

      // 将 hdfs 资源拷贝到本地
      EnvHelper.copyResToLocal(executionFlow.getProjectId(), execLocalPath, projectRes, logger);

      // 生成具体 Dag, 待执行
      Graph<String, FlowNode, FlowNodeRelation> dagGraph = genDagGraph(flowDag);

      // 执行 flow, 这里会等待任务完全结束才会返回
      status = runFlow(dagGraph);
    } catch (ExecTimeoutException e) {
      logger.error("Exec flow timeout", e);
      clean(true);
    } catch (Exception e) {
      logger.error(String.format("run exec id: %s", executionFlow.getId()), e);
      clean(true);
    } finally {
      // 执行失败
      if (status == null) {
        updateExecutionFlow(FlowStatus.FAILED);
      } else {
        // 更新 ExecutionFlow
        updateExecutionFlow(status);
      }

      // 后置处理
      postProcess();
    }
  }

  /**
   * 生成flow的 DAG <p>
   *
   * @return DAG
   */
  private Graph<String, FlowNode, FlowNodeRelation> genDagGraph(FlowDag flowDag) {
    Graph<String, FlowNode, FlowNodeRelation> dagGraph = new DAGGraph<>();

    if (CollectionUtils.isNotEmpty(flowDag.getNodes())) {
      for (FlowNode node : flowDag.getNodes()) {
        dagGraph.addVertex(node.getName(), node);
      }
    }

    if (CollectionUtils.isNotEmpty(flowDag.getEdges())) {
      for (FlowNodeRelation edge : flowDag.getEdges()) {
        dagGraph.addEdge(edge.getStartNode(), edge.getEndNode());
      }
    }

    return dagGraph;
  }

  /**
   * 生成项目的资源文件
   */
  private List<String> genProjectResFiles(FlowDag flowDag) throws
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    List<FlowNode> nodes = flowDag.getNodes();
    Set<String> projectFiles = new HashSet<>();

    for (FlowNode node : nodes) {
      // 得到结点参数信息
      BaseParam baseParam = BaseParamFactory.getBaseParam(node.getType(), node.getParameter());

      // 结点参数中获取资源文件
      if (baseParam != null) {
        List<String> projectResourceFiles = baseParam.getProjectResourceFiles();
        if (projectResourceFiles != null) {
          projectFiles.addAll(projectResourceFiles);
        }
      }
    }

    return new ArrayList<>(projectFiles);
  }

  /**
   * 运行一个具体的 DAG: 1. 首先取出 start 结点 2. 得到结点, 提交并运行 3. 设置门闩, 满足门闩的话, 如果超时, 调到 END, 如果没超时, 调到 4 4.
   * 如果是失败的化, 重试, 如果重试够多则直接失败, 如果是成功, 则调到 5 5. 看该节点的后继, 运行其后继, 提交, 跑到 2 6. 如果都运行完了, 到 SUCCESS <p>
   * END: 失败状态的话, 错误; 否则认为成功
   */

  private FlowStatus runFlow(Graph<String, FlowNode, FlowNodeRelation> dagGraph) {
    // 信号量控制, 是否有线程结束了
    Semaphore semaphore = new Semaphore(0);

    // 将 dagGraph 做一下处理, 去掉已经完成的结点
    try {
      for (String nodeName : dagGraph.topologicalSort()) {
        ExecutionNode executionNode = flowDao.queryExecutionNode(executionFlow.getId(), nodeName);

        // 删除完成的结点
        if (executionNode != null && executionNode.getStatus().typeIsFinished()) {
          dagGraph.removeVertex(nodeName);
        }
      }
    } catch (Exception e) {
      logger.error("Get topological of graph failed.", e);
      return FlowStatus.FAILED;
    }

    // 得到起始结点
    Collection<String> startVertex = dagGraph.getStartVertex();

    // 提交起始的一些节点
    for (String nodeName : startVertex) {
      if (!executionNodeMap.containsKey(nodeName)) {
        // 插入一个结点
        ExecutionNode executionNode = insertExecutionNode(executionFlow, nodeName);

        // 添加任务
        executionNodeMap.put(nodeName, executionNode);

        // 提交任务
        submitNodeRunner(dagGraph.getVertex(nodeName), executionNode, semaphore);
      }
    }

    // 状态
    FlowStatus status = FlowStatus.SUCCESS;

    // 如果有在运行的节点存在, 则一直循环
    while (!activeNodeRunners.isEmpty()) {
      boolean acquire = false;

      try {
        // 等待, 如果有超时, 会抛出一次
        acquire = semaphore.tryAcquire(calcNodeTimeout(), TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      } catch (ExecTimeoutException e) {
        logger.error(e.getMessage(), e);
      }

      // 如果没有获取到, 则执行清理
      if (!acquire) {
        clean(true);
        return FlowStatus.FAILED;
      }

      // 查看是哪个任务成功了, 只能更新一个
      boolean done = false;

      while (!done) {
        // 等待一会再检测
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
        }

        // 遍历, 得到一个可以执行的信息
        for (Map.Entry<NodeRunner, Future<Boolean>> entry : activeNodeRunners.entrySet()) {
          NodeRunner nodeRunner = entry.getKey();
          Future<Boolean> future = entry.getValue();

          // 如果完成了
          if (future.isDone()) {
            // 完成了
            done = true;

            // 删除, 认为执行完毕
            activeNodeRunners.remove(nodeRunner);

            Boolean value = false;

            Date now = new Date();

            try {
              value = future.get();
            } catch (CancellationException e) {
              logger.error("task has been cancel");

              // 清理任务
              clean(true);
              return FlowStatus.KILL;
            } catch (InterruptedException e) {
              logger.error(e.getMessage(), e);
            } catch (ExecutionException e) {
              logger.error(e.getMessage(), e);
            }

            // 如果失败
            if (!value) {
              // 如果没有达到重试次数, 重试即可
              ExecutionNode executionNode = executionNodeMap.get(nodeRunner.getNodename());

              // 比如, 次数是 2, 则可以尝试 2 次
              if (executionNode.getAttempt() < maxTryTimes) {
                executionNode.incAttempt();

                // 更新结点状态
                flowDao.updateExecutionNode(executionNode);

                // 重新提交
                submitNodeRunner(dagGraph.getVertex(nodeRunner.getNodename()), executionNode,
                    semaphore);
              } else {
                // 不能继续尝试了
                status = FlowStatus.FAILED;

                executionNode.setEndTime(now);
                executionNode.setStatus(status);

                // 更新结点状态
                flowDao.updateExecutionNode(executionNode);

                if (failurePolicyType == FailurePolicyType.END) {
                  clean(true);
                  return status;
                }
              }
            } else { // 如果成功
              // 更新一下状态
              ExecutionNode executionNode = executionNodeMap.get(nodeRunner.getNodename());

              executionNode.setEndTime(now);
              executionNode.setStatus(FlowStatus.SUCCESS);

              flowDao.updateExecutionNode(executionNode);

              // 成功, 看后继, 提交后继
              for (String nodeName : dagGraph.getPostNode(nodeRunner.getNodename())) {
                if (!executionNodeMap.containsKey(nodeName) && isPreNodesAllSuccess(
                    dagGraph.getPreNode(nodeName))) {
                  // 插入一个结点
                  ExecutionNode newExecutionNode = insertExecutionNode(executionFlow, nodeName);

                  // 添加任务
                  executionNodeMap.put(nodeName, newExecutionNode);

                  // 提交任务
                  submitNodeRunner(dagGraph.getVertex(nodeName), newExecutionNode, semaphore);
                }
              }
            }

            break;
          }
        }
      }
    }

    return status;
  }

  /**
   * 插入一个结点, 如果存在, 则直接返回
   */
  private ExecutionNode insertExecutionNode(ExecutionFlow executionFlow, String nodeName) {
    ExecutionNode executionNode = flowDao.queryExecutionNode(executionFlow.getId(), nodeName);

    if (executionNode != null) {
      return executionNode;
    }

    // 创建新结点并插入
    executionNode = new ExecutionNode();

    Date now = new Date();

    executionNode.setExecId(executionFlow.getId());
    executionNode.setName(nodeName);
    executionNode.setAttempt(0);
    executionNode.setStartTime(now);
    executionNode.setStatus(FlowStatus.INIT);
    executionNode.setJobId(LoggerUtil.genJobId(JOB_PREFIX, executionFlow.getId(), nodeName));

    logger.info("insert execution node, id: {}, name: {}, start time: {}, status: {}, job id: {}",
        executionNode.getExecId(),
        nodeName,
        now,
        FlowStatus.INIT,
        LoggerUtil.genJobId(JOB_PREFIX, executionFlow.getId(), nodeName));

    // 更新数据库
    flowDao.insertExecutionNode(executionNode);

    return executionNode;
  }

  /**
   * 提交 NodeRunner 执行
   */
  private void submitNodeRunner(FlowNode flowNode, ExecutionNode executionNode,
      Semaphore semaphore) {
    JobContext jobContext = new JobContext();

    jobContext.setExecutionFlow(executionFlow);
    jobContext.setExecutionNode(executionNode);
    jobContext.setFlowNode(flowNode);
    jobContext.setSemaphore(semaphore);

    // 构建 node runner
    NodeRunner nodeRunner = new NodeRunner(jobContext);

    Future<Boolean> future = nodeExecutorService.submit(nodeRunner);

    activeNodeRunners.putIfAbsent(nodeRunner, future);
  }

  /**
   * 计算节点的超时时间（s） <p>
   *
   * @return 超时时间
   */
  private int calcNodeTimeout() {
    int usedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);

    int remainTime = timeout - usedTime;

    if (remainTime <= 0) {
      throw new ExecTimeoutException("workflow execution time out");
    }

    return remainTime;
  }

  /**
   * 更新 ExecutionFlow <p>
   */
  private void updateExecutionFlow(FlowStatus status) {
    // 如果是关闭情况下, 且 shutdown 了, 且是调度或补数据
    if (shutdown && status == FlowStatus.KILL && (
        executionFlow.getType() == ExecType.COMPLEMENT_DATA
            || executionFlow.getType() == ExecType.SCHEDULER)) {
      return;
    }

    Date now = new Date();

    // 没有完成才更新
    if (executionFlow.getStatus().typeIsNotFinished()) {
      executionFlow.setEndTime(now);
      executionFlow.setStatus(status);

      flowDao.updateExecutionFlow(executionFlow);
    }
  }

  /**
   * 更新 ExecutionFlow <p> 注意的是, 调度和补数据没有状态更新, 这是为了能够容错处理.
   */
  public void updateExecutionFlowToKillStatus(boolean updateKilled) {
    ExecutionFlow queryExecutionFlow = flowDao.queryExecutionFlow(executionFlow.getId());

    if (updateKilled || (queryExecutionFlow.getType() != ExecType.SCHEDULER
        && queryExecutionFlow.getType() != ExecType.COMPLEMENT_DATA)) {
      updateToKilled(queryExecutionFlow);
    }
  }

  /**
   * 更新为 kill 状态
   *
   * @param executionFlow : 待更新的 flow
   */
  private void updateToKilled(ExecutionFlow executionFlow) {
    if (executionFlow.getStatus().typeIsNotFinished()) {
      Date now = new Date();

      executionFlow.setEndTime(now);
      executionFlow.setStatus(FlowStatus.KILL);

      flowDao.updateExecutionFlow(executionFlow);
    }
  }

  /**
   * 更新的是结点
   */
  private void updateNodeToKilled(ExecutionNode executionNode) {
    Date now = new Date();

    executionNode.setStatus(FlowStatus.KILL);
    executionNode.setEndTime(now);

    flowDao.updateExecutionNode(executionNode);
  }

  /**
   * 关闭正在执行的任务, 以及更新节点状态
   */
  public void clean(boolean updateKilled) {
    // kill 正在运行的任务
    kill();

    // 更新未完成的任务结点
    updateUnfinishNodeStatus(updateKilled);
  }

  /**
   * 关闭
   */
  public void shutdown() {
    this.shutdown = true;
  }

  /**
   * 对没有完成的节点, 更新其状态
   */
  private void updateUnfinishNodeStatus(boolean updateKilled) {
    Date now = new Date();

    // 遍历没有完成的节点
    for (Map.Entry<NodeRunner, Future<Boolean>> entry : activeNodeRunners.entrySet()) {
      NodeRunner nodeRunner = entry.getKey();
      Future<Boolean> future = entry.getValue();

      // 进程还在的情况下
      if (!future.isDone()) {
        // 如果是需要更新, 或者是不需要更新, 但是不是补数据和调度方式
        if (updateKilled || (nodeRunner.getExecType() != ExecType.SCHEDULER
            && nodeRunner.getExecType() != ExecType.COMPLEMENT_DATA)) {
          ExecutionNode executionNode = nodeRunner.getExecutionNode();
          updateNodeToKilled(executionNode);
        }
      } else {
        // 已经结束的情况
        Boolean value = false;

        try {
          value = future.get();

          if (value) {
            ExecutionNode executionNode = nodeRunner.getExecutionNode();

            executionNode.setStatus(FlowStatus.SUCCESS);
            executionNode.setEndTime(now);

            flowDao.updateExecutionNode(executionNode);
          }
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
          logger.error(e.getMessage(), e);
        } catch (CancellationException e) { // 任务被取消了
          logger.error("task has been cancel, name:{}", nodeRunner.getNodename());
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        } finally {
          if (!value) {
            if (updateKilled || (nodeRunner.getExecType() != ExecType.SCHEDULER
                && nodeRunner.getExecType() != ExecType.COMPLEMENT_DATA)) {
              ExecutionNode executionNode = nodeRunner.getExecutionNode();
              updateNodeToKilled(executionNode);
            }
          }
        }
      }
    }
  }

  /**
   * kill 执行
   */
  private void kill() {
    synchronized (this) {
      if (activeNodeRunners.isEmpty()) {
        return;
      }

      logger.info("Kill has been called on exec id: {}, num: {}", executionFlow.getId(),
          activeNodeRunners.size());

      // 正在运行中的
      for (Map.Entry<NodeRunner, Future<Boolean>> entry : activeNodeRunners.entrySet()) {
        NodeRunner nodeRunner = entry.getKey();
        Future<Boolean> future = entry.getValue();

        if (!future.isDone()) {
          // 记录 kill 的信息
          logger
              .info("kill exec, id: {}, node: {}", executionFlow.getId(), nodeRunner.getNodename());

          // 结点运行
          nodeRunner.kill();

          // 强制进行关闭, 关闭线程
          future.cancel(true);
        }
      }
    }
  }

  /**
   * flow 执行完的后置处理 <p>
   */
  private void postProcess() {
    logger.info("Develop mode is: {}", BaseConfig.isDevelopMode());

    if (!BaseConfig.isDevelopMode()) {
      // 执行完后, 清理目录, 避免文件过大
      String execLocalPath = BaseConfig
          .getFlowExecDir(executionFlow.getProjectId(), executionFlow.getFlowId(),
              executionFlow.getId());

      try {
        FileUtils.deleteDirectory(new File(execLocalPath));
      } catch (IOException e) {
        logger.error(String.format("delete exec dir exception: %s", execLocalPath), e);
      }

      // 执行完后, 清理 udf 目录
      hdfsCleanUp(BaseConfig.getJobHiveUdfJarPath(executionFlow.getId()));

      // 执行完后, 清理 import/export 目录
      hdfsCleanUp(BaseConfig.getHdfsImpExpDir(executionFlow.getProjectId(), executionFlow.getId()));
    }

    EmailManager.sendMessageOfExecutionFlow(executionFlow);
  }

  /**
   * 清理 hdfs 上的目录
   */
  private void hdfsCleanUp(String path) {
    try {
      if (HdfsClient.getInstance().exists(path)) {
        HdfsClient.getInstance().delete(path, true);
      }
    } catch (Exception e) {
      logger.error(String.format("cleanup hdfs dir exception: %s", path), e);
    }
  }

  /**
   * 查看前驱是否都 OK
   */
  private boolean isPreNodesAllSuccess(Set<String> preNodes) {
    // 没有前驱节点，认为全部执行成功
    if (CollectionUtils.isEmpty(preNodes)) {
      return true;
    }

    for (String preNode : preNodes) {
      ExecutionNode preFinishedNode = executionNodeMap.get(preNode);

      // 没查到
      if (preFinishedNode == null || preFinishedNode.getStatus().typeIsNotFinished()) {
        return false;
      }

      // 如果失败了, 且应该是停止的
      if (!preFinishedNode.getStatus().typeIsSuccess()/*
          && failurePolicyType == FailurePolicyType.END*/) {
        return false;
      }
    }

    return true;
  }
}