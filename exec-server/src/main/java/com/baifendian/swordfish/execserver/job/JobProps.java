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

import java.util.Map;

public class JobProps {

  /**
   * 项目id
   **/
  private int projectId;

  /**
   * flow id
   **/
  private int workflowId;

  /**
   * node Name
   **/
  private String nodeName;

  /**
   * 执行 id
   **/
  private int execId;

  /**
   * 即席查询时的 adHocId
   */
  private int adHocId;

  /**
   * 作业执行用户
   **/
  private String proxyUser;

  /**
   * 作业配置参数
   **/
  private String jobParams;

  /**
   * 作业执行目录
   **/
  private String workDir;

  /**
   * 作业执行队列
   **/
  private String queue;

  /**
   * 环境变量文件
   **/
  private String envFile;

  /**
   * 自定义参数
   **/
  private Map<String, String> definedParams;

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getJobParams() {
    return jobParams;
  }

  public void setJobParams(String jobParams) {
    this.jobParams = jobParams;
  }

  public String getWorkDir() {
    return workDir;
  }

  public void setWorkDir(String workDir) {
    this.workDir = workDir;
  }

  public Map<String, String> getDefinedParams() {
    return definedParams;
  }

  public void setDefinedParams(Map<String, String> definedParams) {
    this.definedParams = definedParams;
  }

  public String getEnvFile() {
    return envFile;
  }

  public void setEnvFile(String envFile) {
    this.envFile = envFile;
  }

  public int getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(int workflowId) {
    this.workflowId = workflowId;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public int getAdHocId() {
    return adHocId;
  }

  public void setAdHocId(int adHocId) {
    this.adHocId = adHocId;
  }
}
