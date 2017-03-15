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

import com.baifendian.swordfish.dao.mysql.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * Created by caojingwei on 16/7/25.
 */
public class UserMapperSQL {

    public String insert(final User user) {
        return new SQL() {
            {
                INSERT_INTO("user");
                // VALUES("id", "#{user.id}");
                VALUES("name", "#{user.name}");
                VALUES("email", "#{user.email}");
                VALUES("password", "#{user.password}");
                if (StringUtils.isNotEmpty(user.getPhone())){
                    VALUES("phone", "#{user.phone}");
                }
                VALUES("create_time", "#{user.createTime}");
                VALUES("modify_time", "#{user.modifyTime}");
                VALUES("status", "#{user.status}");

                if (user.getTenantId() != null){
                    VALUES("tenant_id", "#{user.tenantId}");
                    VALUES("join_time", "#{user.joinTime}");
                }

                VALUES("role", "#{user.roleType}");
            }
        }.toString();
    }

    public String query(final User user,final boolean isJoin) {
        return new SQL() {
            {
                SELECT("u.id as id,u.`name` as `name`,u.email as email,u.phone as phone,u.create_time as create_time,u.modify_time as modify_time,u.password as password,u.status as status,u.is_system_user as is_system_user");
                FROM("user u");
                if (user.getId() !=null) {
                    WHERE("u.id = #{user.id}");
                }
                if (StringUtils.isNotEmpty(user.getName())) {
                    WHERE("u.name = #{user.name}");
                }
                if (StringUtils.isNotEmpty(user.getPassword())) {
                    WHERE("u.password = #{user.password}");
                }
                if (StringUtils.isNotEmpty(user.getEmail())) {
                    WHERE("u.email = #{user.email}");
                }
                if (StringUtils.isNotEmpty(user.getPhone())) {
                    WHERE("u.phone = #{user.phone}");
                }
                if (user.getStatus() !=null) {
                    WHERE("u.status = #{user.status}");
                }
                //WHERE("u.is_system_user = #{user.isSystemUser}");
            }
        }.toString();
    }

    public String updatePswById(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE("user");
                SET("password=#{user.password}");
                SET("modify_time=#{user.modifyTime}");
                WHERE("id = #{user.id}");
            }
        }.toString();
    }

    public String findById(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM("user");
                WHERE("id = #{id}");
            }
        }.toString();
    }

    public String deleteByEmail(Map<String, Object> parameter){
        return new SQL(){{
            DELETE_FROM("user");
            WHERE("email = #{email}");
        }}.toString();
    }

    public String queryById(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("u.id AS id, u.`name` AS `name`, u.email AS email, u.phone AS phone, u.create_time AS create_time, u.modify_time AS modify_time, u.tenant_id AS tenant_id, u.join_time AS join_time, u.password AS password, u.status AS status, u.role AS role");
                SELECT("t.`name` as tenant_name");
                FROM("user u");
                LEFT_OUTER_JOIN("tenant t on u.tenant_id = t.id");
                WHERE("u.id = #{userId}");
            }
        }.toString();
    }

    public String queryByEmail(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("u.id AS id, u.`name` AS `name`, u.email AS email, u.phone AS phone, u.create_time AS create_time, u.modify_time AS modify_time, u.tenant_id AS tenant_id, u.join_time AS join_time, u.password AS password, u.status AS status, u.role AS role");
                SELECT("t.`name` as tenant_name");
                FROM("user u");
                LEFT_OUTER_JOIN("tenant t on u.tenant_id = t.id");
                WHERE("u.email = #{email}");
            }
        }.toString();
    }

    public String findByName(Map<String, Object> parameter){
        return new SQL() {
            {
                SELECT("u.id AS id, u.`name` AS `name`, u.email AS email, u.phone AS phone, u.create_time AS create_time, u.modify_time AS modify_time, u.tenant_id AS tenant_id, u.join_time AS join_time, u.password AS password, u.status AS status, u.role AS role");
                FROM("user u");
                WHERE("u.name = #{name}");
            }
        }.toString();
    }

    public String updateStateById(final int id,final int status){
        return new SQL() {
            {
                UPDATE("user");
                SET("status=#{status}");
                WHERE("id = #{id}");
            }
        }.toString();
    }
    
    public String updateForJoinTenant(final Integer id, final Integer tenant_id) {
    	return new SQL() {
    		{
    			UPDATE("user");
    			SET("tenant_id = #{tenant_id}");
                WHERE("id = #{id}");
    		}
    	}.toString();
    }
}
