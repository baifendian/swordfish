
package com.baifendian.swordfish.dao.hive;

import com.baifendian.swordfish.common.hive.ConnectionInfo;
import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import com.baifendian.swordfish.dao.BaseDao;
import com.baifendian.swordfish.dao.config.HiveConfig;
import com.baifendian.swordfish.dao.exception.DaoSemanticException;
import com.baifendian.swordfish.dao.exception.SqlException;
import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.mapper.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.ParseUtils;
import org.apache.hive.jdbc.HiveConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HiveJdbcExec extends BaseDao {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static final int DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT = 10 * 1000;

    @Autowired
    HiveConfig hiveConfig;

    @Autowired
    HiveConnectionClient hiveConnectionClient;

    @Autowired
    UserMapper userMapper;

    ParseDriver pd = new ParseDriver();

    public boolean isTokQuery(String command) throws ParseException {
        try {
            if (command.startsWith("set") || command.startsWith("add")) {
                return false;
            }
            command = command.replace("`", ""); // 去除反引号
            ASTNode tree = pd.parse(command);
            tree = ParseUtils.findRootNonNullToken(tree);
            // System.out.println(tree.toStringTree());
            String tokType = tree.getToken().getText();
            if (tokType.equals("TOK_QUERY")) {
                if (command.toLowerCase().contains("insert")) {
                    return false;
                }
                return true;
            }
        } catch (ParseException e) {
            // tudo for not sql command
        }
        return false;
    }

    @Override
    public void init() {
        hiveConfig = MyHiveFactoryUtil.getInstance();
        hiveConnectionClient = hiveConfig.hiveConnectionClient();
        userMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(UserMapper.class);
    }

    public HiveConnectionClient getHiveConnectionClient() {
        return hiveConnectionClient;
    }

    /**
     * 获取连接信息
     * <p>
     *
     * @param userName
     * @return {@link ConnectionInfo}
     */
    public ConnectionInfo getConnectionInfo(String userName, String dbName) {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setUser(userName);
        //connectionInfo.setPassword(user.getPassword());
        connectionInfo.setUri(String.format(hiveConfig.getThriftUris(), dbName));
        return connectionInfo;
    }

    /**
     * 生成创建数据库的sql语句
     * <p>
     *
     * @param dbName
     * @param comment
     * @param location
     * @param parameters
     * @return {String}
     */
    private String generatioCreateDbSql(String dbName, String comment, String location, Map<String, String> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("CREATE DATABASE ");
        stringBuilder.append(dbName);
        if (StringUtils.isNotEmpty(comment)) {
            stringBuilder.append("COMMENT ");
            stringBuilder.append(comment);
        }
        if (StringUtils.isNotEmpty(location)) {
            stringBuilder.append("LOCATION ");
            stringBuilder.append(location);
        }
        if (parameters != null && parameters.size() > 0) {
            stringBuilder.append("WITH DBPROPERTIES (");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String property;
                if (i == 0) {
                    property = String.format("%s=%s", entry.getKey(), entry.getValue());
                } else {
                    property = String.format(",%s=%s", entry.getKey(), entry.getValue());
                }
                i++;
                stringBuilder.append(property);
            }
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }

    /**
     * 通过root用户创建数据库
     * <p>
     *
     * @return int
     */
    public ConnectionInfo getRootConnection() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setUser(hiveConfig.getRootUser());
        connectionInfo.setPassword(hiveConfig.getPassword());
        connectionInfo.setUri(String.format(hiveConfig.getThriftUris(), hiveConfig.getRootUser()));
        return connectionInfo;
    }

    /**
     * 执行一条sql语句 不支持use database
     * <p>
     *
     * @param sql
     * @param connectionInfo
     * @return
     */
    public void execSql(String sql, ConnectionInfo connectionInfo) throws SqlException {
        HiveConnection hiveConnection = null;
        try {
            hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
            Statement sta = hiveConnection.createStatement();
            sta.execute(sql);
            sta.close();
        } catch (Exception e) {
            if (e.toString().contains("SemanticException")) {
                LOGGER.error("execSql DaoSemanticException", e);
                throw (new DaoSemanticException(e.getMessage()));
            }
            if (e.toString().contains("TTransportException")) {
                LOGGER.error("Get TTransportException return a client", e);
                hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
                hiveConnection = null;
            }
            if (e.toString().contains("SocketException")) {
                LOGGER.error("SocketException clear pool", e);
                hiveConnectionClient.clear();
            }
            LOGGER.error("execSql SqlException", e);
            throw (new SqlException(e.getMessage()));
        } finally {
            hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
        }
    }

    public void useDatabase(String dbName, Statement sta) throws SQLException {
        String sql = String.format("use %s", dbName);
        sta.execute(sql);
    }

    /**
     * 执行多个sql 语句 并返回查询的语句
     * <p>
     * 
     * @param sqls
     * @param userId
     * @param dbName
     * @param isContinue
     * @param isGetLog
     * @return
     */
    /*
    public List<ExecResult> executeQuerys(List<String> sqls, int userId, String dbName, boolean isContinue, boolean isGetLog) {
        List<ExecResult> execResults = new ArrayList<>();
        HiveConnection hiveConnection = null;
        Statement sta = null;
        Thread logThread = null;
        ConnectionInfo connectionInfo = getConnectionInfo(userId, dbName);

        try {
            hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
            sta = hiveConnection.createStatement();
            List<String> logs = new ArrayList<>();
            if (isGetLog) {
                logThread = new Thread(new JdbcLogRunnable(sta, logs));
                logThread.setDaemon(true);
                logThread.start();
            }
            for (String sql : sqls) {
                if (sql.trim().startsWith("#") || sql.trim().startsWith("--")) {
                    continue;
                }
                ExecResult execResult = new ExecResult();
                execResult.setStm(sql);
                execResults.add(execResult);
                logs.clear();
                try {
                    if (isTokQuery(sql)) {

                        ResultSet res = sta.executeQuery(sql);
                        ResultSetMetaData resultSetMetaData = res.getMetaData();
                        int count = resultSetMetaData.getColumnCount();

                        List<String> colums = new ArrayList<>();
                        for (int i = 1; i <= count; i++) {
                            colums.add(resultSetMetaData.getColumnLabel(i));
                        }
                        execResult.setTitles(colums);

                        List<List<String>> datas = new ArrayList<>();
                        while (res.next()) {
                            List<String> values = new ArrayList<>();
                            for (int i = 1; i <= count; i++) {
                                values.add(res.getString(i));
                            }
                            datas.add(values);
                        }

                        execResult.setValues(datas);

                    } else {
                        sta.execute(sql);
                    }
                    for (String log : logs) {
                        execResult.getLogs().add(log);
                    }
                } catch (Exception e) {
                    if (e.toString().contains("SemanticException")) {
                        LOGGER.error("executeQuery DaoSemanticException", e);
                        if (!isContinue) {
                            throw (new DaoSemanticException(e.getMessage()));
                        }
                    }
                    if (e.toString().contains("TTransportException")) {
                        LOGGER.error("Get TTransportException return a client", e);
                        hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
                        hiveConnection = null;
                        throw new Exception(e);
                    }
                    if (e.toString().contains("SocketException")) {
                        LOGGER.error("SocketException clear pool", e);
                        hiveConnectionClient.clear();
                        throw new Exception(e);
                    }
                    LOGGER.error("executeQuery Exception", e);
                    if (!isContinue) {
                        throw new Exception(e);
                    }
                    if (isGetLog) {
                        execResult.getLogs().add(e.getMessage());
                    }
                }
            }
            try {
                if (logThread != null) {
                    logThread.interrupt();
                    logThread.join(DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT);
                }
                logThread = null;
            } catch (Exception e) {
            }
            useDatabase(dbName, sta);
            sta.close();
            sta = null;
        } catch (Exception e) {
            LOGGER.error("executeQuerys exception", e);
            throw (new SqlException(e.getMessage()));
        } finally {
            try {
                if (logThread != null) {
                    logThread.interrupt();
                    logThread.join(DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT);
                }
                if (sta != null) {
                    sta.close();
                }
            } catch (Exception e) {
            }
            hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
        }

        return execResults;
    }
*/

    /**
     * 执行一个sql 语句 并返回查询的语句
     * <p>
     * 
     * @param sql
     * @param connectionInfo
     * @return
     */
    public ExecResult executeQuery(String sql, ConnectionInfo connectionInfo) throws SqlException {
        HiveConnection hiveConnection = null;
        try {
            hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
            Statement sta = hiveConnection.createStatement();
            ExecResult execResult = new ExecResult();
            execResult.setStm(sql);
            ResultSet res = sta.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = res.getMetaData();
            int count = resultSetMetaData.getColumnCount();

            List<String> colums = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                colums.add(resultSetMetaData.getColumnLabel(i));
            }
            execResult.setTitles(colums);

            List<List<String>> datas = new ArrayList<>();
            while (res.next()) {
                List<String> values = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    values.add(res.getString(i));
                }
                datas.add(values);
            }

            execResult.setValues(datas);
            sta.close();
            return execResult;
        } catch (Exception e) {
            if (e.toString().contains("SemanticException")) {
                LOGGER.error("execSql DaoSemanticException", e);
                throw (new DaoSemanticException(e.getMessage()));
            }
            if (e.toString().contains("TTransportException")) {
                LOGGER.error("Get TTransportException return a client", e);
                hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
                hiveConnection = null;
            }
            if (e.toString().contains("SocketException")) {
                LOGGER.error("SocketException clear pool", e);
                hiveConnectionClient.clear();
            }
            LOGGER.error("execSql SqlException", e);
            throw (new SqlException(e.getMessage()));
        } finally {
            hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
        }
    }

}
