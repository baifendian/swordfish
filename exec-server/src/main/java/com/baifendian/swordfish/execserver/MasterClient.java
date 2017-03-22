/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.MasterService;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.ScheduleInfo;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Master thrift client
 */
public class MasterClient {

  private static Logger logger = LoggerFactory.getLogger(MasterClient.class);

  private String host;

  private int port;

  private int timeout = 3000;

  private TTransport tTransport;

  private MasterService.Client client;

  private int retries;

  public MasterClient(String host, int port, int retries) {
    this.host = host;
    this.port = port;
    this.retries = retries;
  }

  private void connect() {
    tTransport = new TSocket(host, port, timeout);
    try {
      TProtocol protocol = new TBinaryProtocol(tTransport);
      client = new MasterService.Client(protocol);
      tTransport.open();
    } catch (TTransportException e) {
      e.printStackTrace();
    }
  }

  private void close() {
    if (tTransport != null) {
      tTransport.close();
    }
  }

  public boolean executorReport(String clientHost, int clientPort, HeartBeatData heartBeatData) {
    boolean result = false;
    for(int i=0; i<retries; i++){
      result = executorReportOne(clientHost, clientPort, heartBeatData);
      if(result)
        break;
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public boolean executorReportOne(String clientHost, int clientPort, HeartBeatData heartBeatData) {
    connect();
    try {
      RetInfo retInfo = client.executorReport(clientHost, clientPort, heartBeatData);
      if (retInfo.getStatus() != 0) {
        logger.error("executor report return {}", retInfo.getMsg());
        return false;
      }
    } catch (TException e) {
      logger.error("report info error", e);
      return false;
    } finally {
      close();
    }
    return true;
  }

  public boolean registerExecutor(String clientHost, int clientPort, long registerTime) {
    connect();
    try {
      RetInfo ret = client.registerExecutor(clientHost, clientPort, registerTime);
      if (ret.getStatus() != 0) {
        logger.error("register executor error:{}", ret.getMsg());
        return false;
      }
    } catch (TException e) {
      logger.error("register executor error", e);
      return false;
    } finally {
      close();
    }
    return true;
  }

  public boolean setSchedule(int projectId, int flowId, String flowType, ScheduleInfo scheduleInfo) {
    connect();
    try {
      RetInfo ret = client.setSchedule(projectId, flowId, flowType, scheduleInfo);
      if (ret.getStatus() != 0) {
        logger.error("set schedule error:{}", ret.getMsg());
        return false;
      }
    } catch (TException e) {
      logger.error("set schedule error", e);
      return false;
    } finally {
      close();
    }
    return true;
  }

}
