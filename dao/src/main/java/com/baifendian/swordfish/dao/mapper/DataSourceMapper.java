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

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.sql.Timestamp;
import java.util.List;

@MapperScan
public interface DataSourceMapper {
  /**
   * 插入数据源
   *
   * @param dataSource
   * @return
   */
  @InsertProvider(type = DataSourceMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() as id", keyProperty = "dataSource.id", before = false, resultType = Integer.class)
  int insert(@Param("dataSource") DataSource dataSource);

  /**
   * 更新数据源
   *
   * @param dataSource
   */
  @UpdateProvider(type = DataSourceMapperProvider.class, method = "update")
  int update(@Param("dataSource") DataSource dataSource);

  /**
   * 删除数据源
   *
   * @param projectId 项目 id
   * @param name      数据源名称
   * @return
   */
  @DeleteProvider(type = DataSourceMapperProvider.class, method = "deleteByProjectAndName")
  int deleteByProjectAndName(@Param("projectId") int projectId, @Param("name") String name);

  /**
   * 查看项目下的所有数据源
   *
   * @param projectId
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerName", column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getByProjectId")
  List<DataSource> getByProjectId(@Param("projectId") Integer projectId);

  /**
   * 查询某个具体的数据源
   *
   * @param projectId
   * @param name
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerName", column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getByName")
  DataSource getByName(@Param("projectId") Integer projectId, @Param("name") String name);

  /**
   * 查询某个具体的数据源
   *
   * @param name
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
          @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getByProjectNameAndName")
  DataSource getByProjectNameAndName(@Param("projectName") String projectName,@Param("name") String name);
}
