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
package com.baifendian.swordfish.execserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 操作系统工具类 <p>
 *
 */
public class OsUtil {

  private static final Logger logger = LoggerFactory.getLogger(OsUtil.class);

  /**
   * private constructor
   */
  private OsUtil() {
  }

  /**
   * 是否 windows <p>
   */
  public static boolean isWindows() {
    String os = System.getProperty("os.name");
    return os.startsWith("Windows");
  }

  /**
   * 获取内存使用率
   * @return
   */
  public static double memoryUsage(){
    Map<String, Object> map = new HashMap<>();
    InputStreamReader inputs = null;
    BufferedReader buffer = null;
    try{
      inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
      buffer = new BufferedReader(inputs);
      String line = "";
      while(true){
        line = buffer.readLine();
        if(line == null)
          break;

        if(line.contains(":")){
          String[] memInfo = line.split(":");
          String value = memInfo[1].replace("kB", "").trim();
          map.put(memInfo[0], value);
        }
      }

      long memTotal = Long.parseLong(map.get("MemTotal").toString());
      long memFree = Long.parseLong(map.get("MemFree").toString());
      long buffers = Long.parseLong(map.get("Buffers").toString());
      long cached = Long.parseLong(map.get("Cached").toString());

      double usage = (float)(memTotal - memFree - buffers - cached) / memTotal;
      return usage;
    } catch (Exception e){
      logger.error("get memory usage error", e);
    } finally {
      try {
        buffer.close();
        inputs.close();
      } catch (IOException e) {
        logger.error("close stream", e);
      }
    }
    return 0;
  }

  /**
   * 获取cpu使用率
   * @return
   */
  public static double cpuUsage() {
    try {
      Map<?, ?> map1 = OsUtil.cpuinfo();
      Thread.sleep(5 * 1000);
      Map<?, ?> map2 = OsUtil.cpuinfo();

      long user1 = Long.parseLong(map1.get("user").toString());
      long nice1 = Long.parseLong(map1.get("nice").toString());
      long system1 = Long.parseLong(map1.get("system").toString());
      long idle1 = Long.parseLong(map1.get("idle").toString());

      long user2 = Long.parseLong(map2.get("user").toString());
      long nice2 = Long.parseLong(map2.get("nice").toString());
      long system2 = Long.parseLong(map2.get("system").toString());
      long idle2 = Long.parseLong(map2.get("idle").toString());

      long total1 = user1 + system1 + nice1;
      long total2 = user2 + system2 + nice2;
      float total = total2 - total1;

      long totalIdle1 = user1 + nice1 + system1 + idle1;
      long totalIdle2 = user2 + nice2 + system2 + idle2;
      float totalidle = totalIdle2 - totalIdle1;

      double cpusage = (total / totalidle);
      return cpusage;
    } catch (InterruptedException e) {
      logger.error("get cpu usage error", e);
    }
    return 0;
  }

  public static Map<?, ?> cpuinfo() {
    InputStreamReader inputs = null;
    BufferedReader buffer = null;
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
      buffer = new BufferedReader(inputs);
      String line = "";
      while (true) {
        line = buffer.readLine();
        if (line == null) {
          break;
        }
        if (line.startsWith("cpu")) {
          StringTokenizer tokenizer = new StringTokenizer(line);
          List<String> temp = new ArrayList<String>();
          while (tokenizer.hasMoreElements()) {
            String value = tokenizer.nextToken();
            temp.add(value);
          }
          map.put("user", temp.get(1));
          map.put("nice", temp.get(2));
          map.put("system", temp.get(3));
          map.put("idle", temp.get(4));
          map.put("iowait", temp.get(5));
          map.put("irq", temp.get(6));
          map.put("softirq", temp.get(7));
          map.put("stealstolen", temp.get(8));
          break;
        }
      }
    } catch (Exception e) {
      logger.error("get cpu usage error", e);
    } finally {
      try {
        buffer.close();
        inputs.close();
      } catch (Exception e) {
        logger.error("get cpu usage error", e);
      }
    }
    return map;
  }


}
