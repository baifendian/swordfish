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
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractYarnJob extends AbstractProcessJob {

  /**
   * 数据库接口
   */
  private FlowDao flowDao;

  /**
   * 日志 links
   */
  private List<String> logLinks;

  public AbstractYarnJob(JobProps props, Logger logger) {
    super(props, logger);

    flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    logLinks = new ArrayList<>();
  }

  @Override
  protected void logProcess(String log) {
    super.logProcess(log);

    // 分析日志
    String appid = findAppid(log);

    if (appid != null && !logLinks.contains(appid)) {
      logLinks.add(appid);

      ExecutionNode executionNode = flowDao.queryExecutionNode(props.getExecId(), props.getNodeName());
      executionNode.setLogLinkList(logLinks);

      flowDao.updateExecutionNode(executionNode);
    }
  }

  /**
   * 获取 appid <p>
   *
   * @return appid
   */
  private String findAppid(String line) {
    if (line.contains("YarnClientImpl: Submitted application")) {
      return line.substring(line.indexOf("application") + "application".length() + 1);
    }

    return null;
  }

  @Deprecated
  protected String findLogLinks(String line) {
    if (line.contains("The url to track the job:")) {
      return line.substring(line.indexOf("job:") + "job:".length() + 1);
    }

    return null;
  }

  @Override
  public void cancel() throws Exception {
    // 先停止任务
    super.cancel();

    // 然后 kill application
    if (CollectionUtils.isNotEmpty(logLinks)) {
      for (String appid : logLinks) {
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
}
