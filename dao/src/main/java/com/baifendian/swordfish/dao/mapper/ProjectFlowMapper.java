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

import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

public interface ProjectFlowMapper {

  /**
   * 插入记录并获取记录 id <p>
   *
   * @param flow
   * @return 插入记录数
   */
  @InsertProvider(type = ProjectFlowMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "flow.id", resultType = int.class, before = false)
  int insertAndGetId(@Param("flow") ProjectFlow flow);

  /**
   * 通过 id 更新记录 <p>
   *
   * @param flow
   * @return 修改记录数
   */
  @UpdateProvider(type = ProjectFlowMapperProvider.class, method = "updateById")
  int updateById(@Param("flow") ProjectFlow flow);

  /**
   * 删除一个 workflow <p>
   *
   * @param flowId
   * @return 删除记录数
   */
  @DeleteProvider(type = ProjectFlowMapperProvider.class, method = "deleteById")
  int deleteById(@Param("id") int flowId);

  /**
   * 查询一个 workflow (by name) <p>
   *
   * @param projectId
   * @param name
   * @return {@link ProjectFlow}
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ProjectFlowMapperProvider.class, method = "queryByName")
  ProjectFlow findByName(@Param("projectId") int projectId, @Param("name") String name);

  /**
   * 根据Id获取工作流
   *
   * @param id
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ProjectFlowMapperProvider.class, method = "queryById")
  ProjectFlow findById(@Param("id") int id);

  /**
   * 根据项目名和工作流名称，查询一个工作流
   *
   * @param projectName
   * @param name
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ProjectFlowMapperProvider.class, method = "findByProjectNameAndName")
  ProjectFlow findByProjectNameAndName(@Param("projectName") String projectName, @Param("name") String name);

  /**
   * 根据工作流ID查询一个工作流
   *
   * @param projectId
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "lastModifyBy", column = "last_modify_by", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectFlowMapperProvider.class, method = "findByProject")
  List<ProjectFlow> findByProject(@Param("projectId") Integer projectId);

  /**
   * 查询工作流 owner
   *
   * @param id
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectFlowMapperProvider.class, method = "queryFlowOwner")
  User queryFlowOwner(@Param("id") Integer id);

  /**
   * 更新项目的配置
   *
   * @param projectId
   * @param queue
   * @param proxyUser
   * @return
   */
  @UpdateProvider(type = ProjectFlowMapperProvider.class, method = "updateProjectConf")
  int updateProjectConf(@Param("projectId") int projectId, @Param("queue") String queue, @Param("proxyUser") String proxyUser);

  /**
   * 计算项目的工作流数
   *
   * @param projectId
   * @return
   */
  @SelectProvider(type = ProjectFlowMapperProvider.class, method = "countProjectFlows")
  int countProjectFlows(@Param("projectId") Integer projectId);
}
