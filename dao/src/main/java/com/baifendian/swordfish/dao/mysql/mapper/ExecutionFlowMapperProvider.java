
package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.enums.FlowErrorCode;
import com.baifendian.swordfish.dao.mysql.enums.FlowRunType;
import com.baifendian.swordfish.dao.mysql.enums.FlowStatus;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.MaintainQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * workflow 执行的信息操作
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */

public class ExecutionFlowMapperProvider {

    public static final String TABLE_NAME = "execution_flows";

    List<Integer> flowTypes = new ArrayList<>();

    List<Integer> flowTypesNoLong = new ArrayList<>();

    public ExecutionFlowMapperProvider() {
        flowTypes.add(FlowType.LONG.getType());
        flowTypes.add(FlowType.SHORT.getType());
        flowTypes.add(FlowType.ETL.getType());
        flowTypesNoLong.add(FlowType.SHORT.getType());
        flowTypesNoLong.add(FlowType.ETL.getType());
    }

    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_NAME);
                VALUES("flow_id", "#{executionFlow.flowId}");
                VALUES("work_id", "#{executionFlow.workId}");
                VALUES("status", EnumFieldUtil.genFieldStr("executionFlow.status", FlowStatus.class));
                VALUES("submit_user", "#{executionFlow.submitUser}");
                VALUES("submit_time", "#{executionFlow.submitTime}");
                VALUES("schedule_time", "#{executionFlow.scheduleTime}");
                VALUES("start_time", "#{executionFlow.startTime}");
                VALUES("end_time", "#{executionFlow.endTime}");
                VALUES("workflow_data", "#{executionFlow.workflowData}");
                VALUES("type", EnumFieldUtil.genFieldStr("executionFlow.type", FlowRunType.class));
                VALUES("error_code", EnumFieldUtil.genFieldStr("executionFlow.errorCode", FlowErrorCode.class));
            }
        }.toString();
    }

    public String update(ExecutionFlow executionFlow) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);
                if (executionFlow.getStatus() != null) {
                    SET("status = " + EnumFieldUtil.genFieldStr("executionFlow.status", FlowStatus.class));
                }
                if (executionFlow.getStartTime() != 0) {
                    SET("start_time = #{executionFlow.startTime}");
                }
                if (executionFlow.getEndTime() != 0) {
                    SET("end_time = #{executionFlow.endTime}");
                }
                if (executionFlow.getErrorCode() != null) {
                    SET("error_code = " + EnumFieldUtil.genFieldStr("executionFlow.errorCode", FlowErrorCode.class));
                }
                WHERE("id = #{executionFlow.id}");
            }
        }.toString();
    }

    public String select(MaintainQuery maintainQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("select a.*, b.type as flow_type, b.name as flow_name, c.name as submit_user_name ");
        sb.append("from execution_flows as a ");
        sb.append("inner join project_flows as b on a.flow_id = b.id and b.project_id = #{maintainQuery.projectId} ");
        sb.append("inner join user as c on a.submit_user = c.id where 1=1 ");
        if (maintainQuery.getMyself()) {
            sb.append("and a.submit_user = #{maintainQuery.userId} ");
        }
        if (maintainQuery.getStartTime() != null) {
            sb.append("and a.start_time >= #{maintainQuery.startTime} ");
        }
        if (maintainQuery.getEndTime() != null) {
            sb.append("and a.start_time <= #{maintainQuery.endTime} ");
        }
        if (maintainQuery.getExecId() != null) {
            sb.append("and a.id = #{maintainQuery.execId} ");
        }
        if (CollectionUtils.isNotEmpty(maintainQuery.getTaskStatus())) {
            sb.append("and a.status in (");
            int size = maintainQuery.getTaskStatus().size();
            String fm = EnumFieldUtil.genFieldSpecialStr("maintainQuery.taskStatus[{0}]", FlowStatus.class);
            MessageFormat mf = new MessageFormat(fm);
            for (int i = 0; i < size; i++) {
                sb.append(mf.format(new Object[] { i }));
                if (i < size - 1) {
                    sb.append(",");
                }
            }
            sb.append(") ");
        }
        if (CollectionUtils.isNotEmpty(maintainQuery.getFlowTypes())) {
            List<Integer> types = new ArrayList<>();
            for (FlowType flowType : maintainQuery.getFlowTypes()) {
                types.add(flowType.getType());
            }
            sb.append("and b.type in (");
            sb.append(StringUtils.join(types, ","));
            sb.append(") ");
        }
        if (maintainQuery.getName() != null) {
            // sb.append("and b.name like '%${maintainQuery.name}%' ");
            sb.append("and b.name like CONCAT(CONCAT('%', #{maintainQuery.name}), '%') ");
        }
        int start = (maintainQuery.getStart() - 1) * maintainQuery.getLength();

        String limit = String.format("ORDER BY a.start_time desc LIMIT %s , %s", start, maintainQuery.getLength());
        sb.append(limit);
        return sb.toString();
    }

    public String selectCount(MaintainQuery maintainQuery) {

        StringBuilder sb = new StringBuilder();
        sb.append("select count(1) ");
        sb.append("from execution_flows as a ");
        sb.append("inner join project_flows as b on a.flow_id = b.id and b.project_id = #{maintainQuery.projectId} ");
        sb.append("inner join user as c on a.submit_user = c.id where 1=1 ");
        if (maintainQuery.getMyself()) {
            sb.append("and a.submit_user = #{maintainQuery.userId} ");
        }
        if (maintainQuery.getStartTime() != null) {
            sb.append("and a.start_time >= #{maintainQuery.startTime} ");
        }
        if (maintainQuery.getEndTime() != null) {
            sb.append("and a.start_time <= #{maintainQuery.endTime} ");
        }
        if (maintainQuery.getExecId() != null) {
            sb.append("and a.id = #{maintainQuery.execId} ");
        }
        if (maintainQuery.getTaskStatus() != null) {
            sb.append("and a.status in (");
            int size = maintainQuery.getTaskStatus().size();
            String fm = EnumFieldUtil.genFieldSpecialStr("maintainQuery.taskStatus[{0}]", FlowStatus.class);
            MessageFormat mf = new MessageFormat(fm);
            for (int i = 0; i < size; i++) {
                sb.append(mf.format(new Object[] { i }));
                if (i < size - 1) {
                    sb.append(",");
                }
            }
            sb.append(") ");
        }
        if (CollectionUtils.isNotEmpty(maintainQuery.getFlowTypes())) {
            List<Integer> types = new ArrayList<>();
            for (FlowType flowType : maintainQuery.getFlowTypes()) {
                types.add(flowType.getType());
            }
            sb.append("and b.type in (");
            sb.append(StringUtils.join(types, ","));
            sb.append(") ");
        }
        if (maintainQuery.getName() != null) {
            sb.append("and b.name like CONCAT(CONCAT('%', #{maintainQuery.name}), '%') ");
        }
        return sb.toString();
    }

    public String selectByExecId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.*");
                SELECT("b.type as flow_type");
                SELECT("b.name as flow_name");
                SELECT("b.project_id as project_id");
                SELECT("c.name as project_name");
                SELECT("d.id as org_id");
                SELECT("d.name as org_name");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id");
                INNER_JOIN("project as c on b.project_id = c.id");
                LEFT_OUTER_JOIN("org as d ON c.org_id = d.id");
                WHERE("a.id = #{execId}");
            }
        }.toString();
    }

    public String selectByFlowIdAndTimes(Map<String, Object> parameter) {
        StringBuilder sb = new StringBuilder();
        String inExpr = "(" + FlowRunType.DISPATCH.ordinal() + "," + FlowRunType.ADD_DATA.ordinal() + ")";
        sb.append("SELECT id, flow_id, work_id, type, status, schedule_time FROM execution_flows WHERE flow_id = #{flowId} AND type IN " + inExpr + " AND ");
        sb.append("schedule_time = (SELECT MIN(schedule_time) FROM execution_flows WHERE flow_id = #{flowId} AND type IN" + inExpr
                  + " AND schedule_time >= #{startDate} AND schedule_time < #{endDate})");

        return sb.toString();
    }

    public String selectByFlowIdAndTime(Map<String, Object> parameter) {
        StringBuilder sb = new StringBuilder();
        String inExpr = "(" + FlowRunType.DISPATCH.ordinal() + "," + FlowRunType.ADD_DATA.ordinal() + ")";
        sb.append("SELECT id, flow_id, work_id, type, status, schedule_time FROM execution_flows WHERE flow_id = #{flowId} AND type IN " + inExpr + " AND ");
        sb.append("schedule_time = #{scheduleTime}");

        return sb.toString();
    }

    public String selectNewestExeFlow(Set<Integer> flowIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("select a.id, a.flow_id, a.work_id, a.status from execution_flows as a,");

        sb.append("(select max(id) as id from execution_flows ");
        sb.append("where flow_id in (0");
        for (Integer flowId : flowIds) {
            sb.append(",");
            sb.append(flowId);
        }
        sb.append(") ");
        sb.append("group by flow_id) as b ");
        sb.append("where a.id = b.id");
        return sb.toString();
    }

    public String deleteByExecId(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("id = #{execId}");
            }
        }.toString();
    }

    public String selectFlowStatus(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.status, count(a.status) as num");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id");
                WHERE("b.project_id = #{projectId}");
                WHERE("a.start_time >= #{queryDate}"); // should be >=, (add by
                                                       // qifeng.dai)
                WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
                GROUP_BY("a.status");
            }
        }.toString();
    }

    public String selectUserFlowStatus(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.status, count(a.status) as num");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id");
                WHERE("b.project_id = #{projectId}");
                WHERE("a.submit_user = #{userId}");
                WHERE("a.start_time >= #{queryDate}"); // should be >=, (add by
                                                       // qifeng.dai)
                WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
                GROUP_BY("a.status");
            }
        }.toString();
    }

    public String selectDayFlowStatus(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.status, count(a.status) as num, DATE_FORMAT(a. start_time,'%Y-%m-%d')as day");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id");
                WHERE("b.project_id = #{projectId}");
                WHERE("a.start_time >= #{startDate}"); // should be >=, (add by
                                                       // qifeng.dai)
                WHERE("a.start_time < #{endDate}");
                WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
                GROUP_BY("a.status, day");
            }
        }.toString();
    }

    public String selectFlowHourAvgTime(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("DATE_FORMAT(start_time, '%k')as hour");
                SELECT("avg(UNIX_TIMESTAMP(end_time)-UNIX_TIMESTAMP(start_time)) as duration");
                SELECT("count(0) as num");
                FROM(TABLE_NAME);
                WHERE("flow_id = #{flowId}");
                WHERE("start_time >= #{startDate}"); // should be >=, (add by
                                                     // qifeng.dai)
                WHERE("start_time < #{endDate}");
                WHERE("end_time is not NULL");
                GROUP_BY("hour");
            }
        }.toString();
    }

    public String selectFlowDayAvgTime(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("DATE_FORMAT(start_time, '%Y-%m-%d')as day");
                SELECT("avg(UNIX_TIMESTAMP(end_time)-UNIX_TIMESTAMP(start_time)) as duration");
                SELECT("count(0) as num");
                FROM(TABLE_NAME);
                WHERE("flow_id = #{flowId}");
                WHERE("start_time >= #{startDate}"); // should be >=, (add by
                                                     // qifeng.dai)
                WHERE("start_time < #{endDate}");
                WHERE("end_time is not NULL");
                GROUP_BY("day");
            }
        }.toString();
    }

    public String selectHourFlowStatus(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.status, count(a.status) as num, DATE_FORMAT(start_time,'%k')as hour");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id");
                WHERE("b.project_id = #{projectId}");
                WHERE("a.start_time >= #{startDate}"); // should be >=, (add by
                                                       // qifeng.dai)
                WHERE("a.start_time < #{endDate}");
                WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
                GROUP_BY("a.status, hour");
            }
        }.toString();
    }

    public String selectFlowTopTimes(Map<String, Object> parameter) {
        String sqlTemp = new SQL() {
            {
                SELECT("a.id, a.flow_id, a.status, a.submit_user, a.start_time, a.end_time");
                SELECT("IFNULL(UNIX_TIMESTAMP(a.end_time)-UNIX_TIMESTAMP(a.start_time)," + " UNIX_TIMESTAMP()-UNIX_TIMESTAMP(a.start_time)) as duration");
                SELECT("b.type as flow_type, b.name as flow_name");
                SELECT("c.name as submit_user_name");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
                INNER_JOIN("user as c on a.submit_user = c.id");
                WHERE("a.start_time >= #{startDate}"); // should be >=, (add by
                                                       // qifeng.dai)
                WHERE("a.start_time < #{endDate}");
                WHERE("b.type in (" + StringUtils.join(flowTypesNoLong, ",") + ")");
                ORDER_BY("duration desc");
            }
        }.toString();

        String sql = String.format("%s LIMIT 0, #{num}", sqlTemp);
        return sql;
    }

    public String selectFlowErrorNum(Map<String, Object> parameter) {

        String sqlTemp = new SQL() {
            {
                SELECT("a.flow_id, count(a.flow_id) as num");
                SELECT("b.type as flow_type, b.name as flow_name");
                SELECT("c.name as submit_user_name");
                FROM("execution_flows as a");
                INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
                INNER_JOIN("user as c on a.submit_user = c.id");
                WHERE("a.start_time >= #{startDate}"); // should be >=, (add by
                                                       // qifeng.dai)
                WHERE("a.start_time < #{endDate}");
                WHERE("a.status = " + FlowStatus.FAILED.getType());
                WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
                GROUP_BY("a.flow_id");
                ORDER_BY("num desc");
            }
        }.toString();

        String sql = String.format("%s LIMIT 0, #{num}", sqlTemp);
        return sql;
    }
}