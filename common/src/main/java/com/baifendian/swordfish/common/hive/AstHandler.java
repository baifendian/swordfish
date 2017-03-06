/*
 * Create Author  : dsfan
 * Create Date    : 2016-7-21
 * File Name      : AstHandler.java
 */

package com.baifendian.swordfish.common.hive;

import com.baifendian.swordfish.common.hive.exception.SqlException;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

/**抽象语法树处理类
 * <p>
 * 
 * @author : dsfan
 *
 * @date : 2016-7-21
 */
public class AstHandler {
    /**获取hive语句的抽象语法树
    * <p>
    *
    * @return {@link ASTNode}
    */
    public static ASTNode parseStm2Ast(String stm) {
        ParseDriver parseDriver = new ParseDriver();
        try {
            return parseDriver.parse(stm);
        } catch (ParseException e) {
            throw new SqlException("Parse hql error");
        }
    }
    public static void main(String[] args) throws Exception {
        String sql = " SELECT appkey,sid, count(distinct(page_name)) page_depth FROM bdi_mid_app_visit GROUP BY appkey ,sid";
        System.out.println(AstHandler.parseStm2Ast(sql).toStringTree());
    }
}
