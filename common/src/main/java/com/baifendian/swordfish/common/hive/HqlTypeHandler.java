/*
 * Create Author  : dsfan
 * Create Date    : 2016-7-21
 * File Name      : HqlTypeHandler.java
 */

package com.baifendian.swordfish.common.hive;

import com.baifendian.swordfish.common.hive.beans.StmType;
import com.baifendian.swordfish.common.hive.exception.SqlException;
import org.antlr.runtime.tree.Tree;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseUtils;
import org.apache.hadoop.hive.ql.parse.SemanticAnalyzerFactory;

/**Hive语句类型处理类
 * <p>
 * 
 * @author : dsfan
 *
 * @date : 2016-7-21
 */
public class HqlTypeHandler {
    /**分析HQL语句类型
     * <br/>
     * <b>原理：</b>思路类似于{@link SemanticAnalyzerFactory#get(HiveConf conf, ASTNode tree)}
     * <p>
     *
     * @param astNode 抽象语法树的根节点
     * @return {@link StmType}
     */
    public static StmType analyzeStmType(ASTNode astNode) {
        // 获取非空根节点
        ASTNode rootNode = ParseUtils.findRootNonNullToken(astNode);
        if (rootNode.getToken() == null) {
            throw new SqlException("Empty Syntax Tree");
        } else {
            switch (rootNode.getToken().getType()) {
                case HiveParser.TOK_EXPLAIN:
                case HiveParser.TOK_EXPLAIN_SQ_REWRITE:
                    return StmType.EXPLAIN; // explain 语句

                case HiveParser.TOK_LOAD:
                    return StmType.LOAD; // load 语句

                case HiveParser.TOK_EXPORT:
                    return StmType.EXPORT; // export 语句

                case HiveParser.TOK_IMPORT:
                    return StmType.IMPORT; // import 语句

                case HiveParser.TOK_ALTERTABLE:
                case HiveParser.TOK_ALTERVIEW:
                case HiveParser.TOK_CREATEDATABASE:
                case HiveParser.TOK_CREATETABLE:
                case HiveParser.TOK_CREATEVIEW:
                case HiveParser.TOK_DROPDATABASE:
                case HiveParser.TOK_SWITCHDATABASE:
                case HiveParser.TOK_DROPTABLE:
                case HiveParser.TOK_DROPVIEW:
                case HiveParser.TOK_DESCDATABASE:
                case HiveParser.TOK_DESCTABLE:
                case HiveParser.TOK_DESCFUNCTION:
                case HiveParser.TOK_MSCK:
                case HiveParser.TOK_ALTERINDEX_REBUILD:
                case HiveParser.TOK_ALTERINDEX_PROPERTIES:
                case HiveParser.TOK_SHOWDATABASES:
                case HiveParser.TOK_SHOWTABLES:
                case HiveParser.TOK_SHOWCOLUMNS:
                case HiveParser.TOK_SHOW_TABLESTATUS:
                case HiveParser.TOK_SHOW_TBLPROPERTIES:
                case HiveParser.TOK_SHOW_CREATETABLE:
                case HiveParser.TOK_SHOWFUNCTIONS:
                case HiveParser.TOK_SHOWPARTITIONS:
                case HiveParser.TOK_SHOWINDEXES:
                case HiveParser.TOK_SHOWLOCKS:
                case HiveParser.TOK_SHOWDBLOCKS:
                case HiveParser.TOK_SHOW_COMPACTIONS:
                case HiveParser.TOK_SHOW_TRANSACTIONS:
                case HiveParser.TOK_SHOWCONF:
                case HiveParser.TOK_CREATEINDEX:
                case HiveParser.TOK_DROPINDEX:
                case HiveParser.TOK_ALTERTABLE_CLUSTER_SORT:
                case HiveParser.TOK_LOCKTABLE:
                case HiveParser.TOK_UNLOCKTABLE:
                case HiveParser.TOK_LOCKDB:
                case HiveParser.TOK_UNLOCKDB:
                case HiveParser.TOK_CREATEROLE:
                case HiveParser.TOK_DROPROLE:
                case HiveParser.TOK_GRANT:
                case HiveParser.TOK_REVOKE:
                case HiveParser.TOK_SHOW_GRANT:
                case HiveParser.TOK_GRANT_ROLE:
                case HiveParser.TOK_REVOKE_ROLE:
                case HiveParser.TOK_SHOW_ROLE_GRANT:
                case HiveParser.TOK_SHOW_ROLE_PRINCIPALS:
                case HiveParser.TOK_SHOW_ROLES:
                case HiveParser.TOK_ALTERDATABASE_PROPERTIES:
                case HiveParser.TOK_ALTERDATABASE_OWNER:
                case HiveParser.TOK_TRUNCATETABLE:
                case HiveParser.TOK_SHOW_SET_ROLE:
                case HiveParser.TOK_CREATEFUNCTION:
                case HiveParser.TOK_DROPFUNCTION:
                case HiveParser.TOK_ANALYZE:
                case HiveParser.TOK_CREATEMACRO:
                case HiveParser.TOK_DROPMACRO:
                    return StmType.DDL; // ddl 语句

                case HiveParser.TOK_UPDATE_TABLE:
                    return StmType.UPDATE; // update 语句

                case HiveParser.TOK_DELETE_FROM:
                    return StmType.DELETE; // delete 语句

                case HiveParser.TOK_QUERY:
                    Tree child = rootNode.getChild(1);
                    // 没有insert子句的情况
                    if (child.getType() == HiveParser.TOK_INSERT
                        && child.getChild(0).getType() == HiveParser.TOK_DESTINATION
                        && child.getChild(0).getChild(0).getType() == HiveParser.TOK_DIR) {
                        return StmType.SELECT; // select 语句
                    }
                    return StmType.INSERT; // insert 语句

                default:
                    return StmType.OTHER; // 其他类型语句
            }
        }
    }
}
