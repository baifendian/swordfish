package com.baifendian.swordfish.dao.mysql.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 *  workflow 执行的信息操作
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月24日
 */
public class ExecNodeLogMapperProvider {

    public static final String TABLE_NAME = "exec_node_log";

    public String insert(Map<String, Object> parameter){
        return new SQL(){{
            INSERT_INTO(TABLE_NAME);
            VALUES("log_id", "#{execNodeLog.logId}");
            VALUES("log_info", "#{execNodeLog.logInfo}");
            VALUES("start_byte", "#{execNodeLog.startByte}");
            VALUES("end_byte", "#{execNodeLog.endByte}");
            VALUES("upload_time", "#{execNodeLog.uploadTime}");
        }}.toString();
    }

    public String select(Long logId) {
        return new SQL(){{
            SELECT("*");
            FROM(TABLE_NAME);
            WHERE("log_id = #{logId}");
            ORDER_BY("upload_time asc");
        }}.toString();
    }

    public String selectPagination(Map<String, Object> parameter) {
        String sqlTemp = new SQL(){{
            SELECT("*");
            FROM(TABLE_NAME);
            WHERE("log_id = #{logId}");
            ORDER_BY("upload_time asc");
        }}.toString();
        String limit = " LIMIT #{start}, #{length}";
        return  sqlTemp + limit;
    }

    public String selectCount(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("COUNT(0)");
            FROM(TABLE_NAME);
            WHERE("log_id = #{logId}");
        }}.toString();
    }

    public String deleteByLogId(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("log_id = #{logId}");
            }
        }.toString();
    }

}
