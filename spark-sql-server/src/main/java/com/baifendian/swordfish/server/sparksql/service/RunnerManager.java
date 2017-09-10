package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultRet;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
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
  private Map<String, JobInfo> jobInfoMap = new ConcurrentHashMap<>();

  private String tmpPath = "/tmp/swordfish/spark-sql/";

  private static final String CREATE_FUNCTION_FORMAT = "create temporary function {0} as ''{1}''";
  private static final String HADOOP_GET_FORMAT = "hadoop fs -get {0} {1}";

  public RunnerManager() {
    this.sqlExecutorService = Executors.newSingleThreadExecutor();

    startClean();

    File file = new File(tmpPath);
    file.mkdirs();
  }

  public List<String> createUdf(String jobId, List<UdfInfo> udfs) {
    List<String> result = new ArrayList<>();

    File file = new File(tmpPath + jobId);
    file.mkdirs();

    Runtime runtime = Runtime.getRuntime();
    for (UdfInfo udfInfo : udfs) {
      for (String path : udfInfo.getLibJars()) {
        long curTime = System.currentTimeMillis();
        String jarLocalFile = tmpPath + jobId + "/" + curTime;
        String hadoopStr = MessageFormat
            .format(HADOOP_GET_FORMAT, path, jarLocalFile);
        logger.info("hadoop string:{}", hadoopStr);
        try {
          runtime.exec(hadoopStr);
        } catch (IOException e) {
          logger.info("Exec script error. cmd:{}", hadoopStr);
          throw new RuntimeException(e);
        }

        result.add("add jar "+jarLocalFile);
      }
      String funcStr = MessageFormat
          .format(CREATE_FUNCTION_FORMAT, udfInfo.getFunc(), udfInfo.getClassName());
      result.add(funcStr);
      logger.info("create function:{}", funcStr);
    }

    return result;
  }

  public boolean executeEtlSql(String jobId, List<UdfInfo> udfs, List<String> sqls, long stopTime) {
    logger.info("Begin running spark sql, jobid:{}", jobId);
    SparkSqlExec sparkSqlExec = new SparkSqlExec(jobId, createUdf(jobId, udfs), sqls, null,
        stopTime);
    Future future = sqlExecutorService.submit(sparkSqlExec);
    jobInfoMap.put(jobId, new JobInfo(stopTime, sparkSqlExec, future));
    return true;
  }

  public boolean executeAdhocSql(String jobId, List<UdfInfo> udfs, List<String> sqls, long stopTime,
      int queryLimit) {
    logger.info("Begin running adHoc spark sql, jobid:{}", jobId);
    SparkSqlExec sparkSqlExec = new SparkSqlExec(jobId, createUdf(jobId, udfs), sqls, queryLimit,
        stopTime);
    Future future = sqlExecutorService.submit(sparkSqlExec);
    jobInfoMap.put(jobId, new JobInfo(stopTime, sparkSqlExec, future));

    return true;
  }

  public AdhocResultRet getAdHocResult(String jobId, int index) {
    logger.info("Begin get adHoc spark sql result, jobid:{}", jobId);
    AdhocResultRet adhocResultRet = new AdhocResultRet();

    JobInfo jobInfo = jobInfoMap.get(jobId);
    if (jobInfo == null) {
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
      adhocResultRet.setResultData(jobInfo.execInfo.getAdHocResult(index));

      return adhocResultRet;
    } catch (Throwable e) {
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
    JobInfo jobInfo = jobInfoMap.get(jobId);
    if (jobInfo == null || jobInfo.isDone()) {
      logger.info("job id:{} is end.", jobId);
      return false;
    }

    jobInfo.cancel();

    return true;
  }

  public void startClean() {
    Thread thread = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(1000 * 60 * 30L);
        } catch (InterruptedException e) {
          logger.error("InterruptedException", e);
        }
        logger.info("Begin clean job");
        for (Map.Entry<String, JobInfo> entry : jobInfoMap.entrySet()) {
          JobInfo jobInfo = entry.getValue();
          if (jobInfo.canClean()) {
            jobInfoMap.remove(entry.getKey());

            // 清理本地缓存Jar
            cleanPath(entry.getKey());

          }
        }
        logger.info("End clean job");
      }
    });
    thread.start();
  }

  public void cleanPath(String jobId) {
    File file = new File(tmpPath + jobId);
    String[] nameList = file.list();
    if (nameList != null) {
      for (String name : nameList) {
        File newFile = new File(tmpPath + jobId + "/" + name);
        newFile.delete();
      }
    }
    file.delete();
  }
}

class JobInfo {

  public JobInfo(long stopTime, SparkSqlExec execInfo, Future future) {
    this.stopTime = stopTime;
    this.execInfo = execInfo;
    this.future = future;
  }

  boolean isDone() {
    return future.isDone();
  }

  void cancel() {
    execInfo.cancel();
  }

  boolean canClean() {
    if (System.currentTimeMillis() > stopTime) {
      // todo timeout, kill
      return true;
    }

    if (!isDone()) {
      return false;
    }
    if (!execInfo.isAdHoc()) {
      return true;
    }

    return execInfo.isAdHocEnd();
  }

  long stopTime;
  SparkSqlExec execInfo;
  Future future;

}
