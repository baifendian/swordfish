/*
 * Create Author  : dsfan
 * Create Date    : 2016年7月29日
 * File Name      : HqlInfoHandler.java
 */

package com.baifendian.swordfish.common.hive;

import com.baifendian.swordfish.common.hive.beans.FunctionInfo;
import com.baifendian.swordfish.common.hive.beans.StmInfo;
import com.baifendian.swordfish.common.hive.beans.TableInfo;
import com.baifendian.swordfish.common.utils.PlaceholderUtil;
import com.baifendian.swordfish.common.utils.TimePlaceholderUtil;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.BaseSemanticAnalyzer;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Hive语句信息处理类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年7月29日
 */
public class HqlInfoHandler {

    /** 当前查询的hive数据库名，默认为：default */
    private String nowQueryDB = "default";

    /** 多个语句信息的汇总 */
    private final List<StmInfo> stmInfos;

    public HqlInfoHandler(String nowQueryDB) {
        this.nowQueryDB = nowQueryDB;
        this.stmInfos = new ArrayList<>();
    }

    public void setNowQueryDB(String dbName) {
        nowQueryDB = dbName;
    }

    /**
     * 解析一个 hql 语句 <br/>
     * 如果存在切换db时，不支持多线程操作
     * <p>
     *
     * @param stm
     *            hql 语句
     */
    public StmInfo parseStm(String stm) {
        // 初始化 stmInfo
        StmInfo stmInfo = new StmInfo();
        stmInfo.setInputTables(new HashSet<>());
        stmInfo.setOuputTables(new HashSet<>());
        stmInfo.setFunctions(new HashSet<>());

        // 解析语句的抽象语法树
        stm = stm.replace("`", ""); // 去除反引号
        parseAst(AstHandler.parseStm2Ast(stm), stmInfo);

        return stmInfo;
    }

    public List<StmInfo> parseStms(List<String> stms) {
        List<StmInfo> stmInfos = new ArrayList<>();
        for (String stm : stms) {
            stm = PlaceholderUtil.resolvePlaceholdersConst(stm, "NULL");
            stm = TimePlaceholderUtil.resolvePlaceholdersConst(stm, "NULL");
            stmInfos.add(parseStm(stm));
        }
        return stmInfos;
    }

    /**
     * 解析一个 hql 的抽象语法树
     * <p>
     *
     * @param ast
     *            {@link ASTNode}
     */
    private void parseAst(ASTNode ast, StmInfo stmInfo) {
        // 解析的预处理
        prepareToParseCurrentNodeAndChilds(ast);

        // 解析子节点
        parseChildNodes(ast, stmInfo);

        // 解析当前节点
        parseCurrentNode(ast, stmInfo);
    }

    /**
     * 解析的预处理
     * <p>
     *
     * @param ast
     *            {@link ASTNode}
     */
    private void prepareToParseCurrentNodeAndChilds(ASTNode ast) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                // 切换数据库
                case HiveParser.TOK_SWITCHDATABASE:
                    nowQueryDB = ast.getChild(0).getText();
                    break;
            }
        }
    }

    /**
     * 递归解析所有子节点，从最内层的语法树开始向上解析
     * <p>
     *
     * @param ast
     *            {@link ASTNode}
     */
    private void parseChildNodes(ASTNode ast, StmInfo stmInfo) {
        int numCh = ast.getChildCount();
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                parseAst(child, stmInfo);
            }
        }
    }

    /**
     * 解析当前节点
     * <p>
     * 
     * @param ast
     *            {@link ASTNode}
     */
    private void parseCurrentNode(ASTNode ast, StmInfo stmInfo) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                case HiveParser.TOK_TAB: // 输出表
                    handleOutTable(ast, stmInfo);
                    break;

                case HiveParser.TOK_TABREF: // 输入表
                    handleInTable(ast, stmInfo);
                    break;
                case HiveParser.TOK_FUNCTION: // 函数
                    handleFunction(ast, stmInfo);
                    break;
                case HiveParser.TOK_FUNCTIONDI:
                    handleFunction(ast, stmInfo);
                    break;
                case HiveParser.TOK_FUNCTIONSTAR:
                    handleFunction(ast, stmInfo);
                    break;
            }
        }
    }

    /**
     * 处理输入表
     * <p>
     *
     * @param ast
     *            {@link ASTNode}
     */
    private void handleInTable(ASTNode ast, StmInfo stmInfo) {
        ASTNode tabTree = (ASTNode) ast.getChild(0);
        TableInfo table = new TableInfo();
        if (tabTree.getChildCount() == 1) { // 只有表名的情况
            table.setDbName(nowQueryDB);
            table.setTableName(BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0)));
        } else { // 数据库名.表名 的情况
            table.setDbName(BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0)));
            table.setTableName(BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(1)));
        }
        stmInfo.getInputTables().add(table);
    }

    /**
     * 处理函数调用
     * <p>
     *
     * @param ast
     *            {@link ASTNode}
     */
    private void handleFunction(ASTNode ast, StmInfo stmInfo) {
        ASTNode tabTree = (ASTNode) ast.getChild(0);
        FunctionInfo function = new FunctionInfo();
        String text = tabTree.getText();
        String[] texts = text.split("\\.");
        if (texts.length > 1) {
            function.setDbName(texts[0]);
            function.setIfDefaultDb(false);
            function.setFunctionName(texts[1]);
        } else {
            function.setDbName(nowQueryDB);
            function.setIfDefaultDb(true);
            function.setFunctionName(text);
        }
        stmInfo.getFunctions().add(function);

    }

    /**
     * 处理输出表
     * <p>
     *
     * @param ast
     *            {@link ASTNode}
     */
    private void handleOutTable(ASTNode ast, StmInfo stmInfo) {
        // 表名
        String tableName = BaseSemanticAnalyzer.getUnescapedName((ASTNode) ast.getChild(0));

        TableInfo table = new TableInfo();
        table.setDbName(nowQueryDB);
        table.setTableName(tableName);
        stmInfo.getOuputTables().add(table);
    }

    /**
     * getter method
     * 
     * @see HqlInfoHandler#stmInfos
     * @return the stmInfos
     */
    public List<StmInfo> getStmInfos() {
        return stmInfos;
    }

    public static void main(String[] args) throws Exception {
        String dbName = "bdi";
        String sql1 = "insert into table appkey_info select x from bma.user_action;";
        String sql2 = "use bma";
        String sql3 = "select a.count(0) as num from api.appkey_info";
        String sql4 = "select fun1(aaa) from user_action";
        String sql5 = " SELECT appkey,sid, count(distinct(page_name)) page_depth FROM bdi_mid_app_visit GROUP BY appkey ,sid";

        List<String> sqls = new ArrayList<>();
        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql3);
        sqls.add(sql4);
        sqls.add(sql5);

        HqlInfoHandler hqlInfoHandler = new HqlInfoHandler(dbName);
        System.out.println(hqlInfoHandler.parseStm(sql1));
        System.out.println(hqlInfoHandler.parseStm(sql2));
        System.out.println(hqlInfoHandler.parseStm(sql3));
        System.out.println(hqlInfoHandler.parseStm(sql4));
        System.out.println(hqlInfoHandler.parseStm(sql5));

        // System.out.println(hqlInfoHandler.parseStms(sqls));

    }
}
