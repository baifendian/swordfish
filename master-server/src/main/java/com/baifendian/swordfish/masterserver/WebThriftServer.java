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
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.MasterDao;
import com.baifendian.swordfish.dao.model.MasterServer;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import com.baifendian.swordfish.masterserver.exception.MasterException;
import com.baifendian.swordfish.masterserver.master.Master;
import com.baifendian.swordfish.masterserver.master.MasterServiceImpl;
import com.baifendian.swordfish.masterserver.quartz.QuartzManager;
import com.baifendian.swordfish.rpc.MasterService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class WebThriftServer {

  private static final Logger logger = LoggerFactory.getLogger(WebThriftServer.class);

  private TServer server;

  // master host
  private final String host;

  // master port
  private final int port;

  // master 的 dao
  private MasterDao masterDao;

  // master service 的具体实现
  private MasterServiceImpl masterService;

  private Master master;

  public WebThriftServer() throws UnknownHostException {
    host = InetAddress.getLocalHost().getHostAddress();
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
      registerMaster();
      init();

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        // 清理数据库
        masterDao.deleteMasterServer(host, port);

        // 关闭 server
        server.stop();

        if (master != null) {
          master.stop();
        }

        try {
          // 关闭调度
          QuartzManager.shutdown();
        } catch (SchedulerException e) {
          logger.error(e.getMessage(), e);
        }
      }));

      master.run();

      // 启动调度
      QuartzManager.start();

      server.serve();
    } catch (Exception e) {
      QuartzManager.shutdown();
      logger.error("Catch an exception", e);
      throw e;
    }
  }

  /**
   * 初始化连接
   *
   * @throws MasterException
   * @throws TTransportException
   */
  private void init() throws MasterException, TTransportException {
    master = new Master();
    masterService = new MasterServiceImpl(master);

    TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
    TTransportFactory tTransportFactory = new TTransportFactory();
    TProcessor tProcessor = new MasterService.Processor(masterService);
    InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);

    server = ThriftUtil.getTThreadPoolServer(protocolFactory, tProcessor, tTransportFactory, inetSocketAddress,
        MasterConfig.masterMinThreads, MasterConfig.masterMaxThreads);

    logger.info("start thrift server on port: {}", port);
  }

  /**
   * 注册 master 到 database 中
   *
   * @throws MasterException
   */
  public void registerMaster() throws MasterException {
    MasterServer masterServer = masterDao.getMasterServer();

    if (masterServer != null && !(masterServer.getHost().equals(host) && masterServer.getPort() == port)) {
      String msg = String.format("can't register more then one master, exist master:%s:%d, " +
              "if you change the master deploy server please clean the table master_server and start up again",
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
    WebThriftServer webThriftServer = new WebThriftServer();
    webThriftServer.run();
  }
}
