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

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.Map;

/**
 * Created by caojingwei on 16/8/22.
 */
public class SessionMapperSQL {
    public String findById(Map<String,Object> parameter){
        return new SQL(){{
            SELECT("*");
            FROM("session");
            WHERE("id = #{sessionId}");
        }}.toString();
    }

    public String insert(Map<String,Object> parameter){
        return new SQL(){{
            INSERT_INTO("session");
            VALUES("id","#{session.id}");
            VALUES("user_id","#{session.userId}");
            VALUES("ip","#{session.ip}");
            VALUES("start_time","#{session.startTime}");
            VALUES("end_time","#{session.endTime}");
            VALUES("is_remember","#{session.isRemember}");
        }}.toString();
    }

    public String delete(final String sessionId, final int endTime){
        return new SQL(){{
            DELETE_FROM("session");
            if (StringUtils.isNotEmpty(sessionId)) {
                WHERE("id = #{sessionId}");
            }else{
                WHERE("end_time < #{endTime}");
            }
        }}.toString();
    }

    public String update(final String sessionId, final int endTime, final int startTime) {
        return new SQL() {
            {
                UPDATE("session");
                SET("end_time=#{endTime}");
                SET("start_time=#{startTime}");
                WHERE("id = #{sessionId}");
            }
        }.toString();
    }
}
