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
package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.execserver.job.hive.EtlSqlJob;
import com.baifendian.swordfish.execserver.job.mr.MrJob;
import com.baifendian.swordfish.execserver.job.shell.ShellJob;
import com.baifendian.swordfish.execserver.job.spark.SparkJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

//import com.baifendian.swordfish.execserver.job.mr.MrJob;

/**
 * job生成工厂类
 */
public class JobTypeManager {

  private static final Logger logger = LoggerFactory.getLogger(JobTypeManager.class);

  private static final String MR = "MR";
  private static final String SHELL = "SHELL";
  private static final String SPARK = "SPARK";
  private static final String VIRTUAL = "VIRTUAL";
  private static final String HQL = "HQL";
  private static final String SPARK_STREAMING = "SPARK-STREAMING";

  private static Map<String, JobType> jobTypeMap = new HashMap<>();

  static {
    initBaseJobType();
  }

  private static void initBaseJobType() {
    jobTypeMap.put(MR, new JobType(MrJob.class, false));
    jobTypeMap.put(SHELL, new JobType(ShellJob.class, false));
    jobTypeMap.put(SPARK, new JobType(SparkJob.class, false));
    jobTypeMap.put(VIRTUAL, new JobType(NoopJob.class, false));
    jobTypeMap.put(HQL, new JobType(EtlSqlJob.class, false));
    jobTypeMap.put(SPARK_STREAMING, new JobType(SparkJob.class, true));

    addPluginJobs();

    printPluginJobs();
  }

  /**
   * 根据配置文件解析plugin job
   */
  private static void addPluginJobs() {
    try {
      File pluginFile = ResourceUtils.getFile("classpath:plugin_jobs.xml");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(pluginFile);
      NodeList pluginJobs = doc.getElementsByTagName("pluginJob");
      for (int i = 0; i < pluginJobs.getLength(); i++) {
        String name = doc.getElementsByTagName("name").item(i).getFirstChild().getNodeValue();
        String className = doc.getElementsByTagName("className").item(i).getFirstChild().getNodeValue();
        String isLongStr = doc.getElementsByTagName("isLongJob").item(i).getFirstChild().getNodeValue();
        boolean isLong = false;
        if (isLongStr.equalsIgnoreCase("true")) {
          isLong = true;
        }
        Class<? extends Job> jobClassName = (Class<? extends Job>) Class.forName(className);
        if (jobTypeMap.containsKey(name)) {
          logger.error("job type name {} is exists for {} ignore", name, jobTypeMap.get(name));
        } else {
          jobTypeMap.put(name, new JobType(jobClassName, isLong));
        }
      }
    } catch (FileNotFoundException e) {
      logger.error("Catch an exception", e);
    } catch (ParserConfigurationException e) {
      logger.error("Catch an exception", e);
    } catch (SAXException e) {
      logger.error("Catch an exception", e);
    } catch (IOException e) {
      logger.error("Catch an exception", e);
    } catch (ClassNotFoundException e) {
      logger.error("Catch an exception", e);
    }
  }

  public static Job newJob(String jobIdLog, String jobTypeStr, JobProps props, Logger logger) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
    JobType jobType = jobTypeMap.get(jobTypeStr.toUpperCase());
    Job job;

    if (jobType == null) {
      logger.error("unsupport job type: {}", jobTypeStr);
      throw new ExecException("unsupport job type:" + jobTypeStr);
    } else {
      Constructor<Job> constructor = jobType.getClassName().getConstructor(String.class, JobProps.class, Logger.class);
      job = constructor.newInstance(jobIdLog, props, logger);
    }

    return job;
  }

  public static boolean isLongJob(String jobTypeStr) {
    if (!jobTypeMap.containsKey(jobTypeStr.toUpperCase())) {
      logger.error("unsupport job type: {}", jobTypeStr);
      throw new IllegalArgumentException("not found job type " + jobTypeStr);
    }

    JobType jobType = jobTypeMap.get(jobTypeStr.toUpperCase());
    return jobType.isLong();
  }

  private static void printPluginJobs() {
    logger.info("active plugin jobs:");
    for (Map.Entry entry : jobTypeMap.entrySet()) {
      logger.info("job type {} {}", entry.getKey(), entry.getValue());
    }
  }
}
