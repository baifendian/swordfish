/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.dao.mysql.mapper;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * @author : liujin
 * @date : 2017-03-10 16:01
 */
public class MasterServerMapperSQL {
    public String query(){
        return new SQL(){{
            SELECT("*");
            FROM("master_server");
        }}.toString();
    }

    public String insert(Map<String,Object> parameter){
        return new SQL(){{
            INSERT_INTO("master_server");
            VALUES("host","#{masterServer.host}");
            VALUES("port","#{masterServer.port}");
            VALUES("update_time","#{masterServer.updateTime}");
        }}.toString();
    }

    public String delete(){
        return new SQL(){{
            DELETE_FROM("master_server");
        }}.toString();
    }

}
