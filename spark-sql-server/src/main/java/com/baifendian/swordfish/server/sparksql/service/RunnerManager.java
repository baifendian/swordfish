package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultRet;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 */
public class RunnerManager {

  private static Logger logger = LoggerFactory.getLogger(RunnerManager.class.getName());

  private final ExecutorService sqlExecutorService;
  private Map<String, Map.Entry<SparkSqlExec, Future>> jobInfo = new ConcurrentHashMap<>();

  /**
   * 查询限制，默认为 1000
   */
  private static int defaultQueryLimit = 1000;

  public RunnerManager() {
    this.sqlExecutorService = Executors.newSingleThreadExecutor();
  }

  public List<String> createUdf(List<UdfInfo> udfs) {
    List<String> udfCmds = new ArrayList<>();

    return udfCmds;
  }

  public boolean executeEtlSql(String jobId, List<UdfInfo> udfs, List<String> sqls, long stopTime) {
    logger.info("Begin running spark sql, jobid:{}", jobId);
    SparkSqlExec sparkSqlExec = new SparkSqlExec(jobId, createUdf(udfs), sqls, null, stopTime);
    Future future = sqlExecutorService.submit(sparkSqlExec);
    jobInfo.put(jobId, new SimpleEntry<>(sparkSqlExec, future));
    return true;
  }

  public boolean executeAdhocSql(String jobId, List<UdfInfo> udfs, List<String> sqls, long stopTime, int queryLimit) {
    logger.info("Begin running adHoc spark sql, jobid:{}", jobId);
    SparkSqlExec sparkSqlExec = new SparkSqlExec(jobId, createUdf(udfs), sqls, queryLimit, stopTime);
    Future future = sqlExecutorService.submit(sparkSqlExec);
    jobInfo.put(jobId, new SimpleEntry<>(sparkSqlExec, future));

    return true;
  }

  public AdhocResultRet getAdHocResult(String jobId, int index) {
    logger.info("Begin get adHoc spark sql result, jobid:{}", jobId);
    AdhocResultRet adhocResultRet = new AdhocResultRet();

    Map.Entry<SparkSqlExec, Future> entry = jobInfo.get(jobId);
    if (entry == null) {
      logger.info("job id:{} is end.", jobId);
      RetInfo retInfo = new RetInfo();
      retInfo.setStatus(1);
      retInfo.setMsg("job id is end");
      adhocResultRet.setRetInfo(retInfo);
      return adhocResultRet;
    }
    try {
      RetInfo retInfo = new RetInfo();
      retInfo.setStatus(0);
      adhocResultRet.setRetInfo(retInfo);
      adhocResultRet.setResultData(entry.getKey().getAdHocResult(index));

      return adhocResultRet;
    } catch (Throwable e){
      logger.info("Get result failed.", e);
      RetInfo retInfo = new RetInfo();
      retInfo.setStatus(1);
      retInfo.setMsg(e.getMessage());
      adhocResultRet.setRetInfo(retInfo);

      return adhocResultRet;
    }
  }

  public boolean cancelExecFlow(String jobId) {
    logger.info("Begin cancel spark sql result, jobid:{}", jobId);
    Map.Entry<SparkSqlExec, Future> entry = jobInfo.get(jobId);
    if (entry == null || entry.getValue().isDone()) {
      logger.info("job id:{} is end.", jobId);
      return false;
    }

    entry.getKey().cancel();
    return true;
  }

}
