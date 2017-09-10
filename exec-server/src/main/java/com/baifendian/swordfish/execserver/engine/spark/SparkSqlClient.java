package com.baifendian.swordfish.execserver.engine.spark;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.rpc.AdhocResultInfo;
import com.baifendian.swordfish.rpc.AdhocResultRet;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.SparkSqlService;
import com.baifendian.swordfish.rpc.SparkSqlService.Iface;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkSqlClient {

  private static Logger LOGGER = LoggerFactory.getLogger(SparkSqlClient.class.getName());

  private BlockingQueue<SparkSqlService.Iface> clientQueue;

  private Map<String, SparkSqlService.Iface> jobInfo = new ConcurrentHashMap<>();

  static public void init(String hosts, int port) {
    instance = new SparkSqlClient(hosts, port);
  }

  static public SparkSqlClient getInstance() {
    return instance;
  }

  static private SparkSqlClient instance;

  public SparkSqlClient(String hosts, int port) {
    String[] hostArray = hosts.split(",");
    clientQueue = new ArrayBlockingQueue<>(hostArray.length);

    for (String host : hostArray) {
      clientQueue.add(new SparkSqlClientDecorator(host, port));
    }
  }

  public boolean execEtl(String jobId, List<UdfInfo> udfs, List<String> sql, int remainTime,
      Logger logger) {

    return execute(jobId, remainTime, logger,
        (client) -> {
          logger.info("Begin run sql");
          try {
            return client.execEtl(jobId, udfs, sql, remainTime).getStatus() == 0;
          } catch (TException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private SparkSqlService.Iface getClient(int remainTime, Logger logger) {
    SparkSqlService.Iface client;
    try {
      client = clientQueue.poll(remainTime, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.info("queue interrupted", e);
      return null;
    }

    return client;
  }

  boolean executeAdhoc(String jobId, List<UdfInfo> udfs, List<String> sql,
      ResultCallback resultCallback, int queryLimit, int remainTime, Logger logger) {
    return execute(jobId, remainTime, logger,
        (client) -> {
          logger.info("Begin run sql");
          return execAdhocSql(client, jobId, udfs, sql, resultCallback, queryLimit, remainTime,
              logger);
        });
  }

  private boolean execAdhocSql(SparkSqlService.Iface client, String jobId, List<UdfInfo> udfs,
      List<String> sql,
      ResultCallback resultCallback, int queryLimit, int remainTime, Logger logger) {
    Date startTime = new Date();
    RetInfo retInfo;
    try {
      retInfo = client.execAdhoc(jobId, udfs, sql, queryLimit, remainTime);
    } catch (TException e) {
      logger.info("TException", e);
      return false;
    }
    jobInfo.put(jobId, client);
    for (int i = 0; i < sql.size(); ++i) {
      ExecResult execResult = new ExecResult();
      execResult.setIndex(i);
      execResult.setStm(sql.get(i));
      AdhocResultInfo adhocResultInfo;
      try {
        AdhocResultRet adhocResultRet = client.getAdhocResult(jobId, i);
        if (adhocResultRet.retInfo.getStatus() == 0) {
          adhocResultInfo = adhocResultRet.getResultData();
        } else {
          logger.info("Get result error. {}", adhocResultRet.getRetInfo().getMsg());

          execResult.setStatus(FlowStatus.FAILED);
          resultCallback.handleResult(execResult, startTime, new Date());

          continue;
        }
      } catch (Throwable e) {
        logger.info("TException", e);
        execResult.setStatus(FlowStatus.FAILED);
        resultCallback.handleResult(execResult, startTime, new Date());
        continue;
      }

      execResult.setStatus(FlowStatus.valueOfType(adhocResultInfo.getStatus()));
      execResult.setTitles(adhocResultInfo.getTitles());
      execResult.setValues(adhocResultInfo.getValues());
      resultCallback.handleResult(execResult, startTime, new Date());
    }

    jobInfo.remove(jobId);

    return retInfo.getStatus() == 0;
  }

  public boolean cancel(String jobId, Logger logger) {
    SparkSqlService.Iface client = jobInfo.get(jobId);
    if (client == null) {
      logger.info("Job:{} is end.", jobId);
      return false;
    }

    try {
      client.cancelExecFlow(jobId);
    } catch (TException e) {
      logger.info("TException", e);
      return false;
    }

    return true;
  }

  private boolean execute(String jobId, int remainTime, Logger logger,
      Function<Iface, Boolean> function) {
    SparkSqlService.Iface client = getClient(remainTime, logger);
    if (client == null) {
      return false;
    }

    jobInfo.put(jobId, client);

    try {
      return function.apply(client);
    } catch (Throwable e) {
      logger.info("Sql exec error.", e);
    } finally {
      clientQueue.add(client);
      jobInfo.remove(jobId);
    }

    return false;
  }

  public static void main(String[] args) throws InterruptedException {
    SparkSqlClient.init("172.24.8.98", 20017);
    List<String> sqls = new ArrayList<>();
    sqls.add("select count(1) from ods.tbs_ods_twfb");
    SparkSqlClient.getInstance()
        .executeAdhoc("test", new ArrayList<>(), sqls,
            (execResult, startTime, endTime) -> LOGGER.error(execResult.getStm())
            , 1000, 100000, LOGGER);
  }
}
