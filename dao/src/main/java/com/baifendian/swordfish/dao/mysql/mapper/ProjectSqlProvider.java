package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.model.Project;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.Map;

/**
 * Created by caojingwei on 16/8/25.
 */
public class ProjectSqlProvider {

    public String insert(Map<String, Object> parameter) {
        return new SQL() {{
            INSERT_INTO("project");
            VALUES("name", "#{project.name}");
            VALUES("`desc`", "#{project.desc}");
            VALUES("tenant_id", "#{project.tenantId}");
            VALUES("create_time", "#{project.createTime}");
            VALUES("modify_time", "#{project.modifyTime}");
            VALUES("owner", "#{project.ownerId}");
            VALUES("queue_id", "#{project.queueId}");
            VALUES("mail_groups", "#{project.mailGroups}");
        }}.toString();
    }

    public String queryUserProject(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("p.id as id,p.name as name,p.descs");
                SELECT("p.create_time as create_time, p.modify_time as modify_time,p.owner as owner");
                SELECT("u.name as owner_name, p.queue_id as queue_id, q.name as queue_name");
                FROM("project p");
                JOIN("project_user p_u on p_u.project_id = p.id");
                LEFT_OUTER_JOIN("user u on p.owner = u.id");
                LEFT_OUTER_JOIN("queue q on p.queue_id = q.id");
                WHERE("p_u.user_id = #{userId}");
            }}.toString();
    }

    public String queryAllProject(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("p.id as id,p.name as name,p.`desc` as `desc`");
                SELECT("p.create_time as create_time, p.modify_time as modify_time,p.owner as owner");
                SELECT("u.name as owner_name ");
                FROM("project p");
                LEFT_OUTER_JOIN("user u on p.owner = u.id");
            }}.toString();
    }

    public String queryTenantProject(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("p.id as id,p.name as name,p.`desc` as `desc`,p.tenant_id as tenant_id, t.name as tenant_name");
                SELECT("p.create_time as create_time, p.modify_time as modify_time,p.owner as owner");
                SELECT("u.name as owner_name, p.queue_id as queue_id, q.name as queue_name");
                FROM("project p");
                LEFT_OUTER_JOIN("user u on p.owner = u.id");
                LEFT_OUTER_JOIN("queue q on p.queue_id = q.id");
                WHERE("p.tenant_id = #{tenantId}");
            }}.toString();
    }



    public String query(@Param("project") Project project, final boolean isJoin) {
        return new SQL() {{
            SELECT("p.id as id,p.name as name,p.`desc` as `desc`,p.tenant_id as tenant_id,p.create_time as create_time,p.modify_time as modify_time,p.owner as owner,p.queue_id as queue_id,p.db_id as db_id");
            if (isJoin) {
                SELECT("o.`name` as tenant_name,u.`name` as owner_name,q.`name` as queue_name");
            }
            FROM("project p");
            if (isJoin) {
                LEFT_OUTER_JOIN("tenant o on p.tenant_id = o.id");
                LEFT_OUTER_JOIN("user u on p.owner = u.id");
                LEFT_OUTER_JOIN("queue q on p.queue_id = q.id ");
            }
            if (project.getId() !=null) {
                WHERE("p.id = #{project.id}");
            }
            if (StringUtils.isNotEmpty(project.getName())) {
                WHERE("p.name = #{project.name}");
            }
            if (StringUtils.isNotEmpty(project.getDesc())) {
                WHERE("p.`desc` = #{project.desc}");
            }
            if (project.getOwnerId() !=null) {
                WHERE("p.owner = #{project.ownerId}");
            }
        }}.toString();
    }

    public String updateById(final Project project) {
        return new SQL() {{
            UPDATE("project");
            if (project.getDesc() != null) {
                SET("`desc`=#{project.desc}");
            }
            if (project.getOwnerId() !=null) {
                SET("owner=#{project.ownerId}");
            }
            SET("modify_time=#{project.modifyTime}");
            WHERE("id = #{project.id}");
        }}.toString();
    }

    public String deleteById(final int id) {
        return new SQL() {{
            DELETE_FROM("project");
            WHERE("id = #{id}");
        }}.toString();
    }

    public String queryById(final int projectId){
        return new SQL() {{
            SELECT("p.id as id,p.name as name,p.`desc` as `desc`,p.tenant_id as tenant_id, t.name as tenant_name");
            SELECT("p.create_time as create_time, p.modify_time as modify_time,p.owner as owner");
            SELECT("u.name as owner_name, p.queue_id as queue_id, q.name as queue_name");
            FROM("project p");
            LEFT_OUTER_JOIN("user u on p.owner = u.id");
            LEFT_OUTER_JOIN("queue q on p.queue_id = q.id");
            WHERE("p.id = #{projectId}");
        }}.toString();
    }

    public String updateDescAndMailById(final int projectId,final String desc,final Date modifyTime,final String mailGroups) {
        return new SQL() {{
            UPDATE("project");
            SET("`desc`=#{desc}");
            SET("modify_time=#{modifyTime}");
            SET("mail_groups=#{mailGroups}");
            WHERE("id = #{projectId}");
        }}.toString();
    }

    public String queryByTableId(final Integer tableId) {
        return new SQL() {{
            SELECT("p.*");
            FROM("project as p");
            JOIN("er_phyentity as phy on p.id=phy.project_id");
            WHERE("phy.id = #{tableId}");
        }}.toString();
    }

    public String queryProjectSizeTopByTenantId(final int tenantId,final int top){
        return "select p.*,u.`name` as owner_name,SUM(dats.size) as project_size from project as p left join user as u on p.`owner` = u.id left join data_analysis_table_size as dats on p.id = dats.project_id where p.tenant_id = "+tenantId+" GROUP BY p.id ORDER BY project_size DESC LIMIT  0,"+top;
    }

    public String queryByName(Map<String, Object> parameter){
        return new SQL() {{
            SELECT("p.id as id,p.name as name,p.`desc` as `desc`,p.create_time as create_time,p.modify_time as modify_time,p.owner as owner,p.queue_id as queue_id,p.mail_groups as mail_groups");
            FROM("project p");
            WHERE("p.name = #{name}");
        }}.toString();
    }

    public String queryByDBName(final String dbName) {
        return new SQL() {{
            SELECT("p.*");
            FROM("project as p");
            LEFT_OUTER_JOIN("db on p.db_id=db.id");
            WHERE("db.name = #{dbName}");
        }}.toString();
    }
}
