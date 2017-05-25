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
package com.baifendian.swordfish.execserver.job.yarn;

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.execserver.job.AbstractProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractYarnJob extends AbstractProcessJob {

  private static final Pattern APPLICATION_REGEX = Pattern.compile("application_\\d+_\\d+");
  private static final Pattern JOB_REGEX = Pattern.compile("job_\\d+_\\d+");

  /**
   * 数据库接口
   */
  private FlowDao flowDao;

  /**
   * 应用 links
   */
  private List<String> appLinks;

  /**
   * 日志 links
   */
  private List<String> jobLinks;

  public AbstractYarnJob(JobProps props, Logger logger) {
    super(props, logger);

    flowDao = DaoFactory.getDaoInstance(FlowDao.class);

    appLinks = new ArrayList<>();
    jobLinks = new ArrayList<>();
  }

  @Override
  protected void logProcess(List<String> logs) {
    super.logProcess(logs);

    boolean captureAppLinks = false;
    boolean captureJobLinks = false;

    // 分析日志
    for (String log : logs) {
      String appId = findAppId(log);

      if (StringUtils.isNotEmpty(appId) && !appLinks.contains(appId)) {
        appLinks.add(appId);
        captureAppLinks = true;
      }

      String jobId = findJobId(log);

      if (StringUtils.isNotEmpty(jobId) && !jobLinks.contains(jobId)) {
        jobLinks.add(jobId);
        captureJobLinks = true;
      }
    }

    if (captureAppLinks || captureJobLinks) {
      ExecutionNode executionNode = flowDao.queryExecutionNode(props.getExecId(), props.getNodeName());

      if (captureAppLinks) {
        executionNode.setAppLinkList(appLinks);
      }

      if (captureJobLinks) {
        executionNode.setJobLinkList(appLinks);
      }

      flowDao.updateExecutionNode(executionNode);
    }
  }

  /**
   * 获取 appid <p>
   *
   * @param line
   * @return appid
   */
  protected String findAppId(String line) {
    Matcher matcher = APPLICATION_REGEX.matcher(line);

    if (matcher.find()) {
      return matcher.group();
    }

    return null;
  }

  /**
   * 查找 job id
   *
   * @param line
   * @return
   */
  protected String findJobId(String line) {
    Matcher matcher = JOB_REGEX.matcher(line);

    if (matcher.find()) {
      return matcher.group();
    }

    return null;
  }

  @Override
  public void cancel() throws Exception {
    // 先停止任务
    super.cancel();

    // 然后 kill application
    if (CollectionUtils.isNotEmpty(appLinks)) {
      for (String appid : appLinks) {
        String commandFile = "/tmp/" + props.getJobAppId() + ".kill";
        String cmd = "yarn application -kill " + appid;

        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/sh\n");
        sb.append("BASEDIR=$(cd `dirname $0`; pwd)\n");
        sb.append("cd $BASEDIR\n");

        if (props.getEnvFile() != null) {
          sb.append("source " + props.getEnvFile() + "\n");
        }

        sb.append("\n\n");
        sb.append(cmd);

        FileUtils.writeStringToFile(new File(commandFile), sb.toString(), Charset.forName("UTF-8"));

        String runCmd = "sh " + commandFile;
        if (props.getProxyUser() != null) {
          runCmd = "sudo -u " + props.getProxyUser() + " " + runCmd;
        }

        logger.info("kill cmd:{}", runCmd);

        Runtime.getRuntime().exec(runCmd);
      }
    }
  }

  public static void main(String[] args) {
    String msg = "[INFO] 2017-05-23 18:25:22.268 com.baifendian.swordfish.execserver.runner.node.NodeRunner:[147] -  hive execute log : INFO  : Starting Job = job_1493947416024_0139, Tracking URL = http://hlg-5p149-wangwenting:8088/proxy/application_1493947416024_0139/\n" +
        "job_1493947416024_0140 [INFO] 2017-05-23 18:25:22.268 com.baifendian.swordfish.execserver.runner.node.NodeRunner:[147] -  hive execute log : INFO  : Kill Command = /opt/hadoop/bin/hadoop job  -kill job_1493947416024_0139\n" +
        "[INFO] 2017-05-23 18:25:27.269 com.baifendian.swordfish.execserver.runner.node.NodeRunner:[147] -  hive execute log : INFO  : Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0";

    // 查找 application id
    Matcher matcher = APPLICATION_REGEX.matcher(msg);

    while (matcher.find()) {
      System.out.println(matcher.group());
    }

    // 查找 job id
    matcher = JOB_REGEX.matcher(msg);

    while (matcher.find()) {
      System.out.println(matcher.group());
    }

    // 测试另外的 msg
    msg = "sh.execserver.runner.node.NodeRunner:application[147] -  hive execute log : INFO  : Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0";

    // 查找 application id
    matcher = APPLICATION_REGEX.matcher(msg);

    while (matcher.find()) {
      System.out.println(matcher.group());
    }

    // 查找 job id
    matcher = JOB_REGEX.matcher(msg);

    while (matcher.find()) {
      System.out.println(matcher.group());
    }
  }
}
