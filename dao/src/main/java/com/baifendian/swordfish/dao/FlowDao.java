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

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
//import com.baifendian.swordfish.dao.etl.EtlAutoGen;
import com.baifendian.swordfish.execserver.utils.hadoop.hdfs.HdfsPathManager;
import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.enums.*;
import com.baifendian.swordfish.dao.mysql.mapper.*;
import com.baifendian.swordfish.dao.mysql.model.*;
import com.baifendian.swordfish.dao.mysql.model.flow.FlowDag;
import com.baifendian.swordfish.dao.mysql.model.flow.ScheduleMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Workflow 相关操作
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月27日
 */
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

    @Autowired
    private ExecNodeLogMapper execNodeLogMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    //private EtlAutoGen etlAutoGen;

    @Override
    protected void init() {
        executionFlowMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ExecutionFlowMapper.class);
        projectFlowMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ProjectFlowMapper.class);
        flowNodeMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(FlowNodeMapper.class);
        flowNodeRelationMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(FlowNodeRelationMapper.class);
        scheduleMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ScheduleMapper.class);
        executionNodeMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ExecutionNodeMapper.class);
        execNodeLogMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ExecNodeLogMapper.class);
        resourceMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ResourceMapper.class);
        //etlAutoGen = new EtlAutoGen();
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
     * 获取 flow 执行详情
     * <p>
     *
     * @param execId
     * @return {@link ExecutionFlow}
     */
    public ExecutionFlow queryExecutionFlow(long execId) {
        return executionFlowMapper.selectByExecId(execId);
    }

    /**
     * 获取所有未完成的 flow 列表
     * <p>
     *
     * @return
     */
    public List<ExecutionFlow> queryAllNoFinishFlow() {
        return executionFlowMapper.selectNoFinishFlow();
    }

    /**
     * 按时间段查询 flow 的调度的最新运行状态(调度或者补数据)
     * <p>
     *
     * @param flowId
     * @param startDate
     * @param endDate
     * @return List<{@link ExecutionFlow}>
     */
    public List<ExecutionFlow> queryFlowLastStatus(Integer flowId, Date startDate, Date endDate) {
        return executionFlowMapper.selectByFlowIdAndTimes(flowId, startDate, endDate);
    }

    /**
     * 按时间查询 flow 的调度的最新运行状态(调度或者补数据)
     * <p>
     *
     * @param flowId
     * @param scheduleTime
     * @return List<{@link ExecutionFlow}>
     */
    public List<ExecutionFlow> queryFlowLastStatus(int flowId, Date scheduleTime) {
        return executionFlowMapper.selectByFlowIdAndTime(flowId, scheduleTime);
    }

    /**
     * 更新 flow 执行状态
     * <p>
     *
     * @param execId
     * @param status
     * @return 是否成功
     */
    public boolean updateExecutionFlowStatus(long execId, FlowStatus status) {
        ExecutionFlow executionFlow = new ExecutionFlow();
        executionFlow.setId(execId);
        executionFlow.setStatus(status);

        // add by qifeng.dai, 如果结束了, 则应该设置结束时间
        if(status.typeIsFinished()) {
            executionFlow.setEndTime(new Date());
        }

        return executionFlowMapper.update(executionFlow) > 0;
    }

    public boolean updateExecutionFlowStatus(long execId, FlowStatus status, String worker) {
        ExecutionFlow executionFlow = new ExecutionFlow();
        executionFlow.setId(execId);
        executionFlow.setStatus(status);
        executionFlow.setWorker(worker);

        if(status.typeIsFinished()) {
            executionFlow.setEndTime(new Date());
        }

        return executionFlowMapper.update(executionFlow) > 0;
    }

    /**
     * 更新 flow 执行详情
     * <p>
     *
     * @param executionFlow
     * @return 是否成功
     */
    public boolean updateExecutionFlow(ExecutionFlow executionFlow) {
        return executionFlowMapper.update(executionFlow) > 0;
    }

    /**
     * 获取 workflow 详情
     * <p>
     *
     * @param workflowId
     * @return {@link ProjectFlow}
     */
    public ProjectFlow queryFlow(Integer workflowId) {
        return projectFlowMapper.findById(workflowId);
    }

    /**
     * 查询项目中的所有的 sql
     * <p>
     *
     * @param projectId
     * @return sql列表
     */
    /*
    public List<String> queryAllSqlFromProject(int projectId) {
        List<String> sqlList = new ArrayList<>();

        // 1. 获取普通 ETL SQL 节点
        List<FlowNode> flowNodes = flowNodeMapper.selectAllNodes(projectId, NodeType.SQL);
        for (FlowNode flowNode : flowNodes) {
            if (flowNode.getParamObject() != null) {
                String sqls = ((SqlParam) flowNode.getParamObject()).getValue();
                if (StringUtils.isNotEmpty(sqls)) {
                    sqls = ParamUtil.resolvePlaceholdersConst(sqls, "NULL");
                    sqlList.add(sqls);
                }
            }
        }

        // 2. 获取图形化 ETL 节点
        List<ProjectFlow> projectFlows = projectFlowMapper.queryFlowsByProjectId(projectId, FlowType.ETL);
        for (ProjectFlow projectFlow : projectFlows) {
            int workflowId = projectFlow.getId();
            List<FlowNode> nodes = flowNodePubMapper.selectByFlowId(workflowId); // 节点信息
            // 有发布节点的情况
            if (CollectionUtils.isNotEmpty(nodes)) {
                List<FlowNodeRelation> relations = flowNodePublishRelationMapper.selectByFlowId(workflowId); // 边信息
                Pair<EtlStatus, MessageObj> result = etlAutoGen.autoCodeGen(relations, nodes, null);
                if (result.getKey() == EtlStatus.OK) {
                    String sqls = result.getValue().getCode();
                    sqls = PlaceholderUtil.resolvePlaceholdersConst(sqls, "NULL");
                    sqlList.add(sqls);
                }
            }
        }
        return sqlList;
    }
    */

    /**
     * 执行 workflow 时，插入执行信息
     * <p>
     *
     * @param projectId
     * @param workflowId
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
     * 调度 workflow 时，插入执行信息（调度或者补数据）
     * <p>
     *
     * @param projectId
     * @param workflowId
     * @param submitUser
     * @param scheduleTime
     * @param runType
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
        projectFlow.setType(type);

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
     * 
     * @throws Exception
     */
    @Transactional(value = "TransactionManager")
    public void createNode(int projectId, int workflowId, NodeType nodeType, int userId, String name) throws Exception {
        FlowNode flowNode = new FlowNode();

        flowNode.setName(name);
        flowNode.setFlowId(workflowId);
        flowNode.setType(nodeType);
        flowNode.setPosX(0.0);
        flowNode.setPosY(0.0);
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
            int startTime = BFDDateUtils.getSecs(BFDDateUtils.parse(time, Constants.BASE_TIME_FORMAT));
            schedule.setCrontabStr(JsonUtil.toJsonString(meta));

            int count = scheduleMapper.insert(schedule);
            if (count <= 0) {
                throw new Exception("插入失败");
            }

        } else {
            // 更新调度信息
            ScheduleMeta meta = JsonUtil.parseObject(schedule.getCrontabStr(), ScheduleMeta.class);
            int startTime = BFDDateUtils.getSecs(BFDDateUtils.parse(time, Constants.BASE_TIME_FORMAT));
            schedule.setCrontabStr(JsonUtil.toJsonString(meta));

            schedule.setLastModifyBy(userId);
            schedule.setModifyTime(new Date());
            int count = scheduleMapper.update(schedule);
            if (count <= 0) {
                throw new Exception("更新失败");
            }
        }
    }

    /**
     * 插入 ExecutionNode
     * <p>
     *
     * @param executionNode
     */
    public void insertExecutionNode(ExecutionNode executionNode) {
        // 插入执行节点信息
        executionNodeMapper.insert(executionNode);
    }

    /**
     * 更新 ExecutionNode
     * <p>
     *
     * @param executionNode
     */
    public void updateExecutionNode(ExecutionNode executionNode) {
        // 更新执行节点信息
        executionNodeMapper.update(executionNode);
    }

    /**
     * 插入 ExecNodeLog
     * <p>
     *
     * @param execNodeLog
     */
    public void insertExecNodeLog(ExecNodeLog execNodeLog) {
        // 插入执行节点执行日志
        execNodeLogMapper.insert(execNodeLog);
    }

    /**
     * 获取 node 执行详情
     * <p>
     *
     * @param execId
     * @param nodeId
     * @param attempt
     * @return {@link ExecutionNode}
     */
    public ExecutionNode queryExecutionNode(long execId, Integer nodeId, Integer attempt) {
        return executionNodeMapper.selectOneExecNode(execId, nodeId, attempt);
    }

    public ExecutionNode queryExecutionNodeLastAttempt(long execId, Integer nodeId) {
        return executionNodeMapper.selectExecNodeLastAttempt(execId, nodeId);
    }

    /**
     * 查询 Schedule
     * <p>
     *
     * @param flowId
     * @return {@link Schedule}
     */
    public Schedule querySchedule(int flowId) {
        // 插入执行节点信息
        return scheduleMapper.selectByFlowId(flowId);
    }

    /**
     * 获取 hdfs 中的资源路径
     * <p>
     *
     * @param resourceId
     * @param isPub
     * @return 资源的 hdfs 路径
     */
    public String queryResourceHdfsPath(int resourceId, boolean isPub) {
        Resource resource = resourceMapper.queryDetail(resourceId);
        if (resource != null) {
            return HdfsPathManager.genResourceHdfsPath(resource.getOrgName(), resource.getProjectName(), resourceId, isPub) + resource.getName();
        }
        return null;
    }

}
