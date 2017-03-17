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
package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.exception.ExecException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : liujin
 * @date : 2017-03-06 11:33
 */
public class FunctionUtil {

  private static final Logger logger = LoggerFactory.getLogger(FunctionUtil.class);
  /**
   * create function format
   */
  private static final String CREATE_FUNCTION_FORMAT = "create temporary function {0} as ''{1}''";

  private static final String JOB_HIVE_UDFJAR_BASEPATH = "job.hive.udfjar.hdfs.basepath";

  private static Configuration conf;

  private static String hiveUdfJarBasePath;

  static {
    try {
      conf = new PropertiesConfiguration("job/hive.properties");
      hiveUdfJarBasePath = conf.getString(JOB_HIVE_UDFJAR_BASEPATH);
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  public static List<String> createFuncs(List<UdfsInfo> udfsInfos, String jobIdLog, String workingDir) throws IOException, InterruptedException {
    List<String> funcList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(udfsInfos)) {
      Set<String> resources = getFuncResouces(udfsInfos);
      if (CollectionUtils.isNotEmpty(resources)) {

        if (StringUtils.isEmpty(hiveUdfJarBasePath)) {
          throw new ExecException(JOB_HIVE_UDFJAR_BASEPATH + " not defined ");
        }
        String uploadPath = hiveUdfJarBasePath + "/" + jobIdLog;
        uploadUdfJars(resources, uploadPath, workingDir);
        addJarSql(funcList, resources, uploadPath);
      }
    }
    addTempFuncSql(funcList, udfsInfos);
    return funcList;
  }

  /**
   * 获取所有函数的资源 <p>
   *
   * @return 资源  Set
   */
  private static Set<String> getFuncResouces(List<UdfsInfo> udfsInfos) {
    Set<String> resources = new HashSet<>();
    for (UdfsInfo udfsInfo : udfsInfos) {
      if (udfsInfo.getLibJar() != null) {
        resources.add(udfsInfo.getLibJar().getRes());
      }
    }
    return resources;
  }

  private static void uploadUdfJars(Set<String> resources, String path, String workingDir) throws IOException, InterruptedException {
    HdfsClient hdfsClient = HdfsClient.getInstance();
    if (!hdfsClient.exists(path)) {
      hdfsClient.mkdir(path);
    }
    for (String res : resources) {
      String cmd = String.format("hdfs dfs -put %s/%s %s", workingDir, res, path);
      logger.debug("cmd:%s", cmd);
      Process process = Runtime.getRuntime().exec(cmd);
      int ret = process.waitFor();
      if (ret != 0) {
        logger.error("run cmd:" + cmd + " error");
        String msg = IOUtils.toString(process.getErrorStream());
        logger.error(msg);
        throw new ExecException(msg);
      }
    }
  }


  /**
   * 添加 jar <p>
   */
  private static void addJarSql(List<String> sqls, Set<String> resources, String uploadPath) {
    if (!uploadPath.startsWith("hdfs:")) {
      uploadPath = "hdfs://" + uploadPath;
    }
    for (String resource : resources) {
      //sqls.add("add jar " + ResourceHelper.getResourceHdfsUrl(resourceId, isPub));
      sqls.add(String.format("add jar %s/%s", uploadPath, resource));
    }
  }


  /**
   * 添加临时函数
   */
  private static void addTempFuncSql(List<String> sqls, List<UdfsInfo> udfsInfos) {
    for (UdfsInfo udfsInfo : udfsInfos) {
      sqls.add(MessageFormat.format(CREATE_FUNCTION_FORMAT, udfsInfo.getFunc(), udfsInfo.getClassName()));
    }
  }
}
