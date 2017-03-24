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
package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.model.Resource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

public interface ResourceMapper {

  /**
   * 插入一个资源文件
   *
   * @param resource
   * @return
   */
  @InsertProvider(type = ResourceSqlProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "resource.id", resultType = int.class, before = false)
  int insert(@Param("resource") Resource resource);

  /**
   * 查询资源, 根据名称查询
   *
   * @param name
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  //@SelectProvider(type = ResourceSqlProvider.class, method = "queryDetail")
  @SelectProvider(type = ResourceSqlProvider.class, method = "insert")
  Resource queryResource(@Param("name") String name);

  /**
   * 删除资源
   *
   * @param name
   * @return
  @DeleteProvider(type = ResourceSqlProvider.class, method = "delete")
  int delete(@Param("name") String name);*/

//
//  /**
//   * 查询详情 <p>
//   *
//   * @return {@link Resources}
//   */
//  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = ResourceType.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "parentId", column = "parent_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
//      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
//      @Result(property = "publishTime", column = "publish_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
//      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "orgId", column = "org_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "orgName", column = "org_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),})
//  @SelectProvider(type = ResourceSqlProvider.class, method = "queryDetails")
//  List<Resources> queryDetails(@Param("resourceIds") Set<Integer> resourceIds);
//
//  /**
//   * 删除项目的资源信息
//   */
//  @DeleteProvider(type = ResourceSqlProvider.class, method = "deleteByProjectId")
//  int deleteByProjectId(@Param("projectId") Integer projectId);
//
//  @SelectProvider(type = ResourceSqlProvider.class, method = "queryIdByNames")
//  List<Integer> queryIdByNames(@Param("projectId") Integer projectId, @Param("resourceNames") List<String> resourceNames);
//
//  @SelectProvider(type = ResourceSqlProvider.class, method = "countByProject")
//  Integer countByProject(@Param("projectId") Integer projectId);
}
