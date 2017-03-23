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
import java.util.Properties;

public class BaseConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfig.class);

  private static String localDataBasePath; // 本地目录, 用于存放 资源和工作流 的数据

  private static String hdfsDataBasePath; // hdfs 目录, 用于存放 资源和工作流 的数据

  private static String localExecBasePath; // 本地目录, 用于执行工作流

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
      hdfsDataBasePath = properties.getProperty("hdfs.data.base.path");
      localExecBasePath = properties.getProperty("local.exec.base.path");

      systemEnvPath = properties.getProperty("sf.env.file");
      // 没有配置时使用部署用户的环境变量文件
      if (StringUtils.isEmpty(systemEnvPath))
        systemEnvPath = System.getProperty("user.home") + File.separator + ".bash_profile";
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   * 本地的资源数据缓存文件目录
   *
   * @param projectId
   * @return
   */
  public static String getLocalResourceDir(int projectId) {
    return MessageFormat.format("{0}/{1}/resources", localDataBasePath, projectId);
  }

  /**
   * 本地的资源数据缓存文件名称
   *
   * @param projectId
   * @param resName
   * @return
   */
  public static String getLocalResourceFilename(int projectId, String resName) {
    return MessageFormat.format("{0}/{1}/resources/{2}", localDataBasePath, projectId, resName);
  }

  /**
   * 本地的工作流数据缓存文件目录
   *
   * @param projectId
   * @return
   */
  public static String getLocalWorkflowDir(int projectId) {
    return MessageFormat.format("{0}/{1}/workflows", localDataBasePath, projectId);
  }

  /**
   * 本地的工作流数据缓存文件名称
   *
   * @param projectId
   * @param workflowName
   * @return
   */
  public static String getLocalWorkflowFilename(int projectId, String workflowName) {
    return MessageFormat.format("{0}/{1}/workflows/{2}.{3}", localDataBasePath, projectId, workflowName, ".zip");
  }

  /**
   * hdfs 上资源的文件目录
   *
   * @param projectId
   * @return
   */
  public static String getHdfsResourcesDir(int projectId) {
    return MessageFormat.format("{0}/{1}/resources", hdfsDataBasePath, projectId);
  }

  /**
   * hdfs 上资源的文件名称
   *
   * @param projectId
   * @param resName
   * @return
   */
  public static String getHdfsResourcesFilename(int projectId, String resName) {
    return MessageFormat.format("{0}/{1}/resources/{2}", hdfsDataBasePath, projectId, resName);
  }

  /**
   * hdfs 上工作流数据的文件目录
   *
   * @param projectId
   * @return
   */
  public static String getHdfsWorkflowDir(int projectId) {
    return MessageFormat.format("{0}/{1}/workflows", hdfsDataBasePath, projectId);
  }

  /**
   * hdfs 上工作流数据的文件名称
   *
   * @param projectId
   * @param workflowName
   * @return
   */
  public static String getHdfsWorkflowFilename(int projectId, String workflowName) {
    return MessageFormat.format("{0}/{1}/workflows/{2}.{3}", hdfsDataBasePath, projectId, workflowName, ".zip");
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
}
