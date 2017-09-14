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
package com.baifendian.swordfish.execserver.common;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.enums.ExternalJobType;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import com.baifendian.swordfish.common.job.struct.resource.ResourceInfo;
import com.baifendian.swordfish.dao.enums.SqlEngineType;
import com.baifendian.swordfish.execserver.exception.ExecException;
import com.google.common.base.Joiner;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class FunctionUtil {

  private static final String CREATE_FUNCTION_FORMAT = "create temporary function {0} as ''{1}''";
  private static final String CREATE_PHOENIX_FUNCTION_FORMAT =
      "create temporary function {0} ({1}) RETURNS {2} as ''{3}'' USING JAR ''hdfs://{4}''";
  private static Joiner joiner = Joiner.on(",");

  /**
   * 创建自定义函数, 即 udf
   *
   * @param srcDir 资源目录
   * @param isHdfsFile 源是否是 hdfs 文件系统
   */
  public static List<String> createFuncs(List<UdfsInfo> udfsInfos, int execId, String nodeName,
      Logger logger,
      String srcDir, boolean isHdfsFile,
      SqlEngineType type, ExternalJobType externalJobType)
      throws IOException, InterruptedException {
    // 得到 hive udf jar 包路径
    String hiveUdfJarPath = BaseConfig.getJobHiveUdfJarPath(execId, externalJobType, nodeName);

    // 是否定义了 udf 的基本目录
    if (StringUtils.isEmpty(hiveUdfJarPath)) {
      logger.error("Not define hive udf jar path");
      throw new RuntimeException("Hive udf jar base path not defined ");
    }

    List<String> funcList = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(udfsInfos)) {
      Set<String> resources = getFuncResouces(udfsInfos);

      if (CollectionUtils.isNotEmpty(resources)) {
        // adHoc 查询时直接通过复制 hdfs 上文件的方式来处理
        if (isHdfsFile) {
          copyJars(resources, hiveUdfJarPath, srcDir, logger);
        } else {
          uploadUdfJars(resources, hiveUdfJarPath, srcDir, logger);
        }

        if (SqlEngineType.PHOENIX != type) {
          // Phoenix sql can not add jar
          addJarSql(funcList, resources, hiveUdfJarPath);
        }
      }
    }

    if (SqlEngineType.PHOENIX == type) {
      addPhoenixTempFuncSql(funcList, udfsInfos, hiveUdfJarPath);
    } else {
      addTempFuncSql(funcList, udfsInfos);
    }

    return funcList;
  }

  /**
   * 获取所有函数的资源
   */
  private static Set<String> getFuncResouces(List<UdfsInfo> udfsInfos) {
    Set<String> resources = new HashSet<>();

    for (UdfsInfo udfsInfo : udfsInfos) {
      if (CollectionUtils.isNotEmpty(udfsInfo.getLibJars())) {
        for (ResourceInfo resourceInfo : udfsInfo.getLibJars()) {
          resources.add(resourceInfo.getRes());
        }
      }
    }

    return resources;
  }

  /**
   * 上传 udf jar
   */
  private static void uploadUdfJars(Set<String> resources, String tarDir, String srcDir,
      Logger logger) throws IOException, InterruptedException {
    HdfsClient hdfsClient = HdfsClient.getInstance();

    if (!hdfsClient.exists(tarDir)) {
      hdfsClient.mkdir(tarDir);
    }

    for (String res : resources) {
      // 如果目标不存在, 则进行拷贝
      if (!hdfsClient.exists(String.format("%s/%s", tarDir, res))) {
        String cmd = String.format("hdfs dfs -put %s/%s %s", srcDir, res, tarDir);

        logger.debug("cmd:{}", cmd);

        Process process = Runtime.getRuntime().exec(cmd);
        int ret = process.waitFor();
        if (ret != 0) {
          logger.error("run cmd:" + cmd + " error");

          String msg = IOUtils.toString(process.getErrorStream(), Charset.forName("UTF-8"));

          logger.error(msg);

          throw new ExecException(msg);
        }
      }
    }
  }

  /**
   * 复制资源文件到目标目录
   */
  private static void copyJars(Set<String> resources, String tarDir, String srcDir, Logger logger)
      throws IOException, InterruptedException {
    HdfsClient hdfsClient = HdfsClient.getInstance();

    if (!hdfsClient.exists(tarDir)) {
      hdfsClient.mkdir(tarDir);
    }

    for (String res : resources) {
      // 如果目标不存在才进行拷贝
      if (!hdfsClient.exists(String.format("%s/%s", tarDir, res))) {
        logger.debug("create symlink {}/{} -> {}/{}", tarDir, res, srcDir, res);
        hdfsClient
            .copy(String.format("%s/%s", srcDir, res), String.format("%s/%s", tarDir, res), false,
                true);
      }
    }
  }

  /**
   * 添加 jar
   */
  private static void addJarSql(List<String> sqls, Set<String> resources, String uploadPath) {
    if (!uploadPath.startsWith("hdfs:")) {
      uploadPath = "hdfs://" + uploadPath;
    }

    for (String resource : resources) {
      sqls.add(String.format("add jar %s/%s", uploadPath, resource));
    }
  }

  /**
   * 添加临时函数
   */
  private static void addTempFuncSql(List<String> sqls, List<UdfsInfo> udfsInfos) {
    if (CollectionUtils.isNotEmpty(udfsInfos)) {
      for (UdfsInfo udfsInfo : udfsInfos) {
        sqls.add(MessageFormat
            .format(CREATE_FUNCTION_FORMAT, udfsInfo.getFunc(), udfsInfo.getClassName()));
      }
    }
  }

  /**
   * PHOENIX临时函数
   */
  private static void addPhoenixTempFuncSql(List<String> sqls, List<UdfsInfo> udfsInfos,
      String hdfsPath) {
    if (CollectionUtils.isNotEmpty(udfsInfos)) {
      for (UdfsInfo udfsInfo : udfsInfos) {
        String argTypes = "";
        if (CollectionUtils.isNotEmpty(udfsInfo.getArgTypes())) {
          argTypes = joiner.join(udfsInfo.getArgTypes());
        }
        String hdfsFile = hdfsPath + "/" + udfsInfo.getLibJars().get(0).getRes();

        sqls.add(MessageFormat
            .format(CREATE_PHOENIX_FUNCTION_FORMAT, udfsInfo.getFunc(), argTypes,
                udfsInfo.getReturnType(), udfsInfo.getClassName(), hdfsFile));
      }
    }
  }

  public static void main(String[] args) {
    List<String> sqls = new ArrayList<>();
    List<UdfsInfo> udfsInfos = new ArrayList<>();
    UdfsInfo udfsInfo = new UdfsInfo();
    udfsInfo.setFunc("func");
    udfsInfo.setClassName("test");
    udfsInfo.setReturnType("string");
    udfsInfo.setArgTypes(Arrays.asList("string"));
    ResourceInfo resourceInfo = new ResourceInfo();
    resourceInfo.setRes("test.jar");
    udfsInfo.setLibJars(Arrays.asList(resourceInfo));
    udfsInfos.add(udfsInfo);

    String hdfsPath = "/test/";

    addPhoenixTempFuncSql(sqls, udfsInfos, hdfsPath);
  }
}
