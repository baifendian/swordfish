package com.baifendian.swordfish.dao.hive;

import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by wenting on 11/24/16.
 */
public class JdbcLogRunnable implements  Runnable{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final int DEFAULT_QUERY_PROGRESS_INTERVAL = 1000;
    private List<String> logs;
    private HiveStatement hiveStatement;

    public JdbcLogRunnable(Statement statement, List<String> logs) {
        if (statement instanceof HiveStatement) {
            this.hiveStatement = (HiveStatement) statement;
        }
        this.logs = logs;
    }

    public void run() {
        while (true) {
            try {
                // fetch the log periodically and output to beeline console
                for (String log : hiveStatement.getQueryLog()) {
                    logs.add(log);
                    //LOGGER.info(log);
                }
                Thread.sleep(DEFAULT_QUERY_PROGRESS_INTERVAL);
            } catch (SQLException e) {
                logs.add(e.getMessage());
                LOGGER.warn(e.getMessage());
                return;
            } catch (InterruptedException e) {
                showRemainingLogsIfAny(hiveStatement, logs);
                return;
            }
        }
    }

    private void showRemainingLogsIfAny(Statement statement, List<String> logs) {
        List<String> logsTemp;
        do {
            try {
                logsTemp = hiveStatement.getQueryLog();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                return;
            }
            for (String log : logsTemp) {
                LOGGER.info(log);
                //logs.add(log);
            }
        } while (logsTemp.size() > 0);
    }
}
