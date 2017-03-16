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

import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.execserver.utils.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.execserver.flow.FlowRunnerManager;
import com.baifendian.swordfish.execserver.servlet.ExecutorServlet;
import com.baifendian.swordfish.execserver.utils.OsUtil;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker 服务 <p>
 *
 * @author : dsfan
 * @date : 2016年10月25日
 */
public class ExecServer {
  /**
   * LOGGER
   */
  private static final Logger logger = LoggerFactory.getLogger(ExecServer.class);

  /**
   * 默认配置文件
   */
  private static final String DEFAULT_CONFIG = "classpath:worker.properties";

  /**
   * yarn 作业配置文件
   */
  private static final String JOB_YARN_CONFIG = "classpath:job/yarn.properties";

  /**
   * hive 作业配置文件
   */
  private static final String JOB_HIVE_CONFIG = "classpath:job/hive.properties";

  /**
   * java 作业配置文件
   */
  private static final String JOB_JAVA_CONFIG = "classpath:job/java.properties";

  /**
   * server配置文件
   */
  private static final String SERVER_FILE_PATH = "classpath:worker-server.properties";

  /**
   * 是否保持启动
   */
  private static boolean running = true;

  private static final int MAX_FORM_CONTENT_SIZE = 10 * 1024 * 1024;

  private static final int DEFAULT_THREAD_NUMBER = 50;

  public static final int DEFAULT_HEADER_BUFFER_SIZE = 4096;

  public static final String SERVLET_CONTEXT_KEY = "sf_app";
  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * {@link FlowRunnerManager}
   */
  private final FlowRunnerManager flowRunnerManager;

  /**
   * constructor
   */
  public ExecServer() throws Exception {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.flowRunnerManager = new FlowRunnerManager();

    Configuration conf = new PropertiesConfiguration("worker.properties");
    Server server = createJettyServer(conf);
    server.start();
  }

  /**
   * @param props
   */
  private Server createJettyServer(Configuration props) {
    int maxThreads = props.getInt("executor.maxThreads", DEFAULT_THREAD_NUMBER);

    /*
     * Default to a port number 0 (zero)
     * The Jetty server automatically finds an unused port when the port number is set to zero
     * TODO: This is using a highly outdated version of jetty [year 2010]. needs to be updated.
     */
    Server server = new Server(props.getInt("executor.port", 0));
    QueuedThreadPool httpThreadPool = new QueuedThreadPool(maxThreads);
    server.setThreadPool(httpThreadPool);


    boolean isStatsOn = props.getBoolean("executor.connector.stats", true);
    logger.info("Setting up connector with stats on: " + isStatsOn);

    for (Connector connector : server.getConnectors()) {
      logger.info("start server on port:" + connector.getPort());
      connector.setStatsOn(isStatsOn);
      logger.info(String.format(
              "Jetty connector name: %s, default header buffer size: %d",
              connector.getName(), connector.getHeaderBufferSize()));
      connector.setHeaderBufferSize(props.getInt("jetty.headerBufferSize",
              DEFAULT_HEADER_BUFFER_SIZE));
      logger.info(String.format(
              "Jetty connector name: %s, (if) new header buffer size: %d",
              connector.getName(), connector.getHeaderBufferSize()));
    }

    Context root = new Context(server, "/", Context.SESSIONS);
    root.setMaxFormContentSize(MAX_FORM_CONTENT_SIZE);

    root.addServlet(new ServletHolder(new ExecutorServlet()), "/executor");

    root.setAttribute(SERVLET_CONTEXT_KEY, this);
    return server;
  }

  public static void main(String[] args) {
    System.out.println("start exec server");
    try {
      // 下面语句用于 windows 调试
      if (OsUtil.isWindows()) {
        System.setProperty("hadoop.home.dir", "d:\\hadoop\\");
      }

      // 初始化 hdfs client
      HdfsClient.init(ConfigurationUtil.getConfiguration());

      ExecServer execServer = new ExecServer();
      //ExecServiceImpl impl = new ExecServiceImpl();

      //impl.scheduleExecFlow(1, 4, "etl", System.currentTimeMillis());
/*
            final Server server = new Server(new String[] { SERVER_FILE_PATH }, impl);
            server.start();

            // 添加ShutdownHook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    server.close(); // 关闭服务
                    impl.destory(); // 销毁资源
                }
            }));


            synchronized (ExecServer.class) {
                while (running) {
                    try {
                        ExecServer.class.wait();
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
*/
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

  }

  public FlowRunnerManager getFlowRunnerManager() {
    return flowRunnerManager;
  }
}
