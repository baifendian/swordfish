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

package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.enums.FunctionType;
import com.baifendian.swordfish.dao.mysql.model.Function;
import com.baifendian.swordfish.dao.mysql.model.Resource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * 函数相关操作
 * <p>
 *
 * @author : dsfan
 * @date : 2016年8月25日
 */
public interface FunctionMapper {
    /**
     * 插入记录并获取记录 id
     * <p>
     *
     * @param function
     * @return 修改记录数
     */
    @InsertProvider(type = FunctionSqlProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "function.id", resultType = int.class, before = false)
    int insertAndGetId(@Param("function") Function function);

    /**
     * 插入函数和资源的关系
     * <p>
     *
     * @param functionId
     * @param resourceIds
     * @return 修改记录数
     */
    @InsertProvider(type = FunctionSqlProvider.class, method = "insertFuncAndResources")
    int insertFuncAndResources(@Param("functionId") Integer functionId, @Param("resourceIds") List<Integer> resourceIds);

    /**
     * 查询详情
     * <p>
     *
     * @param functionId
     * @return {@link Resource}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = FunctionType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "desc", column = "param_desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "command", column = "command", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "feature", column = "feature", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "className", column = "class_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "resourceIds", column = "id", many = @Many(select = "com.baifendian.swordfish.dao.mysql.mapper.FunctionMapper.findResourceIdsByFuncId")) })
    @SelectProvider(type = FunctionSqlProvider.class, method = "queryDetail")
    Function queryDetail(@Param("functionId") Integer functionId);

    /**
     * 查询详情(by name)
     * <p>
     *
     * @param funcs
     * @return {@link Resource}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = FunctionType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "desc", column = "param_desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "command", column = "command", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "feature", column = "feature", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "className", column = "class_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "resourceIds", column = "id", many = @Many(select = "com.baifendian.swordfish.dao.mysql.mapper.FunctionMapper.findResourceIdsByFuncId")) })
    @SelectProvider(type = FunctionSqlProvider.class, method = "queryFuncsByName")
    List<Function> queryFuncsByName(@Param("funcs") Set<String> funcs, @Param("projectId") int projectId);

    /**
     * 查询资源 id 列表
     * <p>
     *
     * @param functionId
     * @return 资源id列表
     */
    @Results(value = { @Result(property = "resourceId", column = "resource_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER) })
    @SelectProvider(type = FunctionSqlProvider.class, method = "findResourceIdsByFuncId")
    List<Integer> findResourceIdsByFuncId(@Param("functionId") Integer functionId);

    /**
     * 查询资源 id 列表
     * <p>
     *
     * @param functions
     * @return 资源id列表
     */
    @Results(value = { @Result(property = "resourceId", column = "resource_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER) })
    @SelectProvider(type = FunctionSqlProvider.class, method = "findResouceIdsByFuncs")
    List<Integer> findResouceIdsByFuncs(@Param("functions") List<Function> functions);

    /**
     * 删除自定义函数
     * <p>
     *
     * @param functionId
     * @return 删除记录数
     */
    @DeleteProvider(type = FunctionSqlProvider.class, method = "deleteById")
    int delete(@Param("functionId") Integer functionId);

    /**
     * 删除函数和资源的关系
     * <p>
     *
     * @param functionId
     * @return 删除记录数
     */
    @DeleteProvider(type = FunctionSqlProvider.class, method = "deleteFuncAndResources")
    int deleteFuncAndResources(@Param("functionId") Integer functionId);

    /**
     * 更新函数
     * <p>
     *
     * @param function
     * @return 更新记录数
     */
    @UpdateProvider(type = FunctionSqlProvider.class, method = "updateById")
    int updateById(@Param("function") Function function);

    /**
     * 根据项目进行删除, 仅仅删除资源信息
     *
     * @param projectId
     * @param functionType
     * @return
     */
    @DeleteProvider(type = FunctionSqlProvider.class, method = "deleteFuncAndResourcesByProjectId")
    int deleteFuncAndResourcesByProjectId(@Param("projectId") Integer projectId, @Param("functionType") FunctionType functionType);

    /**
     * 根据项目进行删除, 仅仅删除函数信息
     *
     * @param projectId
     * @param functionType
     * @return
     */
    @DeleteProvider(type = FunctionSqlProvider.class, method = "deleteByProjectId")
    int deleteByProjectId(@Param("projectId") Integer projectId, @Param("functionType") FunctionType functionType);

    @SelectProvider(type = FunctionSqlProvider.class, method = "countByProjectId")
    int countByProjectId(@Param("projectId") Integer projectId);
}
