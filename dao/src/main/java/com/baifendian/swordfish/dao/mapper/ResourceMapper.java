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
import java.util.List;

public interface ResourceMapper {

  /**
   * 插入一个资源文件
   *
   * @param resource
   * @return
   */
  @InsertProvider(type = ResourceMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "resource.id", resultType = int.class, before = false)
  int insert(@Param("resource") Resource resource);

  /**
   * 根据项目和资源名称查询
   *
   * @param projectName
   * @param name
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "originFilename", column = "origin_filename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = ResourceMapperProvider.class, method = "queryByProjectAndResName")
  Resource queryByProjectAndResName(@Param("projectName") String projectName, @Param("name") String name);

  /**
   * 查询资源, 根据名称查询
   *
   * @param name
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "originFilename", column = "origin_filename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = ResourceMapperProvider.class, method = "queryResource")
  Resource queryResource(@Param("projectId") int projectId, @Param("name") String name);

  /**
   * 更新资源
   *
   * @param resource
   */
  @UpdateProvider(type = ResourceMapperProvider.class, method = "update")
  int update(@Param("resource") Resource resource);

  /**
   * 删除资源
   *
   * @param resourceId
   * @return
   */
  @DeleteProvider(type = ResourceMapperProvider.class, method = "delete")
  int delete(@Param("resourceId") int resourceId);

  /**
   * 删除资源
   *
   * @param projectId
   * @param name
   * @return
   */
  @DeleteProvider(type = ResourceMapperProvider.class, method = "deleteResource")
  int deleteResource(@Param("projectId") int projectId, @Param("name") String name);

  /**
   * 查询资源的详情
   *
   * @param projectId
   * @param name
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "originFilename", column = "origin_filename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = ResourceMapperProvider.class, method = "queryResourceDetail")
  Resource queryResourceDetail(@Param("projectId") int projectId, @Param("name") String name);

  /**
   * 查询项目下的资源列表
   *
   * @param projectId
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "originFilename", column = "origin_filename", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = ResourceMapperProvider.class, method = "queryResourceDetails")
  List<Resource> queryResourceDetails(@Param("projectId") int projectId);

  /**
   * 查询项目下的资源数目
   *
   * @param projectId
   * @return
   */
  @SelectProvider(type = ResourceMapperProvider.class, method = "countProjectRes")
  int countProjectRes(@Param("projectId") int projectId);
}
