package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultInfo;
import com.baifendian.swordfish.server.sparksql.common.FlowStatus;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdhocResultData {
  /**
   * 记录日志的实例
   */
  private static Logger logger = LoggerFactory.getLogger(AdhocResultData.class);

  private BlockingQueue<AdhocResultInfo> adhocResultInfoList;
  private int curIndex = 0;
  private int totalLen = 0;

  public AdhocResultData(int totalLen) {
    this.totalLen = totalLen;
    adhocResultInfoList = new ArrayBlockingQueue<>(totalLen);
  }

  synchronized void handlerResult(int fromIndex, String sql, FlowStatus status) {
    if (adhocResultInfoList == null) {
      logger.error("*************\nAdhoc result list is null.");
      return;
    }

    AdhocResultInfo adhocResultInfo = new AdhocResultInfo();
    adhocResultInfo.setIndex(fromIndex);
    adhocResultInfo.setStatus(status.ordinal());
    adhocResultInfo.setStm(sql);

    adhocResultInfoList.add(adhocResultInfo);
  }

  synchronized void addResult(AdhocResultInfo adhocResultInfo) {
    if (adhocResultInfoList == null) {
      return;
    }
    adhocResultInfoList.add(adhocResultInfo);
  }

  synchronized AdhocResultInfo getAdHocResult() {
    if (curIndex++ >= totalLen) {
      AdhocResultInfo adhocResultInfo = new AdhocResultInfo();
      adhocResultInfo.setIndex(curIndex);
      adhocResultInfo.setStatus(1);
      return adhocResultInfo;
    }

    return adhocResultInfoList.poll();
  }
}
