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

import com.baifendian.swordfish.dao.model.Project;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface ProjectMapper {

  /**
   * 插入项目信息
   *
   * @param project
   * @return
   */
  @InsertProvider(type = ProjectMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "newProject.id", before = false, resultType = int.class)
  int insert(@Param("newProject") Project project);

  /**
   * 更新项目信息
   *
   * @param project
   * @return
   */
  @UpdateProvider(type = ProjectMapperProvider.class, method = "updateById")
  int updateById(@Param("project") Project project);

  /**
   * 删除项目
   *
   * @param id
   * @return
   */
  @DeleteProvider(type = ProjectMapperProvider.class, method = "deleteById")
  int deleteById(@Param("id") int id);

  /**
   * 查询所有的项目列表
   *
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectMapperProvider.class, method = "queryAllProject")
  List<Project> queryAllProject();

  /**
   * 查询某个项目
   *
   * @param userId
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectMapperProvider.class, method = "queryProjectByUser")
  List<Project> queryProjectByUser(@Param("userId") int userId);

  /**
   * 根据用户名称查询
   *
   * @param name
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectMapperProvider.class, method = "queryByName")
  Project queryByName(@Param("name") String name);
}
