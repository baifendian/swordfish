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
package com.baifendian.swordfish.rpc.client;

import com.baifendian.swordfish.rpc.*;
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

  private static Logger logger = LoggerFactory.getLogger(MasterClient.class.getName());

  /**
   * 默认的 client 与 server 重连次数
   */
  private static final int DEFAULT_RETRY_TIMES = 3;

  /**
   * master 地址
   */
  private String host;

  /**
   * master 端口
   */
  private int port;

  /**
   * 超时时间, 客户端连接到 master 的超时时间
   */
  private int timeout = 10000;

  /**
   * exec 向 master 发送心跳的重试次数
   */
  private int retries;

  /**
   * 传输层对象
   */
  private TTransport tTransport;

  /**
   * master client
   */
  private MasterService.Client client;

  public MasterClient(String host, int port, int retries) {
    this.host = host;
    this.port = port;
    this.retries = retries;
  }

  public MasterClient(String host, int port) {
    this(host, port, DEFAULT_RETRY_TIMES);
  }


  /**
   * 连接 master
   *
   * @return 成功返回 true, 否则返回 false
   */
  private boolean connect() {
    tTransport = new TSocket(host, port, timeout);

    try {
      TProtocol protocol = new TBinaryProtocol(tTransport);

      client = new MasterService.Client(protocol);
      tTransport.open();
    } catch (TTransportException e) {
      logger.error("Connection server exception", e);
      return false;
    }

    return true;
  }

  /**
   * 关闭连接
   */
  private void close() {
    if (tTransport != null) {
      tTransport.close();
    }
  }

  /**
   * 报告状态, 用于 exec-server
   *
   * @param clientHost
   * @param clientPort
   * @param heartBeatData
   * @return
   */
  public boolean executorReport(String clientHost, int clientPort, HeartBeatData heartBeatData) {
    boolean result = false;

    for (int i = 0; i < retries; i++) {
      result = executorReportOne(clientHost, clientPort, heartBeatData);
      if (result) {
        break;
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        logger.error("report info error", e);
        return false;
      }
    }

    return result;
  }

  /**
   * 报告状态, 用于 exec-server
   *
   * @param clientHost
   * @param clientPort
   * @param heartBeatData
   * @return
   */
  public boolean executorReportOne(String clientHost, int clientPort, HeartBeatData heartBeatData) {
    if (!connect()) {
      close();
      return false;
    }

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

  /**
   * 注册一个 executor, 用于 exec-server
   *
   * @param clientHost
   * @param clientPort
   * @param registerTime
   * @return
   */
  public boolean registerExecutor(String clientHost, int clientPort, long registerTime) {
    if (!connect()) {
      close();
      return false;
    }

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

  /**
   * 上线一个工作流的调度, 用于 web-server
   *
   * @param projectId
   * @param flowId
   * @return
   */
  public boolean setSchedule(int projectId, int flowId) throws TTransportException {
    if (!connect()) {
      close();
      return false;
    }

    try {
      RetInfo ret = client.setSchedule(projectId, flowId);
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

  /**
   * 取消一个调度的设置, 用于 web-server
   *
   * @param projectId
   * @param flowId
   * @return
   */
  public boolean deleteSchedule(int projectId, int flowId) {
    if (!connect()) {
      close();
      return false;
    }

    try {
      RetInfo ret = client.deleteSchedule(projectId, flowId);
      if (ret.getStatus() != 0) {
        logger.error("delete schedule error:{}", ret.getMsg());
        return false;
      }
    } catch (TException e) {
      logger.error("delete schedule error", e);
      return false;
    } finally {
      close();
    }
    return true;
  }

  /**
   * 执行即席查询, 用于 web-server
   *
   * @param id
   * @return
   */
  public RetInfo execAdHoc(int id) {
    if (!connect()) {
      close();
      return null;
    }

    try {
      RetInfo ret = client.execAdHoc(id);
      return ret;
    } catch (TException e) {
      logger.error("exec ad hoc error", e);
      return null;
    } finally {
      close();
    }
  }

  /**
   * 执行一个工作流, 用于 web-server
   *
   * @param projectId
   * @param flowId
   * @param runTime   工作流的运行时间
   * @param execInfo  执行的一些 context 信息
   * @return
   */
  public RetResultInfo execFlow(int projectId, int flowId, long runTime, ExecInfo execInfo) {
    if (!connect()) {
      close();
      return null;
    }

    try {
      RetResultInfo ret = client.execFlow(projectId, flowId, runTime, execInfo);

      return ret;
    } catch (TException e) {
      logger.error("exec flow error", e);
      return null;
    } finally {
      close();
    }
  }

  /**
   * 取消工作流执行, 用于 web-server
   *
   * @param id
   * @return
   */
  public boolean cancelExecFlow(int id) {
    if (!connect()) {
      close();
      return false;
    }

    try {
      RetInfo ret = client.cancelExecFlow(id);

      if (ret.getStatus() != 0) {
        logger.error("cancel exec flow error:{}", ret.getMsg());
        return false;
      }
    } catch (TException e) {
      logger.error("cancel flow error", e);
      return false;
    } finally {
      close();
    }

    return true;
  }

  /**
   * 执行一个流任务, 用于 web-server
   *
   * @param execId
   * @return
   */
  public RetInfo execStreamingJob(int execId) {
    if (!connect()) {
      close();
      return null;
    }

    try {
      RetInfo ret = client.execStreamingJob(execId);

      return ret;
    } catch (TException e) {
      logger.error("exec streaming job error", e);
      return null;
    } finally {
      close();
    }
  }

  /**
   * 取消流任务执行, 用于 web-server
   *
   * @param execId
   * @return
   */
  public boolean cancelStreamingJob(int execId) {
    if (!connect()) {
      close();
      return false;
    }

    try {
      RetInfo ret = client.cancelExecFlow(execId);

      if (ret.getStatus() != 0) {
        logger.error("cancel streaming job error:{}", ret.getMsg());
        return false;
      }
    } catch (TException e) {
      logger.error("cancel streaming job error", e);
      return false;
    } finally {
      close();
    }

    return true;
  }

  /**
   * 补数据接口, 用于 web-server
   *
   * @param projectId
   * @param workflowId
   * @param scheduleInfo
   * @return
   */
  public RetResultInfo appendWorkFlow(int projectId, int workflowId, ScheduleInfo scheduleInfo) {
    if (!connect()) {
      close();
      return null;
    }

    try {
      RetResultInfo ret = client.appendWorkFlow(projectId, workflowId, scheduleInfo);

      return ret;
    } catch (TException e) {
      logger.error("exec flow error", e);
      return null;
    } finally {
      close();
    }
  }
}
