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

import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.enums.FunctionType;
import com.baifendian.swordfish.dao.model.Function;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 函数表 sql 生成器 <p>
 *
 * @author : dsfan
 * @date : 2016年8月25日
 */
public class FunctionSqlProvider {

  // public String getId(Map<String, Object> parameter){
  //
  // return new SQL() {
  // {
  // INSERT_INTO("funcs");
  // VALUES("`name`", "#{function.name}");
  // VALUES("`type`", "#{function.type}");
  // VALUES("`project_id`", "#{function.project_id}");
  // VALUES("`owner_id`", "#{function.owner_id}");
  // VALUES("`create_time`", "#{function.createTime}");
  // VALUES("`modify_time`", "#{function.modify_time}");
  // VALUES("`command`", "#{function.command}");
  // VALUES("`param_desc`", "#{function.param_desc}");
  // VALUES("`feature`", "#{function.feature}");
  // }
  // }.toString();
  //
  // }
  //

  /**
   * 插入 <p>
   *
   * @return sql 语句
   */
  public String insert(Map<String, Object> parameter) {
    String sql = "insert into " + "funcs(`name`,type,project_id,owner_id,modify_time,create_time,param_desc,class_name,command,feature)"
            + " SELECT * FROM ( SELECT #{function.name} as name," + "#{function.type.type} as type" + ",#{function.projectId} as projectId"
            + ",#{function.ownerId} as ownerId" + ",#{function.modifyTime} as modifyTime" + ",#{function.createTime} as createTime" + ",#{function.desc} as `desc`"
            + ",#{function.className} as className" + ",#{function.command} as command" + ",#{function.feature} as feature" + " ) AS tb_tmp"
            + " WHERE NOT EXISTS (SELECT * from funcs WHERE type = 0 and `name` = #{function.name} )";
    return sql;

    // return new SQL() {
    // {
    // INSERT_INTO("funcs");
    // VALUES("name", "#{function.name}");
    // VALUES("type", EnumFieldUtil.genFieldStr("function.type",
    // FunctionType.class));
    // VALUES("project_id", "#{function.projectId}");
    // VALUES("owner_id", "#{function.ownerId}");
    // VALUES("modify_time", "#{function.modifyTime}");
    // VALUES("create_time", "#{function.createTime}");
    // VALUES("param_desc", "#{function.desc}");
    // VALUES("class_name", "#{function.className}");
    // VALUES("command", "#{function.command}");
    // VALUES("feature", "#{function.feature}");
    // WHERE("NOT EXISTS (SELECT * FROM (SELECT `name` AS func_name from
    // funcs WHERE type = 0 ) as sys_func_names WHERE func_name =
    // #{function.name}");
    // }
    // }.toString();
  }

  public static void main(String[] args) {
    FunctionSqlProvider provider = new FunctionSqlProvider();
    System.out.println(provider.insert(null));
  }

  /**
   * 插入函数和记录的关系 <p>
   *
   * @return sql 语句
   */
  public String insertFuncAndResources(Integer functionId, List<Integer> resourceIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO ");
    sb.append("funcs_resources");
    sb.append("(func_id, resource_id) ");
    sb.append("VALUES ");
    MessageFormat mf = new MessageFormat("(#'{'functionId}, #'{'resourceIds[{0}]})");
    for (int i = 0; i < resourceIds.size(); i++) {
      sb.append(mf.format(new Object[]{i}));
      if (i < resourceIds.size() - 1) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  /**
   * 查询函数详情 <p>
   *
   * @return sql 语句
   */
  public String queryDetail(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("r.*");
        // SELECT("fr.resource_id");
        SELECT("u.name as owner_name");
        FROM("funcs r");
        LEFT_OUTER_JOIN("user as u on u.id = r.owner_id");
        // LEFT_OUTER_JOIN("funcs_resources as fr on fr.func_id =
        // r.id");
        WHERE("r.id = #{functionId}");
      }
    }.toString();
  }

  /**
   * 查询函数详情（by name） <p>
   *
   * @return sql 语句
   */
  public String queryFuncsByName(Map<String, Object> parameter) {
    Set<String> functions = (Set<String>) parameter.get("funcs");
    return new SQL() {
      {
        SELECT("a.*");
        FROM("funcs a");
        if (functions != null && functions.size() > 0) {
          List<String> whereStr = new ArrayList<>();
          for (String name : functions) {
            whereStr.add("(a.name = '" + name + "' and a.project_id = #{projectId})");
          }
          WHERE(StringUtils.join(whereStr, " or "));
        }
      }
    }.toString();
  }

  /**
   * 查询资源列表 <p>
   *
   * @return sql 语句
   */
  public String findResourceIdsByFuncId(Integer functionId) {
    return new SQL() {
      {
        SELECT("resource_id");
        FROM("funcs_resources");
        WHERE("func_id = #{functionId}");
      }
    }.toString();
  }

  public String findResouceIdsByFuncs(List<Function> functions) {
    return new SQL() {
      {
        SELECT("distinct(b.resource_id)");
        FROM("funcs as a");
        INNER_JOIN("funcs_resources as b on a.id = b.func_id");
        if (functions != null && functions.size() > 0) {
          List<String> whereStr = new ArrayList<>();
          for (Function function : functions) {
            whereStr.add("(a.name = '" + function.getName() + "' and a.project_id = '" + function.getProjectId() + "')");
          }
          WHERE("(" + StringUtils.join(whereStr, " or ") + ")");
        }
      }
    }.toString();
  }

  /**
   * 删除某个函数 <p>
   *
   * @return sql 语句
   */
  public String deleteById(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM("funcs");
        WHERE("id = #{functionId}");
      }
    }.toString();
  }

  /**
   * 删除某个函数和资源的关系 <p>
   *
   * @return sql 语句
   */
  public String deleteFuncAndResources(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM("funcs_resources");
        WHERE("func_id = #{functionId}");
      }
    }.toString();
  }

  /**
   * 更新某个函数 <p>
   *
   * @return sql 语句
   */
  public String updateById(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE("funcs");
        SET("command = #{function.command}");
        SET("feature = #{function.feature}");
        SET("class_name = #{function.className}");
        SET("param_desc = #{function.desc}");
        SET("modify_time = #{function.modifyTime}");
        WHERE("id = #{function.id}");
      }
    }.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String deleteFuncAndResourcesByProjectId(Map<String, Object> parameter) {
    String subQuery = new SQL() {
      {
        SELECT("id");
        FROM("funcs");
        WHERE("project_id = #{projectId}");
        WHERE("type = " + EnumFieldUtil.genFieldStr("functionType", FunctionType.class));
      }
    }.toString();

    return new SQL() {
      {
        DELETE_FROM("funcs_resources");
        WHERE("func_id in (" + subQuery + ")");
      }
    }.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String deleteByProjectId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM("funcs");
        WHERE("project_id = #{projectId}");
        WHERE("type = " + EnumFieldUtil.genFieldStr("functionType", FunctionType.class));
      }
    }.toString();
  }

  public String countByProjectId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(1)");
        FROM("funcs");
        WHERE("project_id = #{projectId}");
        WHERE("type=1");
      }
    }.toString();
  }
}
