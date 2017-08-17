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
package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NodeDepType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.ExecutionFlowMapper;
import com.baifendian.swordfish.dao.mapper.ExecutionNodeMapper;
import com.baifendian.swordfish.dao.mapper.FlowNodeMapper;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mapper.ScheduleMapper;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.FlowNodeRelation;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.flow.FlowDag;
import com.baifendian.swordfish.dao.utils.DagHelper;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FlowDao extends BaseDao {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ExecutionFlowMapper executionFlowMapper;

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private FlowNodeMapper flowNodeMapper;

  @Autowired
  private ScheduleMapper scheduleMapper;

  @Autowired
  private ExecutionNodeMapper executionNodeMapper;

  @Override
  protected void init() {
    executionFlowMapper = ConnectionFactory.getSqlSession().getMapper(ExecutionFlowMapper.class);
    projectFlowMapper = ConnectionFactory.getSqlSession().getMapper(ProjectFlowMapper.class);
    flowNodeMapper = ConnectionFactory.getSqlSession().getMapper(FlowNodeMapper.class);
    scheduleMapper = ConnectionFactory.getSqlSession().getMapper(ScheduleMapper.class);
    executionNodeMapper = ConnectionFactory.getSqlSession().getMapper(ExecutionNodeMapper.class);
  }

  /**
   * 获取 flow 执行详情 <p>
   *
   * @see ExecutionFlow
   */
  public ExecutionFlow queryExecutionFlow(int execId) {
    return executionFlowMapper.selectByExecId(execId);
  }

  /**
   * 删除执行的结点
   */
  public int deleteExecutionNodes(int execId) {
    return executionNodeMapper.deleteExecutionNodes(execId);
  }

  /**
   * 获取所有未完成的 flow 列表 <p>
   *
   * @see ExecutionFlow
   */
  public List<ExecutionFlow> queryAllNoFinishFlow() {
    return executionFlowMapper.selectAllNoFinishFlow();
  }

  /**
   * 获取 exec server 没有运行完成的 workflow
   *
   * @see ExecutionFlow
   */
  public List<ExecutionFlow> queryNoFinishFlow(String worker) {
    return executionFlowMapper.selectNoFinishFlow(worker);
  }

  /**
   * 根据具体的scheduleTime获取一个调度的执行记录
   */
  public ExecutionFlow queryExecutionFlowByScheduleTime(int flowId, Date scheduleTime) {
    return executionFlowMapper.selectExecutionFlowByScheduleTime(flowId, scheduleTime);
  }

  /**
   * 更新 flow 执行状态 <p>
   *
   * @return 是否成功
   */
  public boolean updateExecutionFlowStatus(int execId, FlowStatus status) {
    ExecutionFlow executionFlow = new ExecutionFlow();
    executionFlow.setId(execId);
    executionFlow.setStatus(status);

    // 如果结束了, 则应该设置结束时间
    Date now = new Date();

    if (status.typeIsFinished()) {
      executionFlow.setEndTime(now);
    }

    return executionFlowMapper.update(executionFlow) > 0;
  }

  /**
   * 更新执行工作流的状态
   */
  public boolean updateExecutionFlowStatus(int execId, FlowStatus status, String worker) {
    ExecutionFlow executionFlow = new ExecutionFlow();
    executionFlow.setId(execId);
    executionFlow.setStatus(status);
    executionFlow.setWorker(worker);

    Date now = new Date();

    if (status.typeIsFinished()) {
      executionFlow.setEndTime(now);
    }

    return executionFlowMapper.update(executionFlow) > 0;
  }

  /**
   * 更新 flow 执行详情 <p>
   *
   * @return 是否成功
   */
  public boolean updateExecutionFlow(ExecutionFlow executionFlow) {
    return executionFlowMapper.update(executionFlow) > 0;
  }

  /**
   * 调度 workflow 时，插入执行信息（调度或者补数据） <p>
   *
   * @param scheduleTime : 这个时间在不同的场景, 有不同的解释, 直接运行指接受到的时间, 调度和补数据 则是 fire time(定时器给的时间)
   */
  public ExecutionFlow scheduleFlowToExecution(Integer projectId,
      Integer workflowId,
      int submitUser,
      Date scheduleTime,
      ExecType runType,
      FailurePolicyType failurePolicyType,
      Integer maxTryTimes,
      String nodeName,
      NodeDepType nodeDep,
      NotifyType notifyType,
      List<String> mails,
      int timeout) throws Exception {
    logger.info("project id:{}, workflow id:{}, schedule time:{}", projectId, workflowId,
        scheduleTime);

    // 查询工作流的节点信息
    List<FlowNode> flowNodes = flowNodeMapper.selectByFlowId(workflowId);

    // 结点关系信息
    List<FlowNodeRelation> flowNodeRelations = new ArrayList<>();

    // 遍历节点信息, 构建关系
    for (FlowNode flowNode : flowNodes) {
      String dep = flowNode.getDep();
      List<String> depList = JsonUtil.parseObjectList(dep, String.class);
      // 如果依赖不为空
      if (depList != null) {
        for (String depNode : depList) {
          flowNodeRelations.add(new FlowNodeRelation(depNode, flowNode.getName()));
        }
      }
    }

    // 查询项目的工作流信息
    ProjectFlow projectFlow = projectFlowMapper.findById(workflowId);

    FlowDag flowDag = new FlowDag();
    flowDag.setEdges(flowNodeRelations);
    flowDag.setNodes(flowNodes);

    // 处理邮件的字段信息, 以及工作流节点的信息
    ExecutionFlow executionFlow = new ExecutionFlow();

    Date now = new Date();

    executionFlow.setFlowId(workflowId);
    executionFlow.setSubmitUserId(submitUser);
    executionFlow.setSubmitTime(now); // 即插入任务的时间
    executionFlow.setQueue(projectFlow.getQueue());
    executionFlow.setProxyUser(projectFlow.getProxyUser());
    executionFlow.setScheduleTime(scheduleTime);
    executionFlow.setStartTime(now); // 即插入任务的时间

    // 如果 node 名称为空, 表示是执行整个节点
    if (nodeName != null) {
      FlowNode flowNode = DagHelper.findNodeByName(flowNodes, nodeName);
      if (flowNode != null) {
        switch (nodeDep) {
          case NODE_POST:
            flowDag = DagHelper.findNodeDepDag(flowDag, flowNode, true);
            break;
          case NODE_PRE:
            flowDag = DagHelper.findNodeDepDag(flowDag, flowNode, false);
            break;
          case NODE_ONLY:
          default:
            flowDag.setEdges(null);
            flowDag.setNodes(Arrays.asList(flowNode));
        }
      } else {
        throw new Exception(String.format("node %s not found in flow %d", nodeName, workflowId));
      }
    }

    executionFlow.setWorkflowData(JsonUtil.toJsonString(flowDag));
    executionFlow.setUserDefinedParams(projectFlow.getUserDefinedParams());
    executionFlow.setType(runType);
    executionFlow.setFailurePolicy(failurePolicyType);
    executionFlow.setMaxTryTimes(maxTryTimes);
    executionFlow.setNotifyType(notifyType);
    executionFlow.setNotifyMailList(mails);
    executionFlow.setTimeout(timeout);
    executionFlow.setStatus(FlowStatus.INIT);
    executionFlow.setExtras(projectFlow.getExtras());

    // 插入执行信息
    executionFlowMapper.insertAndGetId(executionFlow);

    return executionFlow;
  }

  /**
   * 删除工作流、会自动级联删除 "工作流节点/调度配置/日志级联信息"
   */
  @Transactional(value = "TransactionManager")
  public void deleteWorkflow(int workflowId) {
    projectFlowMapper.deleteById(workflowId);
  }

  /**
   * 插入 ExecutionNode <p>
   */
  public void insertExecutionNode(ExecutionNode executionNode) {
    // 插入执行节点信息
    executionNodeMapper.insert(executionNode);
  }

  /**
   * 更新 ExecutionNode <p>
   */
  public void updateExecutionNode(ExecutionNode executionNode) {
    executionNodeMapper.update(executionNode);
  }

  /**
   * 查询执行节点信息
   */
  public ExecutionNode queryExecutionNode(long execId, String nodeName) {
    return executionNodeMapper.selectExecNode(execId, nodeName);
  }

  /**
   * 查询 Schedule <p>
   *
   * @see Schedule
   */
  public Schedule querySchedule(int flowId) {
    return scheduleMapper.selectByFlowId(flowId);
  }

  /**
   * 根据 name 获取一个工作流的信息
   *
   * @see ProjectFlow
   */
  public ProjectFlow projectFlowfindByName(int projectId, String name) {
    ProjectFlow projectFlow = projectFlowMapper.findByName(projectId, name);

    if (projectFlow != null) {
      List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowId(projectFlow.getId());
      projectFlow.setFlowsNodes(flowNodeList);
    }

    return projectFlow;
  }

  /**
   * 根据 Id 获取一个 workflow
   *
   * @see ProjectFlow
   */
  public ProjectFlow projectFlowFindById(int id) {
    ProjectFlow projectFlow = projectFlowMapper.findById(id);

    if (projectFlow != null) {
      List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowId(projectFlow.getId());
      projectFlow.setFlowsNodes(flowNodeList);
    }

    return projectFlow;
  }

  /**
   * 创建一个 projectFlow, 会操作 project_flows 和 flows_nodes 表
   */
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public void createProjectFlow(ProjectFlow projectFlow) {
    // 插入项目的工作流表
    projectFlowMapper.insertAndGetId(projectFlow);

    // 插入工作流的节点表
    for (FlowNode flowNode : projectFlow.getFlowsNodes()) {
      flowNode.setFlowId(projectFlow.getId());
      flowNodeMapper.insert(flowNode);
    }
  }

  /**
   * 修改一个工作流
   */
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public void modifyProjectFlow(ProjectFlow projectFlow) {
    flowNodeMapper.deleteByFlowId(projectFlow.getId());

    for (FlowNode flowNode : projectFlow.getFlowsNodes()) {
      // 重新设置 id 保证唯一性
      flowNode.setFlowId(projectFlow.getId());
      flowNodeMapper.insert(flowNode);
    }

    projectFlowMapper.updateById(projectFlow);
  }

  /**
   * 根据项目名和工作流名称查询
   *
   * @see ProjectFlow
   */
  public ProjectFlow projectFlowFindByPorjectNameAndName(String projectName, String name) {
    ProjectFlow projectFlow = projectFlowMapper.findByProjectNameAndName(projectName, name);

    if (projectFlow != null) {
      List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowId(projectFlow.getId());
      projectFlow.setFlowsNodes(flowNodeList);
    }

    return projectFlow;
  }

  /**
   * 获取一个项目下所有的工作流
   *
   * @see ProjectFlow
   */
  public List<ProjectFlow> projectFlowFindByProject(int projectId) {
    List<ProjectFlow> projectFlowList = projectFlowMapper.findByProject(projectId);
    List<Integer> flowIds = new ArrayList<>();

    for (ProjectFlow projectFlow : projectFlowList) {
      flowIds.add(projectFlow.getId());
    }

    List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowIds(flowIds);
    for (ProjectFlow projectFlow : projectFlowList) {
      List<FlowNode> flowNodes = new ArrayList<>();
      for (FlowNode flowNode : flowNodeList) {
        if (flowNode.getFlowId() == projectFlow.getId()) {
          flowNodes.add(flowNode);
        }
      }

      projectFlow.setFlowsNodes(flowNodes);
    }

    return projectFlowList;
  }

  /**
   * 获取参照时间的前一个调度结果
   */
  public ExecutionFlow executionFlowPreDate(int flowId, Date date) {
    return executionFlowMapper.selectPreDate(flowId, date);
  }

  public ExecutionFlow executionFlowByStartTimeAndScheduleTime(int flowId, Date startTIme,
      Date scheduleTime) {
    return executionFlowMapper.selectByStartTimeAndScheduleTime(flowId, startTIme, scheduleTime);
  }
}
