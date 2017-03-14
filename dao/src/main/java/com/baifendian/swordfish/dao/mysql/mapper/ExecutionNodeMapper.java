
package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.enums.NodeTypeHandler;
import com.baifendian.swordfish.dao.mysql.model.ExecutionNode;
import com.baifendian.swordfish.dao.mysql.model.statistics.FlowNodeErrorNum;
import com.baifendian.swordfish.dao.mysql.model.statistics.FlowNodeTimeConsumer;
import com.baifendian.swordfish.dao.mysql.model.statistics.FlowStatusNum;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * workflow 执行的节点信息操作
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public interface ExecutionNodeMapper {

    /**
     * 插入记录并获取记录 id
     * <p>
     *
     * @param executionNode
     * @return 插入记录数
     */
    @InsertProvider(type = ExecutionNodeMapperProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "executionNode.id", resultType = Long.class, before = false)
    int insert(@Param("executionNode") ExecutionNode executionNode);

    /**
     * workflow 执执行的节点的信息更新
     * <p>
     *
     * @param executionNode
     * @return 更新记录数
     */
    @UpdateProvider(type = ExecutionNodeMapperProvider.class, method = "update")
    int update(@Param("executionNode") ExecutionNode executionNode);

    /**
     * 查询单个Node记录(由于重试会获取多条记录)
     * <p>
     *
     * @param execId,
     *            nodeId
     * @return
     */
    @Results(value = { @Result(property = "id", column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "execId", column = "exec_id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "appsId", column = "apps_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectByNodeIdAndAttempt")
    ExecutionNode selectOneExecNode(@Param("execId") Long execId, @Param("nodeId") Integer nodeId, @Param("attempt") Integer attempt);

    /**
     *
     * @param execId
     * @param nodeId
     * @return
     */
            @Results(value = {@Result(property = "id", column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                    @Result(property = "execId", column = "exec_id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                    @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                    @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                    @Result(property = "appsId", column = "apps_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                    @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                    @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT)
            })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectExecNodeLastAttempt")
    ExecutionNode selectExecNodeLastAttempt(@Param("execId") Long execId, @Param("nodeId") Integer nodeId);

    /**
     * 查询单个Node记录(由于重试会获取多条记录)
     * <p>
     *
     * @param execId,
     *            nodeId
     * @return
     */
    @Results(value = { @Result(property = "id", column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "execId", column = "exec_id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "appsId", column = "apps_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectByNodeId")
    List<ExecutionNode> selectExecNode(@Param("execId") Long execId, @Param("nodeId") Integer nodeId);

    /**
     * 查询单个Node记录(由于重试会获取多条记录)
     * <p>
     *
     * @param execId,
     *            nodeId
     * @return
     */
    @Results(value = { @Result(property = "id", column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "execId", column = "exec_id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "appsId", column = "apps_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectByFlowId")
    List<ExecutionNode> selectExecNodes(@Param("execId") Long execId, @Param("flowId") Integer flowId);

    /**
     * 查询flow 中所有节点执行的状态
     * <p>
     *
     * @param execId,
     *            flowId
     * @return
     */
    @Results(value = { @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectStatusByFlowId")
    List<ExecutionNode> selectExecNodesStatus(@Param("execId") Long execId, @Param("flowId") int flowId);

    @DeleteProvider(type = ExecutionNodeMapperProvider.class, method = "deleteByExecId")
    int deleteByExecId(@Param("execId") Long execId);

    /**
     * 查询某个项目执行的节点状态记录
     * <p>
     *
     * @param projectId
     * @param queryDate
     * @return
     */
    @Results(value = { @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectNodeStatus")
    List<FlowStatusNum> selectNodeStatus(@Param("projectId") Integer projectId, @Param("queryDate") Date queryDate);

    /**
     * 查询某个项目的用户执行的节点状态记录
     * <p>
     *
     * @param projectId
     * @param queryDate
     * @param userId
     * @return
     */
    @Results(value = { @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectUserNodeStatus")
    List<FlowStatusNum> selectUserNodeStatus(@Param("projectId") Integer projectId, @Param("queryDate") Date queryDate, @Param("userId") int userId);

    /**
     * 查询某个项目每日节点运行状态(每天趋势)
     * <p>
     *
     * @param projectId
     * @param startDate
     * @param endDate
     * @return
     */
    @Results(value = { @Result(property = "day", column = "day", javaType = Date.class, jdbcType = JdbcType.DATE),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectDayNodeStatus")
    List<FlowStatusNum> selectDayNodeStatus(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 查询某个项目每小时节点运行状态(每小时趋势)
     * <p>
     *
     * @param projectId
     * @param startDate
     * @param endDate
     * @return
     */
    @Results(value = { @Result(property = "hour", column = "hour", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectHourNodeStatus")
    List<FlowStatusNum> selectHourNodeStatus(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 查询某个项目节点流耗时排行信息
     * <p>
     *
     * @param projectId
     * @param startDate
     * @param endDate
     * @param num
     * @return
     */
    @Results(value = { @Result(property = "execId", column = "exec_id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "nodeName", column = "node_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "nodeType", column = "node_type", typeHandler = NodeTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "submitUser", column = "submit_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "submitUserName", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "duration", column = "duration", javaType = int.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectNodeTopTimes")
    List<FlowNodeTimeConsumer> selectNodeTopTimes(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("num") int num);

    @Results(value = { @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "nodeName", column = "node_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "nodeType", column = "node_type", typeHandler = NodeTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "submitUser", column = "submit_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "submitUserName", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectNodeErrorNum")
    List<FlowNodeErrorNum> selectNodeErrorNum(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("num") int num);
}
