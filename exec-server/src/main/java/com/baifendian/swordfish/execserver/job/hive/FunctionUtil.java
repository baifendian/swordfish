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

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.job.struct.ResourceInfo;
import com.baifendian.swordfish.common.job.struct.hql.UdfsInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.baifendian.swordfish.common.utils.StructuredArguments.jobValue;

public class FunctionUtil {

  private static final Logger logger = LoggerFactory.getLogger(FunctionUtil.class);

  private static final String CREATE_FUNCTION_FORMAT = "create temporary function {0} as ''{1}''";

  private static String hiveUdfJarBasePath = BaseConfig.getJobHiveUdfJarBasePath();

  /**
   * 创建自定义函数, 即 udf
   *
   * @param udfsInfos
   * @param jobId
   * @param srcDir
   * @param isHdfsFile
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public static List<String> createFuncs(List<UdfsInfo> udfsInfos, String jobId, String srcDir, boolean isHdfsFile) throws IOException, InterruptedException {
    // 是否定义了 udf 的基本目录
    if (StringUtils.isEmpty(hiveUdfJarBasePath)) {
      logger.error("{} not define hive udf jar path", jobValue(jobId));
      throw new ExecException("Hive udf jar base path not defined ");
    }

    List<String> funcList = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(udfsInfos)) {
      Set<String> resources = getFuncResouces(udfsInfos);

      if (CollectionUtils.isNotEmpty(resources)) {
        String uploadPath = hiveUdfJarBasePath + File.separator + jobId;

        // adHoc 查询时直接通过复制 hdfs 上文件的方式来处理
        if (isHdfsFile) {
          copyJars(resources, uploadPath, srcDir);
        } else {
          uploadUdfJars(resources, uploadPath, srcDir);
        }

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
   *
   * @param resources
   * @param tarDir
   * @param srcDir
   * @throws IOException
   * @throws InterruptedException
   */
  private static void uploadUdfJars(Set<String> resources, String tarDir, String srcDir) throws IOException, InterruptedException {
    HdfsClient hdfsClient = HdfsClient.getInstance();

    if (!hdfsClient.exists(tarDir)) {
      hdfsClient.mkdir(tarDir);
    }

    for (String res : resources) {
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

  /**
   * 复制资源文件到目标目录
   *
   * @param resources
   * @param tarDir
   * @param srcDir
   * @throws IOException
   * @throws InterruptedException
   */
  private static void copyJars(Set<String> resources, String tarDir, String srcDir) throws IOException, InterruptedException {
    HdfsClient hdfsClient = HdfsClient.getInstance();

    if (!hdfsClient.exists(tarDir)) {
      hdfsClient.mkdir(tarDir);
    }

    for (String res : resources) {
      logger.debug("create symlink {}/{} -> {}/{}", tarDir, res, srcDir, res);
      hdfsClient.copy(String.format("%s/%s", srcDir, res), String.format("%s/%s", tarDir, res), false, false);
    }
  }

  /**
   * 添加 jar
   *
   * @param sqls
   * @param resources
   * @param uploadPath
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
   *
   * @param sqls
   * @param udfsInfos
   */
  private static void addTempFuncSql(List<String> sqls, List<UdfsInfo> udfsInfos) {
    for (UdfsInfo udfsInfo : udfsInfos) {
      sqls.add(MessageFormat.format(CREATE_FUNCTION_FORMAT, udfsInfo.getFunc(), udfsInfo.getClassName()));
    }
  }
}
