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
package com.baifendian.swordfish.common.config;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public class BaseConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfig.class);

  // 本地目录, 用于存放 资源和工作流 的数据
  private static String localDataBasePath;

  // 本地目录, 用于存放下载的临时文件
  private static String localDownloadBasePath;

  // hdfs 目录, 用于存放 资源和工作流 的数据
  private static String hdfsDataBasePath;

  // 本地目录, 用于执行工作流
  private static String localExecBasePath;

  // 本地目录, udf jar 目录
  private static String jobHiveUdfJarBasePath;

  // 禁用用户列表
  private static Set<String> prohibitUserSet;

  /**
   * 环境变量信息
   */
  private static String systemEnvPath;

  private static Properties properties = new Properties();

  static {
    InputStream is = null;
    try {
      File dataSourceFile = ResourceUtils.getFile("classpath:common/base_config.properties");
      is = new FileInputStream(dataSourceFile);

      properties.load(is);

      localDataBasePath = properties.getProperty("local.data.base.path");
      localDownloadBasePath = properties.getProperty("local.download.base.path");
      hdfsDataBasePath = properties.getProperty("hdfs.data.base.path");
      localExecBasePath = properties.getProperty("local.exec.base.path");
      jobHiveUdfJarBasePath = properties.getProperty("job.hive.udfjar.hdfs.basepath");

      systemEnvPath = properties.getProperty("sf.env.file");

      // 没有配置时使用部署用户的环境变量文件
      if (StringUtils.isEmpty(systemEnvPath)) {
        systemEnvPath = System.getProperty("user.home") + File.separator + ".bash_profile";
      }

      prohibitUserSet = new HashSet<>();
      for (String user : properties.getProperty("prohibit.user.list").split(",")) {
        LOGGER.info("prohibit user: {}", user);
        prohibitUserSet.add(user);
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   * 得到下载到本地的文件名称, 能保证区分开来名称
   *
   * @param projectId
   * @param filename
   * @return
   */
  public static String getLocalDownloadFilename(int projectId, String filename) {
    return MessageFormat.format("{0}/{1}/{2}", localDownloadBasePath, UUID.randomUUID().toString(), filename);
  }

  /**
   * local 上项目的文件目录
   *
   * @param projectId
   * @return
   */
  public static String getLocalProjectDir(int projectId) {
    return MessageFormat.format("{0}/{1}", localDataBasePath, projectId);
  }

  /**
   * 本地的资源数据缓存文件目录
   *
   * @param projectId
   * @return
   */
  public static String getLocalResourceDir(int projectId) {
    return MessageFormat.format("{0}/resources", getLocalProjectDir(projectId));
  }

  /**
   * 本地的资源数据缓存文件名称
   *
   * @param projectId
   * @param filename
   * @return
   */
  public static String getLocalResourceFilename(int projectId, String filename) {
    return MessageFormat.format("{0}/{1}", getLocalResourceDir(projectId), filename);
  }

  /**
   * 本地的工作流数据缓存文件目录
   *
   * @param projectId
   * @return
   */
  public static String getLocalWorkflowDir(int projectId) {
    return MessageFormat.format("{0}/workflows", getLocalProjectDir(projectId));
  }

  /**
   * 本地的工作流数据缓存文件名称
   *
   * @param projectId
   * @param filename
   * @return
   */
  public static String getLocalWorkflowFilename(int projectId, String filename) {
    return MessageFormat.format("{0}/{1}.{2}", getLocalWorkflowDir(projectId), filename, ".zip");
  }

  /**
   * 本地工作流数据缓存解压文件夹名
   *
   * @param projectId
   * @param filename
   * @return
   */
  public static String getLocalWorkflowExtractDir(int projectId, String filename) {
    return MessageFormat.format("{0}/{1}", getLocalWorkflowDir(projectId), filename);
  }

//  /**
//   * 本地工作流数据缓存 WORKFLOW.json 文件
//   *
//   * @param projectId
//   * @param filename
//   * @return
//   */
//  public static String getLocalWorkflowJson(int projectId, String filename) {
//    return MessageFormat.format("{0}/{1}", getLocalWorkflowExtractDir(projectId, filename), "WORKFLOW.json");
//  }

  /**
   * hdfs 上项目的文件目录
   *
   * @param projectId
   * @return
   */
  public static String getHdfsProjectDir(int projectId) {
    return MessageFormat.format("{0}/{1}", hdfsDataBasePath, projectId);
  }

  /**
   * hdfs 上资源的文件目录
   *
   * @param projectId
   * @return
   */
  public static String getHdfsResourcesDir(int projectId) {
    return MessageFormat.format("{0}/resources", getHdfsProjectDir(projectId));
  }

  /**
   * hdfs 上资源的文件名称
   *
   * @param projectId
   * @param filename
   * @return
   */
  public static String getHdfsResourcesFilename(int projectId, String filename) {
    return MessageFormat.format("{0}/{1}", getHdfsResourcesDir(projectId), filename);
  }

  /**
   * hdfs 上工作流数据的文件目录
   *
   * @param projectId
   * @return
   */
  public static String getHdfsWorkflowDir(int projectId) {
    return MessageFormat.format("{0}/workflows", getHdfsProjectDir(projectId));
  }

  /**
   * hdfs 上工作流数据的文件名称
   *
   * @param projectId
   * @param filename
   * @return
   */
  public static String getHdfsWorkflowFilename(int projectId, String filename) {
    return MessageFormat.format("{0}/{1}.{2}", getHdfsWorkflowDir(projectId), filename, "zip");
  }

  /**
   * 工作流执行的目录
   *
   * @param projectId
   * @param workflowId
   * @param execId
   * @return
   */
  public static String getFlowExecDir(int projectId, int workflowId, long execId) {
    return String.format("%s/%d/%d/%d", localExecBasePath, projectId, workflowId, execId);
  }

  /**
   * 得到系统环境变量路径
   *
   * @return
   */
  public static String getSystemEnvPath() {
    return systemEnvPath;
  }

  /**
   * 返回 hive 的 udf jar 路径
   *
   * @return
   */
  public static String getJobHiveUdfJarBasePath() {
    return jobHiveUdfJarBasePath;
  }

  /**
   * 是否禁用用户
   *
   * @param user
   * @return
   */
  public static boolean isProhibitUser(String user) {
    return prohibitUserSet.contains(user);
  }
}
