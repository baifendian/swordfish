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

import static com.baifendian.swordfish.common.utils.ThriftUtil.getTThreadPoolServer;

import com.baifendian.swordfish.common.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.MasterDao;
import com.baifendian.swordfish.dao.model.MasterServer;
import com.baifendian.swordfish.execserver.service.ExecServiceImpl;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.baifendian.swordfish.execserver.utils.OsUtil;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.WorkerService;
import com.baifendian.swordfish.rpc.client.MasterClient;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * exec thrift server service
 */
public class ExecThriftServer {

  private static Logger logger = LoggerFactory.getLogger(ExecThriftServer.class);

  private TServer server;

  private static Configuration conf;

  /**
   * master 数据库接口
   */
  private MasterDao masterDao;

  /**
   * master 的地址信息
   */
  private final MasterServer masterServer;

  /**
   * master 的连接实例
   */
  private final MasterClient masterClient;

  /**
   * 当前 exec 的 host 信息
   */
  private String host;

  /**
   * 当前 exec 的 port 信息
   */
  private final int port;

  private InetSocketAddress inetSocketAddress;

  /**
   * 发送心跳的线程
   */
  private ScheduledExecutorService heartbeatExecutorService;

  /**
   * 运行状态
   */
  private AtomicBoolean running = new AtomicBoolean(true);

  /**
   * exec 服务的实现
   */
  private ExecServiceImpl workerService;

  /**
   * 心跳时间间隔，单位秒
   */
  private int heartBeatInterval;

  static {
    try {
      conf = new PropertiesConfiguration("worker.properties");
    } catch (ConfigurationException e) {
      logger.error("Load configuration exception", e);
      System.exit(1);
    }
  }

  public ExecThriftServer() throws TTransportException, UnknownHostException {
    masterDao = DaoFactory.getDaoInstance(MasterDao.class);
    masterServer = masterDao.getMasterServer();

    if (masterServer == null) {
      logger.error("Can't found master server");
      throw new RuntimeException("can't found master server");
    }

    masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort(),
        Constants.defaultThriftRpcRetrites);

    // executor 的地址, 端口信息
    host = InetAddress.getLocalHost().getHostAddress();
    port = conf.getInt(Constants.EXECUTOR_PORT, 10000);
  }

  /**
   * @throws UnknownHostException
   * @throws TTransportException
   */
  public void run() throws UnknownHostException, TTransportException, InterruptedException {
    HdfsClient.init(ConfigurationUtil.getConfiguration());

    logger.info("register to master {}:{}", masterServer.getHost(), masterServer.getPort());

    // 注册到 master
    boolean ret = masterClient.registerExecutor(host, port, System.currentTimeMillis());
    if (!ret) {
      // 休息一会, 再进行注册
      Thread.sleep(3000);

      ret = masterClient.registerExecutor(host, port, System.currentTimeMillis());

      if (!ret) {
        logger.error("register to master {}:{} failed", masterServer.getHost(),
            masterServer.getPort());
        throw new RuntimeException("register executor error");
      }
    }

    // 心跳
    heartBeatInterval = conf
        .getInt(Constants.EXECUTOR_HEARTBEAT_INTERVAL, Constants.defaultExecutorHeartbeatInterval);

    heartbeatExecutorService = Executors
        .newScheduledThreadPool(Constants.defaultExecutorHeartbeatThreadNum);

    Runnable heartBeatThread = getHeartBeatThread();
    heartbeatExecutorService
        .scheduleAtFixedRate(heartBeatThread, 10, heartBeatInterval, TimeUnit.SECONDS);

    // 启动 worker service
    TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
    TTransportFactory tTransportFactory = new TTransportFactory();

    workerService = new ExecServiceImpl(host, port, conf);

    TProcessor tProcessor = new WorkerService.Processor(workerService);
    inetSocketAddress = new InetSocketAddress(host, port);
    server = getTThreadPoolServer(protocolFactory, tProcessor, tTransportFactory, inetSocketAddress,
        Constants.defaultServerMinNum, Constants.defaultServerMaxNum);

    logger.info("start thrift server on port:{}", port);

    // 这里完全是为了设置为 daemon, 实际意义倒不是很大
    Thread serverThread = new TServerThread(server);
    serverThread.setDaemon(true);
    serverThread.start();

    // 注册钩子, 销毁一些信息
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      postProcess();

      logger.info("exec server stop");
    }));

    synchronized (this) {
      // 等待心跳线程退出
      while (running.get()) {
        try {
          logger.info("wait....................");
          wait();
        } catch (InterruptedException e) {
          logger.error("error", e);
        }
      }

      postProcess();

      logger.info("exec server stop");
    }
  }

  private void postProcess() {
    if (heartbeatExecutorService != null) {
      heartbeatExecutorService.shutdownNow();
      heartbeatExecutorService = null;
    }

    if (workerService != null) {
      workerService.destory();
      workerService = null;
    }

    if (server != null) {
      server.stop();
      server = null;
    }

    // 告知 master 已经关闭
    if (masterClient != null) {
      masterClient.downExecutor(host, port);
    }
  }

  /**
   * 得到发送心跳的线程
   */
  public Runnable getHeartBeatThread() {
    Runnable heartBeatThread = () -> {
      if (running.get()) {
        HeartBeatData heartBeatData = new HeartBeatData();

        heartBeatData.setReportDate(System.currentTimeMillis());
        heartBeatData.setCpuUsed(OsUtil.cpuUsage());
        heartBeatData.setMemUsed(OsUtil.memoryUsage());

        logger.info("executor report heartbeat:{}", heartBeatData);

        boolean result = masterClient.executorReport(host, port, heartBeatData);

        if (!result) {
          logger.warn("heart beat time out!");
          running.compareAndSet(true, false);

          synchronized (this) {
            notify();
          }
        }
      }
    };

    return heartBeatThread;
  }

  public class TServerThread extends Thread {

    private TServer server;

    public TServerThread(TServer server) {
      this.server = server;
    }

    @Override
    public void run() {
      server.serve();
    }
  }

  public static void main(String[] args)
      throws TTransportException, UnknownHostException, InterruptedException {
    ExecThriftServer execThriftServer = new ExecThriftServer();
    execThriftServer.run();
  }
}
