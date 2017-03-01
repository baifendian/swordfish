package com.baifendian.swordfish.dao.mysql.mapper;

/**
 *  schedules 表操作
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月25日
 */
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.enums.NodeTypeHandler;
import com.baifendian.swordfish.dao.mysql.enums.PubStatus;
import com.baifendian.swordfish.dao.mysql.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.mysql.model.Schedule;
import com.baifendian.swordfish.dao.mysql.model.statistics.DisField;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *  已发布workflow节点信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月24日
 */
@MapperScan
public interface ScheduleMapper {
    /**
     * 插入记录
     * <p>
     *
     * @param
     * @return 插入记录数
     */
    @InsertProvider(type = ScheduleMapperProvider.class, method = "insert")
    int insert(@Param("schedule") Schedule schedule);

    /**
     * 任务的调度设置
     * <p>
     *
     * @param schedule
     * @return 更新记录数
     */
    @UpdateProvider(type = ScheduleMapperProvider.class, method = "update")
    int update(@Param("schedule") Schedule schedule);

    /**
     * workflow 发布任务的调度查询(单个任务)
     * <p>
     *
     * @param
     * @return
     */
    @Results(value ={
            @Result(property = "flowId", column = "flow_id",  id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time",  javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time",  javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "lastModifyBy", column = "last_modify_by",  javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "pubStatus", column = "pub_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "startDate", column = "start_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "endDate", column = "end_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "scheduleType", column = "schedule_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "crontabStr", column = "crontab_str", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "nextSubmitTime", column = "next_submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "depWorkflows", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "failurePolicy", column = "failure_policy", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "depPolicy", column = "dep_policy", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "maxTryTimes", column = "max_try_times", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "failureEmails", column = "failure_emails", javaType = Boolean.class, jdbcType = JdbcType.BIT),
            @Result(property = "successEmails", column = "success_emails", javaType = Boolean.class, jdbcType = JdbcType.BIT),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByFlowId")
    Schedule selectByFlowId(@Param("flowId") int flowId);

    /**
     * 查询已发布 workflow 任务的调度情况列表
     * <p>
     *
     * @param projectId
     * @return
     */
    @Results(value ={
            @Result(property = "flowId", column = "flow_id",  id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "createTime", column = "create_time",  javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time",  javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "lastModifyBy", column = "last_modify_by",  javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "pubStatus", column = "pub_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "startDate", column = "start_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "endDate", column = "end_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "publishTime", column = "publish_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "scheduleType", column = "schedule_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "crontabStr", column = "crontab_str", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "nextSubmitTime", column = "next_submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "depWorkflows", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "failurePolicy", column = "failure_policy", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "depPolicy", column = "dep_policy", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "maxTryTimes", column = "max_try_times", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "failureEmails", column = "failure_emails", javaType = Boolean.class, jdbcType = JdbcType.BIT),
            @Result(property = "successEmails", column = "success_emails", javaType = Boolean.class, jdbcType = JdbcType.BIT),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByProjectId")
    List<Schedule> selectByProjectId(@Param("projectId") int projectId, @Param("pubStatus") PubStatus pubStatus,
                                     @Param("scheduleStatus") ScheduleStatus scheduleStatus);

    /**
     * 查询多个workflow的调度信息
     * <p>
     *
     * @param flowIds
     * @return
     */
    @Results(value ={
            @Result(property = "flowId", column = "flow_id",  id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "createTime", column = "create_time",  javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time",  javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "lastModifyBy", column = "last_modify_by",  javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "pubStatus", column = "pub_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "startDate", column = "start_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "endDate", column = "end_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "publishTime", column = "publish_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "scheduleType", column = "schedule_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "crontabStr", column = "crontab_str", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "nextSubmitTime", column = "next_submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "depWorkflows", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "failurePolicy", column = "failure_policy", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "depPolicy", column = "dep_policy", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "maxTryTimes", column = "max_try_times", javaType = Integer.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "failureEmails", column = "failure_emails", javaType = Boolean.class, jdbcType = JdbcType.BIT),
            @Result(property = "successEmails", column = "success_emails", javaType = Boolean.class, jdbcType = JdbcType.BIT),
            @Result(property = "timeout", column = "timeout", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByFlowIds")
    List<Schedule> selectByFlowIds(@Param("flowIds") Set<Integer> flowIds);

    @Results(value ={
            @Result(property = "flowId", column = "flow_id",  id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "depWorkflows", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "queryAll")
    List<Schedule> queryAll();

    @DeleteProvider(type = ScheduleMapperProvider.class, method = "deleteByFlowId")
    int deleteByFlowId(@Param("flowId") int flowId);

    /**
     *  任务类型分布
     * <p>
     *
     * @param projectId
     * @return
     */
    @Results(value ={ @Result(property = "nodeType", column = "node_type", typeHandler = NodeTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "value", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "queryTaskTypeDis")
    List<DisField> queryTaskTypeDis(@Param("projectId") int projectId);

    /**
     *  工作流类型分布
     * <p>
     *
     * @param projectId
     * @return
     */
    @Results(value ={ @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "value", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "queryFlowTypeDis")
    List<DisField> queryFlowTypeDis(@Param("projectId") int projectId);

    /**
     * 查询一个项目中图形化ETL上线的的数目
     * <p>
     *
     * @param projectId
     * @return 查询记录数
     */
    @SelectProvider(type = ScheduleMapperProvider.class, method = "queryFlowEtlNum")
    int queryFlowEtlNum(@Param("projectId") int projectId);


    /**
     *  工作流调度类型分布
     * <p>
     *
     * @param projectId
     * @return
     */
    @Results(value ={ @Result(property = "scheduleType", column = "schedule_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "value", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ScheduleMapperProvider.class, method = "selectScheduleTypeDis")
    List<DisField> selectScheduleTypeDis(@Param("projectId") int projectId);

    @SelectProvider(type = ScheduleMapperProvider.class, method = "selectScheduleTypeNull")
    int selectScheduleTypeNull(@Param("projectId") int projectId);


    /**
     * 查询一个组织里面的workflow数量
     * <p>
     *
     * @param tenantId
     * @return 查询记录数
     */
    @SelectProvider(type = ScheduleMapperProvider.class, method = "queryFlowNum")
    int queryFlowNum(@Param("tenantId") int tenantId, @Param("flowTypes") List<FlowType> flowTypes);

}
