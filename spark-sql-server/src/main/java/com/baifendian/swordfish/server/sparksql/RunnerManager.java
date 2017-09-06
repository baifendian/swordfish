package com.baifendian.swordfish.server.sparksql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *
 */
public class RunnerManager {
  private static Logger logger = LoggerFactory.getLogger(RunnerManager.class.getName());

  private final ExecutorService sqlExecutorService;

  public RunnerManager() {
    this.sqlExecutorService = Executors.newSingleThreadExecutor();
  }

  public void submitSql(){

  }

  /**
   * 提交即席查询任务
   */
  public void submitAdHoc(int execId) {
    List<String> sqls = new ArrayList<>();
    sqls.add("select count(1) from ods.tbs_ods_twfb");
    sqls.add("select count(1) from ods.tbs_ods_twfb");
    sqls.add("select count(1) from ods.tbs_ods_twfb");
    SparkSqlExec sparkSqlExec = new SparkSqlExec(sqls, true, 1000, 1L, logger);

    Future future = sqlExecutorService.submit(sparkSqlExec);
    while (!future.isDone()) {
      try {
        future.get(1, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        sparkSqlExec.cancel();
      }
      logger.info("stop....");
    }
  }

  public void getAdHocResult(){

  }

  public static void main(String[] args) {
    RunnerManager runnerManager = new RunnerManager();
    runnerManager.submitAdHoc(1);
    runnerManager.sqlExecutorService.shutdown();
  }
}
