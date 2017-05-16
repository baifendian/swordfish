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
import com.baifendian.swordfish.execserver.job.Job;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.JobTypeManager;
import com.baifendian.swordfish.execserver.runner.node.NodeRunner;
import com.baifendian.swordfish.execserver.utils.LoggerUtil;
import com.baifendian.swordfish.execserver.utils.OsUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
   * {@link FlowRunnerContext}
   */
  private final FlowRunnerContext context;

  /**
   * {@link ExecutionFlow}
   */
  private final ExecutionFlow executionFlow;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService executorService;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService jobExecutorService;

  /**
   * 执行的节点信息
   */
  private final List<ExecutionNode> executionNodes = new CopyOnWriteArrayList<>();

  /**
   * 执行完成的节点信息
   */
  private final List<ExecutionNode> finishedExecutionNodes = new CopyOnWriteArrayList<>();

  /**
   * 跳过执行的节点信息
   */
  private final List<String> skipNodes = new CopyOnWriteArrayList<>();

  /**
   * 正在运行的 nodeRunner
   */
  private Set<NodeRunner> activeNodeRunners = Collections.newSetFromMap(new ConcurrentHashMap<>());

  /**
   * 一个节点失败后的策略类型
   */
  private final FailurePolicyType failurePolicyType;

  /**
   * 最大重试次数
   */
  private final int maxTryTimes;

  /**
   * 节点最大的超时时间 (2)
   */
  private final int timeout;

  /**
   * 起始时间 (ms)
   */
  private final long startTime = System.currentTimeMillis();

  /**
   * 同步对象
   */
  private final Object synObject = new Object();

  /**
   * workflow 是否执行成功
   */
  private boolean isSuccess = true;

  /**
   * 系统参数
   */
  private final Map<String, String> systemParamMap;

  /**
   * 自定义参数
   */
  private final Map<String, String> customParamMap;

  /**
   * @param context
   */
  public FlowRunner(FlowRunnerContext context) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.context = context;
    this.executionFlow = context.getExecutionFlow();
    this.executorService = context.getExecutorService();
    this.jobExecutorService = context.getJobExecutorService();
    this.maxTryTimes = context.getMaxTryTimes();
    this.timeout = context.getTimeout();
    this.failurePolicyType = context.getFailurePolicyType();
    this.systemParamMap = context.getSystemParamMap();
    this.customParamMap = context.getCustomParamMap();
  }

  /**
   * 执行
   */
  @Override
  public void run() {
    FlowStatus status = null;

    try {
      // 得到执行的目录
      String execLocalPath = BaseConfig.getFlowExecDir(executionFlow.getProjectId(), executionFlow.getFlowId(),
          executionFlow.getId());

      logger.info("exec id:{} current execution dir:{}", executionFlow.getId(), execLocalPath);

      // 如果存在, 首先清除该目录
      File execLocalPathFile = new File(execLocalPath);

      if (execLocalPathFile.exists()) {
        FileUtils.forceDelete(execLocalPathFile);
      }

      // 创建目录
      FileUtils.forceMkdir(execLocalPathFile);

      // proxyUser 用户处理, 如果系统不存在该用户，这里自动创建用户
      String proxyUser = executionFlow.getProxyUser();
      List<String> osUserList = OsUtil.getUserList();

      // 不存在, 则创建
      if (!osUserList.contains(proxyUser)) {
        String userGroup = OsUtil.getGroup();
        if (StringUtils.isNotEmpty(userGroup)) {
          logger.info("create os user:{}", proxyUser);

          String cmd = String.format("sudo useradd -g %s %s", userGroup, proxyUser);

          logger.info("exec cmd: {} ", cmd);

          OsUtil.exeCmd(cmd);
        }
      }

      // 解析工作流
      FlowDag flowDag = JsonUtil.parseObject(executionFlow.getWorkflowData(), FlowDag.class);

      // 下载 workflow 的资源文件到本地 exec 目录
      String workflowHdfsFile = BaseConfig.getHdfsWorkflowFilename(executionFlow.getProjectId(), executionFlow.getWorkflowName());
      HdfsClient hdfsClient = HdfsClient.getInstance();

      if (hdfsClient.exists(workflowHdfsFile)) {
        logger.info("get hdfs workflow file:{}", workflowHdfsFile);

        HdfsClient.getInstance().copyHdfsToLocal(workflowHdfsFile, execLocalPath, false, true);

        // 资源文件解压缩处理 workflow 下的文件为 workflowName.zip
        File zipFile = new File(execLocalPath, executionFlow.getWorkflowName() + ".zip");
        if (zipFile.exists()) {
          String cmd = String.format("unzip -o %s -d %s", zipFile.getPath(), execLocalPath);

          logger.info("call cmd:" + cmd);

          Process process = Runtime.getRuntime().exec(cmd);
          int ret = process.waitFor();
          if (ret != 0) {
            logger.error("run cmd:" + cmd + " error");
            logger.error(IOUtils.toString(process.getErrorStream(), Charset.forName("UTF-8")));
          }
        } else {
          logger.error("can't found workflow zip file:" + zipFile.getPath());
        }
      } else {
        logger.debug("hdfs workflow file:{} not exists", workflowHdfsFile);
      }

      // 解析作业参数获取需要的项目级资源文件清单
      List<String> projectRes = genProjectResFiles(flowDag);
      for (String res : projectRes) {
        File resFile = new File(execLocalPath, res);
        if (!resFile.exists()) {
          String resHdfsPath = BaseConfig.getHdfsResourcesFilename(executionFlow.getProjectId(), res);

          logger.info("get project file:{}", resHdfsPath);

          HdfsClient.getInstance().copyHdfsToLocal(resHdfsPath, execLocalPath, false, true);
        } else {
          logger.info("file:{} exists, ignore", resFile.getName());
        }
      }

      // 生成具体 Dag
      Graph<String, FlowNode, FlowNodeRelation> dagGraph = genDagGraph(flowDag);

      // 执行 flow
      status = runFlow(dagGraph);

      // 更新 ExecutionFlow
      updateExecutionFlow(status);
    } catch (ExecTimeoutException e) {
      logger.error(e.getMessage(), e);
      // 超时时，取消所有正在执行的作业
      cancelAllExectingNode();
    } catch (Throwable e) {
      logger.error("run exec id:" + executionFlow.getId(), e);
    } finally {
      // 执行失败
      if (status == null) {
        updateExecutionFlow(FlowStatus.FAILED);
      }
      // 后置处理
      after();
    }
  }

  /**
   * 生成flow的 DAG <p>
   *
   * @param flowDag
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
   *
   * @param flowDag
   * @return
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  private List<String> genProjectResFiles(FlowDag flowDag) throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    List<FlowNode> nodes = flowDag.getNodes();
    Set<String> projectFiles = new HashSet<>();

    Map<String, String> allParamMap = new HashMap<>();
    allParamMap.putAll(systemParamMap);
    allParamMap.putAll(customParamMap);

    for (FlowNode node : nodes) {
      JobProps props = new JobProps();
      props.setJobParams(node.getParameter());
      props.setDefinedParams(allParamMap);
      String jobId = node.getType() + "_" + node.getName();

      Job job = JobTypeManager.newJob(jobId, node.getType(), props, logger);
      if (job.getParam() != null && job.getParam().getProjectResourceFiles() != null) {
        projectFiles.addAll(job.getParam().getProjectResourceFiles());
      }
    }

    return new ArrayList<>(projectFiles);
  }

  /**
   * 执行单个节点 <p>
   *
   * @param flowNode
   * @return {@link FlowStatus}
   */
  private FlowStatus runNode(FlowNode flowNode) {
    // 判断是否为空
    if (flowNode == null) {
      logger.error("节点为空");
      return FlowStatus.FAILED;
    }

    // 是否全部执行完成
    boolean isAllFinished = false;

    synchronized (synObject) {
      // 插入执行节点信息
      ExecutionNode executionNode = new ExecutionNode();
      executionNode.setExecId(executionFlow.getId());
      executionNode.setName(flowNode.getName());
      executionNode.setAttempt(0);
      executionNode.setStartTime(new Date());
      executionNode.setStatus(FlowStatus.INIT);
      executionNode.setJobId(LoggerUtil.genJobId(JOB_PREFIX, executionFlow.getId(), flowNode.getName()));

      flowDao.insertExecutionNode(executionNode);

      // 插入执行队列
      executionNodes.add(executionNode);

      // 提交 jobrunner
      submitNodeRunner(flowNode, executionNode);

      while (!isAllFinished) {
        // 执行失败，直接结束
        if (!isSuccess) {
          break;
        }

        // 判断是否执行完毕
        try {
          isAllFinished = isFinished(flowNode);
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }

        // 执行失败，直接结束
        if (!isSuccess) {
          break;
        }

        // 如果没有执行完成，等待
        if (!isAllFinished) {
          try {
            synObject.wait(); // 等待执行完成
          } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
    }

    // 执行成功并且节点的数目和执行完成的节点数目相同
    if (isSuccess) {
      return FlowStatus.SUCCESS;
    }

    return FlowStatus.FAILED;
  }

  /**
   * 执行 workflow <p>
   *
   * @param dagGraph
   * @return {@link FlowStatus}
   */
  private FlowStatus runFlow(Graph<String, FlowNode, FlowNodeRelation> dagGraph) {
    // 获取拓扑排序列表
    List<String> topologicalSort;

    try {
      topologicalSort = dagGraph.topologicalSort();
    } catch (Exception e) {
      logger.error("Graph get topological failed.", e);
      return FlowStatus.FAILED;
    }

    // 节点数目
    int nodeSize = topologicalSort.isEmpty() ? 0 : topologicalSort.size();

    // 是否全部执行完成
    boolean isAllFinished = false;
    synchronized (synObject) {
      // 当存在节点未提交或者没有全部执行完成时，循环检测
      while (!topologicalSort.isEmpty() || !isAllFinished) {
        // 遍历一遍节点，找出可执行节点
        Iterator<String> iterator = topologicalSort.iterator();
        while (iterator.hasNext()) {
          String nodeName = iterator.next();

          // 支持恢复处理，
          // 当在恢复处理时该节点有可能已经运行成功了,此时跳过该节点处理
          // 其他情况，重新调度
          ExecutionNode executionNodeLog = flowDao.queryExecutionNode(executionFlow.getId(), nodeName);
          if (executionNodeLog != null && executionNodeLog.getStatus().typeIsSuccess()) {
            iterator.remove();
            finishedExecutionNodes.add(executionNodeLog);
            continue;
          }

          // 找到当前节点的所有前驱节点
          Set<String> preNodes = dagGraph.getPreNode(nodeName);
          boolean preNodesAllSuccess = isPreNodesAllSuccess(preNodes);

          // 全部执行成功
          if (preNodesAllSuccess) {
            // 插入执行节点信息
            FlowNode node = dagGraph.getVertex(nodeName);

            ExecutionNode executionNode = new ExecutionNode();
            executionNode.setExecId(executionFlow.getId());
            executionNode.setName(node.getName());
            executionNode.setAttempt(0);
            executionNode.setStartTime(new Date());
            executionNode.setStatus(FlowStatus.INIT);
            executionNode.setJobId(LoggerUtil.genJobId(JOB_PREFIX, executionFlow.getId(), node.getName()));

            // 如果是在恢复或者是长作业时，execution_nodes表中会存在记录，这里进行更新处理
            if (executionNodeLog != null) {
              flowDao.updateExecutionNode(executionNode);
            } else {
              flowDao.insertExecutionNode(executionNode);
            }

            // 插入执行队列
            executionNodes.add(executionNode);

            // 提交 jobrunner
            submitNodeRunner(node, executionNode);

            // 删除当前元素
            iterator.remove();
          } else { // 没有全部成功，看看是否已经有前驱节点失败（这里是指重试后仍失败）或者跳过
            boolean preNodesHasFailed = isPreNodesHasFailed(preNodes);
            // 如果前驱节点已经失败
            if (preNodesHasFailed) {
              // 如果失败的测试是结束 DAG ,那么设置状态是失败，并跳出当前循环
              if (failurePolicyType == FailurePolicyType.END) {
                isSuccess = false;
                break;
              } else { // 继续执行
                // 删除当前元素,节点加入跳过的队列
                iterator.remove();
                skipNodes.add(nodeName);
              }
            }
          }
        }

        // 执行失败，直接结束
        if (!isSuccess) {
          break;
        }

        // 判断是否执行完毕
        try {
          isAllFinished = isAllFinished(dagGraph);
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }

        // 执行失败，直接结束
        if (!isSuccess) {
          break;
        }

        // 如果没有执行完成，等待
        if (!isAllFinished) {
          try {
            synObject.wait(); // 等待执行完成
          } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
    }

    // 执行成功并且节点的数目和执行完成的节点数目相同
    if (isSuccess && nodeSize == finishedExecutionNodes.size()) {
      return FlowStatus.SUCCESS;
    }

    return FlowStatus.FAILED;
  }

  /**
   * 提交 NodeRunner 执行 <p>
   *
   * @param flowNode
   * @param executionNode
   */
  private void submitNodeRunner(FlowNode flowNode, ExecutionNode executionNode) {
    int nowTimeout = -1;

    if (!JobTypeManager.isLongJob(flowNode.getType())) {
      nowTimeout = calcNodeTimeout();
    }

    NodeRunner nodeRunner = new NodeRunner(executionFlow, executionNode, flowNode, jobExecutorService, synObject, nowTimeout, systemParamMap, customParamMap);
    Future<?> future = executorService.submit(nodeRunner);

    activeNodeRunners.add(nodeRunner);
  }

  /**
   * 计算节点的超时时间（s） <p>
   *
   * @return 超时时间
   */
  private int calcNodeTimeout() {
    int usedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);

    if (timeout <= usedTime) {
      throw new ExecTimeoutException(" workflow execution fetch time out");
    }

    return timeout - usedTime;
  }

  /**
   * 判断是否全部执行完成 <p>
   *
   * @param dagGraph
   * @return 是否全部执行完成
   */
  private boolean isAllFinished(Graph<String, FlowNode, FlowNodeRelation> dagGraph) {
    boolean isAllFinished = true; // 是否全部执行完成
    for (ExecutionNode executionNode : executionNodes) {
      FlowStatus status = executionNode.getStatus();
      if (status.typeIsSuccess()) {
        // 执行成功的情况：从执行队列删除，插入完成队列
        executionNodes.remove(executionNode);
        finishedExecutionNodes.add(executionNode);
      } else if (status.typeIsFinished()) {
        FlowNode node = dagGraph.getVertex(executionNode.getName());
        if (JobTypeManager.isLongJob(node.getType())) {
          // 长任务处理
          // 报错发送邮件，避免出现程序问题，一直重复调度
          logger.debug("exec id:{}, node:{} retry", executionNode.getExecId(), executionNode.getName());
          EmailManager.sendEmail(executionFlow, executionNode);
          executionNodes.remove(executionNode);
          reSubmitNodeRunner(node, executionNode);

          isAllFinished = false;
        } else {
          // 失败的情况：判断是否到达重试次数
          if (executionNode.getAttempt() < maxTryTimes) {
            // 从执行队列中删除
            // 插入一个重试的节点，提交一个重试的 jobrunner
            executionNodes.remove(executionNode);
            reSubmitNodeRunner(node, executionNode);

            isAllFinished = false;
          } else {
            logger.debug("exec id:{}, node:{} fetch max try times {}", executionNode.getExecId(), executionNode.getName(), maxTryTimes);
            // 达到最大重试次数，认为已经失败：从执行队列删除，插入完成队列
            executionNodes.remove(executionNode);
            finishedExecutionNodes.add(executionNode);

            // 如果失败后的策略是停止执行 DAG，那么修改 isSuccess
            if (failurePolicyType == FailurePolicyType.END) {
              isSuccess = false;
            }
          }
        }
      } else {
        // 没执行完成
        isAllFinished = false;
      }
    }

    return isAllFinished;
  }

  /**
   * 重新提交节点执行
   *
   * @param node
   * @param executionNode
   */
  private void reSubmitNodeRunner(FlowNode node, ExecutionNode executionNode) {
    ExecutionNode retryExecutionNode = new ExecutionNode();
    retryExecutionNode.setExecId(executionNode.getExecId());
    retryExecutionNode.setName(executionNode.getName());
    retryExecutionNode.setAttempt(executionNode.getAttempt() + 1);
    retryExecutionNode.setStartTime(new Date());
    retryExecutionNode.setStatus(FlowStatus.INIT);
    retryExecutionNode.setJobId(LoggerUtil.genJobId(JOB_PREFIX, executionFlow.getId(), executionNode.getName()));
    flowDao.updateExecutionNode(retryExecutionNode);
    executionNodes.add(retryExecutionNode);

    logger.debug("exec node:{} failed retrys:{}", executionNode.getName(), executionNode.getAttempt() + 1);

    submitNodeRunner(node, retryExecutionNode);
  }

  /**
   * 判断是否执行完成(单节点执行) <p>
   *
   * @param flowNode
   * @return 是否全部执行完成
   */
  private boolean isFinished(FlowNode flowNode) {
    boolean isAllFinished = true; // 是否全部执行完成
    for (ExecutionNode executionNode : executionNodes) {
      FlowStatus status = executionNode.getStatus();
      if (status.typeIsSuccess()) {
        // 执行成功的情况：从执行队列删除，插入完成队列
        executionNodes.remove(executionNode);
        finishedExecutionNodes.add(executionNode);
      } else if (status.typeIsFinished()) {
        // 失败的情况：判断是否到达重试次数
        if (executionNode.getAttempt() < maxTryTimes) {
          // 从执行队列中删除
          // 插入一个重试的节点，提交一个重试的 jobrunner
          executionNodes.remove(executionNode);

          ExecutionNode retryExecutionNode = new ExecutionNode();
          retryExecutionNode.setExecId(executionNode.getExecId());
          retryExecutionNode.setName(executionNode.getName());
          retryExecutionNode.setAttempt(executionNode.getAttempt() + 1);
          retryExecutionNode.setStartTime(new Date());
          retryExecutionNode.setStatus(FlowStatus.INIT);
          retryExecutionNode.setJobId(LoggerUtil.genJobId(JOB_PREFIX, executionFlow.getId(), executionNode.getName()));
          flowDao.updateExecutionNode(retryExecutionNode);
          executionNodes.add(retryExecutionNode);

          submitNodeRunner(flowNode, retryExecutionNode);

          isAllFinished = false;
        } else {
          // 达到最大重试次数，认为已经失败：从执行队列删除，插入完成队列
          executionNodes.remove(executionNode);
          finishedExecutionNodes.add(executionNode);

          // 如果失败后的策略是停止执行 DAG，那么修改 isSuccess
          if (failurePolicyType == FailurePolicyType.END) {
            isSuccess = false;
          }
        }
      } else {
        // 没执行完成
        isAllFinished = false;
      }
    }

    return isAllFinished;
  }

  /**
   * 判断前驱节点是否已经存在执行失败的情况 <p>
   *
   * @param preNodes
   * @return 是否存在失败
   */
  private boolean isPreNodesHasFailed(Set<String> preNodes) {
    for (String nodeName : preNodes) {
      // 节点已经跳过执行的情况
      if (skipNodes.contains(nodeName)) {
        return true;
      }
      ExecutionNode preFinishedNode = retrieveFinishedExecutionNode(nodeName);
      // 执行完成，并且失败的情况
      if (preFinishedNode != null && !preFinishedNode.getStatus().typeIsSuccess()) {
        return true;
      }
    }

    return false;
  }

  /**
   * 判断前驱节点是否都执行成功 <p>
   *
   * @param preNodes
   * @return 是否全部成功
   */
  private boolean isPreNodesAllSuccess(Set<String> preNodes) {
    // 没有前驱节点，认为全部执行成功
    if (CollectionUtils.isEmpty(preNodes)) {
      return true;
    }
    for (String nodeName : preNodes) {
      ExecutionNode preFinishedNode = retrieveFinishedExecutionNode(nodeName);
      // 未执行完成或者执行失败的情况
      if (preFinishedNode == null || !preFinishedNode.getStatus().typeIsSuccess()) {
        return false;
      }
    }

    return true;
  }

  /**
   * 取回节点执行完成的执行信息(缓存) <p>
   *
   * @param nodeName
   * @return {@link ExecutionNode}
   */
  private ExecutionNode retrieveFinishedExecutionNode(String nodeName) {
    for (ExecutionNode executionNode : finishedExecutionNodes) {
      if (ObjectUtils.equals(nodeName, executionNode.getName())) {
        return executionNode;
      }
    }
    return null;
  }

  /**
   * 更新 ExecutionFlow <p>
   *
   * @param status
   */
  private void updateExecutionFlow(FlowStatus status) {
    executionFlow.setEndTime(new Date());
    executionFlow.setStatus(status);
    flowDao.updateExecutionFlow(executionFlow);
  }

  /**
   * 取消所有没有执行完成的作业 <p>
   */
  private void cancelAllExectingNode() {
    for (ExecutionNode executionNode : executionNodes) {
      if (!executionNode.getStatus().typeIsFinished()) {
        // 异步线程本身有超时设置，这里不再取消
        /*
         * if (nodeFutureMap.containsKey(executionNode)) { Future<?>
         * future = nodeFutureMap.get(executionNode); if
         * (!future.isDone()) { future.cancel(true); // 取消执行线程 } }
         */
        // 修改执行节点的状态
        executionNode.setStatus(FlowStatus.KILL);
        executionNode.setEndTime(new Date());
        flowDao.updateExecutionNode(executionNode);
      }
    }
  }

  /**
   * flow执行完的后置处理 <p>
   */
  private void after() {
    ExecType flowRunType = executionFlow.getType();
    // 调度任务的发邮件处理
    if (flowRunType == ExecType.SCHEDULER) {
      // 发送邮件
      try {
        sendEmail();
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  /**
   * 发送邮件 <p>
   */
  private void sendEmail() {
    // 发送邮件
    if ((executionFlow.getStatus().typeIsSuccess() && executionFlow.getNotifyType().typeIsSendSuccessMail())
        || (executionFlow.getStatus().typeIsFailure() && executionFlow.getNotifyType().typeIsSendFailureMail())) {
      EmailManager.sendEmail(executionFlow);
    }
  }

  /**
   * kill 执行
   *
   * @param user
   */
  public void kill(String user) {
    synchronized (synObject) {
      logger.info("Flow killed by " + user);
      kill();
      updateExecutionFlow(FlowStatus.KILL);
    }
  }

  /**
   * kill 执行
   */
  public void kill() {
    synchronized (synObject) {
      logger.info("Kill has been called on exec:" + executionFlow.getId());
      logger.info("Killing running nodes, num:" + activeNodeRunners.size());

      for (NodeRunner nodeRunner : activeNodeRunners) {
        nodeRunner.kill();
      }
    }
  }
}
