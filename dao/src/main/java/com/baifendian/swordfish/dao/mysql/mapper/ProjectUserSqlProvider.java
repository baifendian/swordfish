package com.baifendian.swordfish.dao.mysql.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * Created by caojingwei on 16/8/25.
 */
public class ProjectUserSqlProvider {

    public String insert(Map<String, Object> parameter) {
        return new SQL() {{
            INSERT_INTO("project_user");
            VALUES("project_id", "#{projectUser.projectId}");
            VALUES("user_id", "#{projectUser.userId}");
            VALUES("`create_time`", "#{projectUser.createTime}");
        }}.toString();
    }

    public String queryForUser(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("u.id as id, u.name as name, email, phone, u.create_time as create_time");
            SELECT("u.modify_time as modify_time, u.tenant_id as tenant_id, t.name as tenant_name");
            SELECT("join_time, role_type, status");
            FROM("project_user");
            JOIN("user u on project_user.user_id = user.id");
            LEFT_OUTER_JOIN("tenant t on u.tenant_id = t.id");
            WHERE("project_user.project_id = #{projectId}");
        }}.toString();
    }

    public String queryForProject(final int userId) {
        return new SQL() {{
            SELECT("p.id as id,p.name as name,p.`desc` as `desc`,p.tenant_id as tenant_id,p.create_time as create_time,p.modify_time as modify_time,p.owner as owner,p.queue_id as queue_id,p.mail_groups as mail_groups");

            FROM("project_user p_u");
            JOIN("project p on p_u.project_id = p.id");

            WHERE("p_u.user_id = #{userId}");
        }}.toString();
    }

    public String delete(final int projectId,final int userId){
        return new SQL() {{
            DELETE_FROM("project_user");
            WHERE("user_id = #{userId}");
            WHERE("project_id = #{projectId}");
        }}.toString();
    }

    public String query(final int userId, final int projectId){
        return new SQL() {{
            SELECT("*");
            FROM("project_user");
            WHERE("project_id = #{projectId}");
            WHERE("user_id = #{userId}");
        }}.toString();
    }

}
