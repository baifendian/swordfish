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
import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.mapper.*;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.model.flow.FlowDag;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FlowDao extends BaseDao {
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

  public FlowNode queryNodeInfo(Integer nodeId) {
    FlowNode flowNode = flowNodeMapper.selectByNodeId(nodeId);
    return flowNode;
  }

  /**
   * 获取 flow 执行详情 <p>
   *
   * @return {@link ExecutionFlow}
   */
  public ExecutionFlow queryExecutionFlow(int execId) {
    return executionFlowMapper.selectByExecId(execId);
  }

  /**
   * 获取所有未完成的 flow 列表 <p>
   */
  public List<ExecutionFlow> queryAllNoFinishFlow() {
    return executionFlowMapper.selectAllNoFinishFlow();
  }

  /**
   * 获取execserver没有运行完成的workflow
   */
  public List<ExecutionFlow> queryNoFinishFlow(String worker) {
    return executionFlowMapper.selectNoFinishFlow(worker);
  }

  /**
   * 按时间段查询 flow 的调度的最新运行状态(调度或者补数据) <p>
   *
   * @return List<{@link ExecutionFlow}>
   */
  public List<ExecutionFlow> queryFlowLastStatus(Integer flowId, Date startDate, Date endDate) {
    return executionFlowMapper.selectByFlowIdAndTimes(flowId, startDate, endDate);
  }

  /**
   * 按时间查询 flow 的调度的最新运行状态(调度或者补数据) <p>
   *
   * @return List<{@link ExecutionFlow}>
   */
  public List<ExecutionFlow> queryFlowLastStatus(int flowId, Date scheduleTime) {
    return executionFlowMapper.selectByFlowIdAndTime(flowId, scheduleTime);
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

    // add by qifeng.dai, 如果结束了, 则应该设置结束时间
    if (status.typeIsFinished()) {
      executionFlow.setEndTime(new Date());
    }

    return executionFlowMapper.update(executionFlow) > 0;
  }

  public boolean updateExecutionFlowStatus(int execId, FlowStatus status, String worker) {
    ExecutionFlow executionFlow = new ExecutionFlow();
    executionFlow.setId(execId);
    executionFlow.setStatus(status);
    executionFlow.setWorker(worker);

    if (status.typeIsFinished()) {
      executionFlow.setEndTime(new Date());
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
   * @return {@link ExecutionFlow}
   */
  public ExecutionFlow scheduleFlowToExecution(Integer projectId, Integer workflowId, int submitUser, Date scheduleTime, ExecType runType,
                                               Integer maxTryTimes, String nodeName, NodeDepType nodeDep, NotifyType notifyType, List<String> mails, int timeout) {
    List<FlowNode> flowNodes = flowNodeMapper.selectByFlowId(workflowId); // 节点信息
    List<FlowNodeRelation> flowNodeRelations = new ArrayList<>();

    for (FlowNode flowNode : flowNodes) {
      String dep = flowNode.getDep();
      if (StringUtils.isNotEmpty(dep)) {
        List<String> depList = JsonUtil.parseObjectList(dep, String.class);
        for (String depNode : depList) {
          flowNodeRelations.add(new FlowNodeRelation(workflowId, depNode, flowNode.getName()));
        }
      }
    }

    ProjectFlow projectFlow = projectFlowMapper.findById(projectId);
    FlowDag flowDag = new FlowDag();
    flowDag.setEdges(flowNodeRelations);
    flowDag.setNodes(flowNodes);

    // TODO:: 处理邮件的字段信息, 以及工作流节点的信息
    ExecutionFlow executionFlow = new ExecutionFlow();

    executionFlow.setFlowId(workflowId);
    executionFlow.setSubmitUserId(submitUser);
    executionFlow.setSubmitTime(new Date());
    executionFlow.setQueue(projectFlow.getQueue());
    executionFlow.setProxyUser(projectFlow.getProxyUser());
    executionFlow.setScheduleTime(scheduleTime);
    executionFlow.setStartTime(new Date());
    executionFlow.setWorkflowData(JsonUtil.toJsonString(flowDag));
    executionFlow.setUserDefinedParams(projectFlow.getUserDefinedParams());
    executionFlow.setType(runType);
    executionFlow.setMaxTryTimes(maxTryTimes);
    executionFlow.setTimeout(timeout);
    executionFlow.setStatus(FlowStatus.INIT);
    executionFlow.setExtras(projectFlow.getExtras());

    executionFlowMapper.insertAndGetId(executionFlow); // 插入执行信息

    return executionFlow;
  }

  /**
   * 创建工作流 创建的工作流已有发布时间
   *
   * @return workflowId
   */
  public int createWorkflow(int projectId, String name, FlowType type, int userId) throws Exception {
    ProjectFlow projectFlow = new ProjectFlow();

    projectFlow.setName(name);
    projectFlow.setCreateTime(new Date());
    projectFlow.setModifyTime(new Date());
    projectFlow.setOwnerId(userId);
    projectFlow.setProjectId(projectId);

    int count = projectFlowMapper.insertAndGetId(projectFlow); // 插入函数记录
    if (count <= 0) {
      throw new Exception("工作流插入失败");
    }
    return projectFlow.getId();
  }

  /**
   * 删除工作流、及其节点、和调度配置
   */
  @Transactional(value = "TransactionManager")
  public void deleteWorkflow(int workflowId) {
    projectFlowMapper.deleteById(workflowId);
    flowNodeMapper.deleteByFlowId(workflowId);
    // flowNodePubMapper.deleteByFlowId(workflowId);
    // scheduleMapper.deleteByFlowId(workflowId);
  }

  /**
   * 查询工作流已发布的节点
   */
  public List<FlowNode> queryWorkflowNodes(int workflowId) {
    List<FlowNode> flowNodes = flowNodeMapper.selectByFlowId(workflowId);
    return flowNodes;
  }


  /**
   * 配置调度信息
   */
  /*
  public void configSchedule(int workflowId, int userId, String time) throws Exception {
    Schedule schedule = scheduleMapper.selectByFlowId(workflowId);
    if (schedule == null) {
      // 插入调度信息
      schedule = new Schedule();

      schedule.setFlowId(workflowId);
      schedule.setTimeout(10);
      schedule.setPubStatus(PubStatus.END);
      schedule.setScheduleStatus(ScheduleStatus.ONLINE);
      schedule.setLastModifyBy(userId);
      schedule.setFailureEmails(Boolean.TRUE);
      schedule.setSuccessEmails(Boolean.FALSE);
      schedule.setMaxTryTimes(2);
      schedule.setFailurePolicy(FailurePolicyType.CONTINUE);

      Date now = new Date();
      schedule.setCreateTime(now);
      schedule.setModifyTime(now);

      // 调度时间
      ScheduleMeta meta = new ScheduleMeta();
      meta.setType(ScheduleType.DAY);
      meta.setStartDate(now);
      schedule.setStartDate(now);
      Date endDate = BFDDateUtils.parse("2099-12-31 00:00:00");
      meta.setEndDate(endDate);
      schedule.setEndDate(endDate);
      schedule.setCrontabStr(JsonUtil.toJsonString(meta));

      int count = scheduleMapper.insert(schedule);
      if (count <= 0) {
        throw new Exception("插入失败");
      }

    } else {
      // 更新调度信息
      ScheduleMeta meta = JsonUtil.parseObject(schedule.getCrontabStr(), ScheduleMeta.class);
      schedule.setCrontabStr(JsonUtil.toJsonString(meta));

      schedule.setLastModifyBy(userId);
      schedule.setModifyTime(new Date());
      int count = scheduleMapper.update(schedule);
      if (count <= 0) {
        throw new Exception("更新失败");
      }
    }
  }
  */

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
    // 更新执行节点信息
    executionNodeMapper.update(executionNode);
  }

  public ExecutionNode queryExecutionNode(long execId, String nodeName) {
    return executionNodeMapper.selectExecNode(execId, nodeName);
  }

  /**
   * 查询 Schedule <p>
   *
   * @return {@link Schedule}
   */
  public Schedule querySchedule(int flowId) {
    // 插入执行节点信息
    return scheduleMapper.selectByFlowId(flowId);
  }

  /**
   * 根据name获取一个工作流的信息
   *
   * @param projectId
   * @param name
   * @return
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
   * 根据Id获取一个workflow
   *
   * @param id
   * @return
   */
  public ProjectFlow projectFlowfindById(int id) {
    ProjectFlow projectFlow = projectFlowMapper.findById(id);
    if (projectFlow != null) {
      List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowId(projectFlow.getId());
      projectFlow.setFlowsNodes(flowNodeList);
    }
    return projectFlow;
  }

  /**
   * 创建一个projectFlow
   *
   * @return
   */
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public void createProjectFlow(ProjectFlow projectFlow) {
    projectFlowMapper.insertAndGetId(projectFlow);
    for (FlowNode flowNode : projectFlow.getFlowsNodes()) {
      flowNode.setFlowId(projectFlow.getId());
      flowNodeMapper.insert(flowNode);
    }
    projectFlow.getData().setNodes(projectFlow.getFlowsNodes());
  }

  /**
   * 修改一个工作流
   *
   * @param projectFlow
   */
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public void modifyProjectFlow(ProjectFlow projectFlow) {
    flowNodeMapper.deleteByFlowId(projectFlow.getId());
    for (FlowNode flowNode : projectFlow.getFlowsNodes()) {
      //重新设置id保证唯一性
      flowNode.setFlowId(projectFlow.getId());
      flowNodeMapper.insert(flowNode);
    }
    projectFlowMapper.updateById(projectFlow);
  }

  /**
   * 根据项目名和工作流名称查询
   *
   * @param projectName
   * @param name
   * @return
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
   * @param projectId
   * @return
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

}
