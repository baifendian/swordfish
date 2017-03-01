/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月24日
 * File Name      : ResourceSqlProvider.java
 */

package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.enums.ResourcePubStatus;
import com.baifendian.swordfish.dao.mysql.enums.ResourceType;
import com.baifendian.swordfish.dao.mysql.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.mysql.model.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 资源表 sql 生成器
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月24日
 */
public class ResourceSqlProvider {

    /**
     * 插入
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO("resources");
                VALUES("name", "#{resource.name}");
                VALUES("type", EnumFieldUtil.genFieldStr("resource.type", ResourceType.class));
                VALUES("project_id", "#{resource.projectId}");
                VALUES("owner_id", "#{resource.ownerId}");
                VALUES("modify_time", "#{resource.modifyTime}");
                VALUES("create_time", "#{resource.createTime}");
                VALUES("publish_time", "#{resource.publishTime}");
                VALUES("`desc`", "#{resource.desc}");
            }
        }.toString();
    }

    /**
     * 查询资源详情
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String queryDetail(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("r.*");
                SELECT("u.name as owner_name");
                SELECT("u2.name as last_publish_by_name");
                SELECT("p.name as project_name");
                SELECT("p.org_id as org_id");
                SELECT("o.name as org_name");
                FROM("resources r");
                INNER_JOIN("user as u on u.id = r.owner_id");
                LEFT_OUTER_JOIN("user as u2 on u2.id = r.last_publish_by");
                INNER_JOIN("project as p on r.project_id = p.id");
                LEFT_OUTER_JOIN("org as o on p.org_id = o.id");
                WHERE("r.id = #{resourceId}");
            }
        }.toString();
    }

    /**
     * 查询资源详情
     * <p>
     *
     * @param resourceIds
     * @return sql 语句
     */
    public String queryDetails(Set<Integer> resourceIds) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT r.*, u.name as owner_name, p.name as project_name, p.org_id as org_id, o.name as org_name ");
        sb.append("FROM resources r ");
        sb.append("LEFT OUTER JOIN user as u on u.id = r.owner_id ");
        sb.append("LEFT OUTER JOIN project as p on r.project_id = p.id ");
        sb.append("LEFT OUTER JOIN org as o on p.org_id = o.id ");

        if (resourceIds != null && resourceIds.size() > 0) {
            sb.append("where r.id in(");
            sb.append(StringUtils.join(resourceIds, ","));
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * 查询资源目录
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String queryAllResourceDirRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("dr.*");
                FROM("dir_relation as dr");
                LEFT_OUTER_JOIN("resources as r on dr.file_id = r.id");
                //WHERE("dr.type = " + FolderType.RESOURCE.getType());
                WHERE("r.type = " + EnumFieldUtil.genFieldStr("type", ResourceType.class));
                WHERE("r.project_id = #{projectId}");
            }
        }.toString();
    }

    /**
     * 删除某个资源
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String deleteById(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM("resources");
                WHERE("id = #{resourceId}");
            }
        }.toString();
    }

    /**
     * 更新某个资源
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String updateById(Map<String, Object> parameter) {
        Resource resource = (Resource) parameter.get("resource");
        return new SQL() {
            {
                UPDATE("resources");
                SET("`desc` = #{resource.desc}");
                SET("modify_time = #{resource.modifyTime}");
                if (resource.getPubStatus() != null) {
                    SET("pub_status = " + EnumFieldUtil.genFieldStr("resource.pubStatus", ResourcePubStatus.class));
                }
                if (resource.getPublishTime() != null) {
                    SET("publish_time = #{resource.publishTime}");
                }
                if (resource.getLastPublishBy() != null) {
                    SET("last_publish_by = #{resource.lastPublishBy}");
                }
                WHERE("id = #{resource.id}");
            }
        }.toString();
    }

    /**
     * 删除项目的资源信息
     *
     * @param parameter
     * @return
     */
    public String deleteByProjectId(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM("resources");
                WHERE("project_id = #{projectId}");
            }
        }.toString();
    }

    /**
    *
    * @param parameter
    * @return
    */
    public String queryIdByNames(Map<String, Object> parameter) {
        List<String> resourceNames = (List<String>) parameter.get("resourceNames");
        StringBuffer buffer = new StringBuffer();

        if(resourceNames != null) {
            for(String resourceName: resourceNames) {
                if(buffer.length() > 0) {
                    buffer.append(",");
                }
                buffer.append("\"" + resourceName + "\"");
            }
        }

        return new SQL() {
            {
                SELECT("id");
                FROM("resources");
                WHERE("project_id = #{projectId}");
                WHERE("name in (" + buffer.toString() + ")");
            }
        }.toString();
    }

    public String countByProject(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("count(1)");
                FROM("resources");
                WHERE("project_id = #{projectId}");
            }
        }.toString();
    }
}
