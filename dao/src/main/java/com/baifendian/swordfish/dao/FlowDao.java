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

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.mapper.*;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.model.flow.FlowDag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
  private FlowNodeRelationMapper flowNodeRelationMapper;

  @Autowired
  private ScheduleMapper scheduleMapper;

  @Autowired
  private ExecutionNodeMapper executionNodeMapper;

  @Override
  protected void init() {
    executionFlowMapper = ConnectionFactory.getSqlSession().getMapper(ExecutionFlowMapper.class);
    projectFlowMapper = ConnectionFactory.getSqlSession().getMapper(ProjectFlowMapper.class);
    flowNodeMapper = ConnectionFactory.getSqlSession().getMapper(FlowNodeMapper.class);
    flowNodeRelationMapper = ConnectionFactory.getSqlSession().getMapper(FlowNodeRelationMapper.class);
    scheduleMapper = ConnectionFactory.getSqlSession().getMapper(ScheduleMapper.class);
    executionNodeMapper = ConnectionFactory.getSqlSession().getMapper(ExecutionNodeMapper.class);
  }

  /**
   * @param nodeId
   * @return
   */
  public FlowNode queryNodeInfo(Integer nodeId) {
    FlowNode flowNode = flowNodeMapper.selectByNodeId(nodeId);
    return flowNode;
  }

  /**
   * 获取 flow 执行详情 <p>
   *
   * @return {@link ExecutionFlow}
   */
  public ExecutionFlow queryExecutionFlow(long execId) {
    return executionFlowMapper.selectByExecId(execId);
  }

  /**
   * 获取所有未完成的 flow 列表 <p>
   */
  public List<ExecutionFlow> queryAllNoFinishFlow() {
    return executionFlowMapper.selectNoFinishFlow();
  }

  /**
   * 获取execserver正在运行的workflow
   */
  public List<ExecutionFlow> queryRunningFlow(String worker) {
    return executionFlowMapper.selectRunningFlow(worker);
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
  public boolean updateExecutionFlowStatus(long execId, FlowStatus status) {
    ExecutionFlow executionFlow = new ExecutionFlow();
    executionFlow.setId(execId);
    executionFlow.setStatus(status);

    // add by qifeng.dai, 如果结束了, 则应该设置结束时间
    if (status.typeIsFinished()) {
      executionFlow.setEndTime(new Date());
    }

    return executionFlowMapper.update(executionFlow) > 0;
  }

  public boolean updateExecutionFlowStatus(long execId, FlowStatus status, String worker) {
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
   * 获取 workflow 详情 <p>
   *
   * @return {@link ProjectFlow}
   */
  public ProjectFlow queryFlow(Integer workflowId) {
    return projectFlowMapper.findById(workflowId);
  }

  /**
   * 执行 workflow 时，插入执行信息 <p>
   *
   * @return {@link ExecutionFlow}
   */
  public ExecutionFlow runFlowToExecution(Integer projectId, Integer workflowId) {
    ProjectFlow projectFlow = projectFlowMapper.findById(workflowId);
    List<FlowNodeRelation> flowNodeRelations = flowNodeRelationMapper.selectByFlowId(workflowId); // 边信息
    List<FlowNode> flowNodes = flowNodeMapper.selectByFlowId(workflowId); // 节点信息
    FlowDag flowDag = new FlowDag();
    flowDag.setEdges(flowNodeRelations);
    flowDag.setNodes(flowNodes);

    ExecutionFlow executionFlow = new ExecutionFlow();
    executionFlow.setProjectId(projectId);
    executionFlow.setFlowId(workflowId);
    executionFlow.setSubmitUser(projectFlow.getOwnerId());
    executionFlow.setSubmitTime(new Date());
    executionFlow.setStartTime(new Date());
    executionFlow.setType(FlowRunType.DIRECT_RUN);
    executionFlow.setStatus(FlowStatus.INIT);
    executionFlow.setWorkflowData(JsonUtil.toJsonString(flowDag));

    executionFlowMapper.insertAndGetId(executionFlow); // 插入执行信息

    return executionFlow;
  }

  /**
   * 调度 workflow 时，插入执行信息（调度或者补数据） <p>
   *
   * @return {@link ExecutionFlow}
   */
  public ExecutionFlow scheduleFlowToExecution(Integer projectId, Integer workflowId, int submitUser, Date scheduleTime, FlowRunType runType) {
    List<FlowNodeRelation> flowNodeRelations = flowNodeRelationMapper.selectByFlowId(workflowId); // 边信息
    List<FlowNode> flowNodes = flowNodeMapper.selectByFlowId(workflowId); // 节点信息
    ProjectFlow projectFlow = projectFlowMapper.findById(workflowId);
    FlowDag flowDag = new FlowDag();
    flowDag.setEdges(flowNodeRelations);
    flowDag.setNodes(flowNodes);

    ExecutionFlow executionFlow = new ExecutionFlow();
    executionFlow.setFlowId(workflowId);
    executionFlow.setSubmitUser(submitUser);
    executionFlow.setProxyUser(projectFlow.getProxyUser());
    executionFlow.setScheduleTime(scheduleTime);
    executionFlow.setSubmitTime(new Date());
    executionFlow.setStartTime(new Date());
    executionFlow.setType(runType);
    executionFlow.setStatus(FlowStatus.INIT);
    executionFlow.setWorkflowData(JsonUtil.toJsonString(flowDag));

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
    projectFlow.setLastModifyBy(userId);
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
   * 创建节点
   */
  @Transactional(value = "TransactionManager")
  public void createNode(int projectId, int workflowId, String nodeType, int userId, String name) throws Exception {
    FlowNode flowNode = new FlowNode();

    flowNode.setName(name);
    flowNode.setFlowId(workflowId);
    flowNode.setType(nodeType);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    flowNode.setCreateTime(timestamp);
    flowNode.setModifyTime(timestamp);
    flowNode.setLastModifyBy(userId);
    int count = flowNodeMapper.insert(flowNode);
    if (count <= 0) {
      throw new Exception("插入节点失败");
    }
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

  /**
   * 获取 node 执行详情 <p>
   *
   * @return {@link ExecutionNode}
   */
  public ExecutionNode queryExecutionNode(long execId, Integer nodeId, Integer attempt) {
    return executionNodeMapper.selectOneExecNode(execId, nodeId, attempt);
  }

  public ExecutionNode queryExecutionNodeLastAttempt(long execId, Integer nodeId) {
    return executionNodeMapper.selectExecNodeLastAttempt(execId, nodeId);
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
}
