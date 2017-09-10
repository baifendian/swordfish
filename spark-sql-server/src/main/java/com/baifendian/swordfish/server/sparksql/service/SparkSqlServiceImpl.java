package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultRet;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.SparkSqlService.Iface;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.util.List;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkSqlServiceImpl implements Iface {

  private static Logger logger = LoggerFactory.getLogger(SparkSqlServiceImpl.class);

  private RunnerManager runnerManager = new RunnerManager();

  @Override
  public RetInfo execEtl(String jobId, List<UdfInfo> udfs, List<String> sql, int remainTime)
      throws TException {
    logger.info("begin ");
    RetInfo retInfo = new RetInfo();
    long endTime = System.currentTimeMillis() + remainTime * 1000L;
    try {
      runnerManager.executeEtlSql(jobId, udfs, sql, endTime);
    } catch (Throwable e) {
      logger.error("exec error.", e);
      retInfo.setStatus(1);
      retInfo.setMsg(e.getMessage());
      return retInfo;
    }
    retInfo.setStatus(0);
    return retInfo;
  }

  @Override
  public RetInfo execAdhoc(String jobId, List<UdfInfo> udfs, List<String> sql, int queryLimit,
      int remainTime) throws TException {
    RetInfo retInfo = new RetInfo();
    long endTime = System.currentTimeMillis() + remainTime * 1000L;
    try {
      runnerManager.executeAdhocSql(jobId, udfs, sql, endTime, queryLimit);
    } catch (Throwable e) {
      logger.error("exec error.", e);
      retInfo.setStatus(1);
      retInfo.setMsg(e.getMessage());
      return retInfo;
    }
    retInfo.setStatus(0);
    return retInfo;
  }

  @Override
  public RetInfo cancelExecFlow(String jobId) throws TException {
    RetInfo retInfo = new RetInfo();
    retInfo.setStatus(0);

    runnerManager.cancelExecFlow(jobId);
    return retInfo;
  }

  @Override
  public AdhocResultRet getAdhocResult(String jobId, int index) throws TException {
    return runnerManager.getAdHocResult(jobId, index);
  }
}
