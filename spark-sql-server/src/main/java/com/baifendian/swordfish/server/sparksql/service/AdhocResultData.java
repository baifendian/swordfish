package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultInfo;
import com.baifendian.swordfish.server.sparksql.common.FlowStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdhocResultData {

  /**
   * 记录日志的实例
   */
  private static Logger logger = LoggerFactory.getLogger(AdhocResultData.class);

  private List<AdhocResultInfo> adhocResultInfoList;
  private Lock lock = new ReentrantLock();

  private Condition resultCondition = lock.newCondition();
  private final int totalLen;
  private boolean isKill = false;

  public AdhocResultData(int totalLen) {
    this.totalLen = totalLen;
    adhocResultInfoList = new ArrayList<>(totalLen);
  }

  synchronized void handlerResult(int fromIndex, String sql, FlowStatus status) {
    AdhocResultInfo adhocResultInfo = new AdhocResultInfo();
    adhocResultInfo.setIndex(fromIndex);
    adhocResultInfo.setStatus(status.ordinal());
    adhocResultInfo.setStm(sql);

    addResult(adhocResultInfo);
  }

  synchronized void addResult(AdhocResultInfo adhocResultInfo) {
    if (adhocResultInfoList == null) {
      logger.error("*************\nAdhoc result list is null.");
      return;
    }
    adhocResultInfoList.add(adhocResultInfo);

    lock.lock();
    resultCondition.signal();
    lock.unlock();
  }

  void cancel() {
    isKill = true;

    lock.lock();
    resultCondition.signal();
    lock.unlock();
  }

  AdhocResultInfo getAdHocResult(int index) {
    if (index >= totalLen) {
      throw new RuntimeException("index is too big.");
    }

    while (index >= adhocResultInfoList.size()) {

      if (isKill) {
        return createKillResult();
      }

      lock.lock();
      try {
        resultCondition.await();
      } catch (InterruptedException e) {
        logger.error("Result error.", e);
      } finally {
        lock.unlock();
      }
    }

    return adhocResultInfoList.get(index);
  }


  AdhocResultInfo createKillResult() {
    AdhocResultInfo adhocResultInfo = new AdhocResultInfo();
    adhocResultInfo.setStatus(FlowStatus.KILL.ordinal());
    return adhocResultInfo;
  }
}
