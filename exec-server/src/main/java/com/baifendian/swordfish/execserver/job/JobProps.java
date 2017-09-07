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

import java.util.Date;
import java.util.Map;

public class JobProps {

  /**
   * 项目id
   **/
  private int projectId;

  /**
   * 一个具体任务的 id, 注意不是执行 id
   **/
  private int execJobId;

  /**
   * node Name
   **/
  private String nodeName;

  /**
   * 执行 id
   **/
  private int execId;

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
   * cyc time, 基准时间
   */
  private Date cycTime;

  /**
   * 自定义参数
   **/
  private Map<String, String> definedParams;

  /**
   * 每个 job 有个具体的应用 id, job 是动态的任务
   */
  private String jobAppId;

  /**
   * 工作流的启动时间
   */
  private Date execJobStartTime;

  /**
   * 工作流超时时间
   */
  private int execJobTimeout;

  /**
   * job id
   */
  private String jobId;

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

  public int getExecJobId() {
    return execJobId;
  }

  public void setExecJobId(int execJobId) {
    this.execJobId = execJobId;
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

  public Date getCycTime() {
    return cycTime;
  }

  public void setCycTime(Date cycTime) {
    this.cycTime = cycTime;
  }

  public String getJobAppId() {
    return jobAppId;
  }

  public void setJobAppId(String jobAppId) {
    this.jobAppId = jobAppId;
  }

  public Date getExecJobStartTime() {
    return execJobStartTime;
  }

  public void setExecJobStartTime(Date execJobStartTime) {
    this.execJobStartTime = execJobStartTime;
  }

  public int getExecJobTimeout() {
    return execJobTimeout;
  }

  public void setExecJobTimeout(int execJobTimeout) {
    this.execJobTimeout = execJobTimeout;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }
}
