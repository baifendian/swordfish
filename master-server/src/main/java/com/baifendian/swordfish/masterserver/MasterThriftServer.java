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
package com.baifendian.swordfish.masterserver;

import com.baifendian.swordfish.common.utils.ThriftUtil;
import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.MasterDao;
import com.baifendian.swordfish.dao.model.MasterServer;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import com.baifendian.swordfish.masterserver.exception.MasterException;
import com.baifendian.swordfish.masterserver.master.MasterServiceImpl;
import com.baifendian.swordfish.masterserver.quartz.QuartzManager;
import com.baifendian.swordfish.rpc.MasterService;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterThriftServer {

  private static final Logger logger = LoggerFactory.getLogger(MasterThriftServer.class);

  // thrift server
  private TServer server;

  // 当前服务的 host
  private final String host;

  // 当前服务的 port
  private final int port;

  // master 的数据库接口
  private MasterDao masterDao;

  // master 的接口实现
  private MasterServiceImpl masterService;

  public MasterThriftServer() throws UnknownHostException {
    host = HttpUtil.getHostAddress();
    port = MasterConfig.masterPort;

    masterDao = DaoFactory.getDaoInstance(MasterDao.class);
  }

  /**
   * 入口方法
   *
   * @throws SchedulerException
   * @throws TTransportException
   * @throws MasterException
   */
  public void run() throws SchedulerException, TTransportException, MasterException {
    try {
      // 在数据库中注册 master
      registerMaster();
      init();

      // 注册钩子, 进程退出前会调用
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        // 清理数据库中 master 信息
        masterDao.deleteMasterServer(host, port);

        // 关闭 server
        server.stop();

        if (masterService != null) {
          masterService.stop();
        }

        // 关闭调度
        try {
          QuartzManager.shutdown();
        } catch (SchedulerException e) {
          logger.error("Catch an exception when shutdown quartz manager", e);
        }
      }));

      masterService.run();

      // 启动调度
      QuartzManager.start();

      server.serve();
    } catch (Exception e) {
      QuartzManager.shutdown();
      logger.error("Catch an exception", e);
    }
  }

  /**
   * 初始化连接
   *
   * @throws MasterException
   * @throws TTransportException
   */
  private void init() throws MasterException, TTransportException {
    masterService = new MasterServiceImpl();

    TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
    TTransportFactory tTransportFactory = new TTransportFactory();
    TProcessor tProcessor = new MasterService.Processor(masterService);
    InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);

    server = ThriftUtil.getTThreadPoolServer(protocolFactory, tProcessor, tTransportFactory, inetSocketAddress,
        MasterConfig.masterMinThreads, MasterConfig.masterMaxThreads);

    logger.info("start thrift server on port: {}", port);
  }

  /**
   * 注册 jobExecManager 到 database 中
   *
   * @throws MasterException
   */
  public void registerMaster() throws MasterException {
    MasterServer masterServer = masterDao.getMasterServer();

    if (masterServer != null && !(masterServer.getHost().equals(host) && masterServer.getPort() == port)) {
      String msg = String.format("can't register more then one jobExecManager, exist jobExecManager:%s:%d, " +
              "if you change the jobExecManager deploy server please clean the table master_server and start up again",
          masterServer.getHost(), masterServer.getPort());
      throw new MasterException(msg);
    } else {
      if (masterServer == null) {
        masterDao.registerMasterServer(host, port);
      } else {
        masterDao.updateMasterServer(host, port);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    MasterThriftServer masterThriftServer = new MasterThriftServer();
    masterThriftServer.run();
  }
}
