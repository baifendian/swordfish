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
package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.rpc.WorkerService.Iface;
import com.baifendian.swordfish.webserver.quartz.QuartzManager;
import com.baifendian.swordfish.webserver.service.master.MasterServiceImpl;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Master 服务 <p>
 *
 * @author : dsfan
 * @date : 2016年10月25日
 */
public class WebServer {
  /**
   * LOGGER
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

  /**
   * 默认配置文件
   */
  private static final String DEFAULT_CONFIG = "classpath:master.properties";

  /**
   * worker client 配置文件
   */
  private static final String CLIENT_FILE_PATH = "classpath:worker-client.properties";

  /**
   * master server 配置文件
   */
  private static final String SERVER_FILE_PATH = "classpath:master-server.properties";

  /**
   * 是否保持启动
   */
  private static boolean running = true;

  /**
   * @param args
   */
  public static void main(String[] args) {
        /*
        try {
            // 加载配置文件
            PropertiesConfiguration.load(new String[] { DEFAULT_CONFIG });

            // 启动调度
            QuartzManager.start();

            // 创建 worker client proxy
            final Client<Iface> client = new Client<>(new String[] { CLIENT_FILE_PATH });
            Iface worker = client.createProxy();

            // 启动 master 服务
            MasterServiceImpl impl = new MasterServiceImpl();
            final Server server = new Server(new String[] { SERVER_FILE_PATH }, impl, client.getRegistryConfig());
            server.start();

            // 添加ShutdownHook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    server.close(); // 关闭 server
                    client.close(); // 关闭 client
                    try {
                        // 关闭调度
                        QuartzManager.shutdown();
                        // 关闭资源
                        impl.destory();
                    } catch (SchedulerException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }));

            synchronized (WebServer.class) {
                while (running) {
                    try {
                        WebServer.class.wait();
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
*/
  }
}
