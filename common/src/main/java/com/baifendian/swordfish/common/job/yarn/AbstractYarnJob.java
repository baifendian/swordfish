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
package com.baifendian.swordfish.common.job.yarn;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public abstract class AbstractYarnJob extends AbstractProcessJob {

  private FlowDao flowDao;

  private String logLinks;
  /**
   * yarn 的 application id
   */
  private String appid;

  public AbstractYarnJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);
    flowDao = DaoFactory.getDaoInstance(FlowDao.class);
  }

  @Override
  protected void readProcessOutput() {
    InputStream inputStream = process.getInputStream();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
        if (appid == null) {
          appid = findAppid(line);
        }
        if(logLinks == null){
          logLinks = findLogLinks(line);
          if(logLinks != null){
            ExecutionNode executionNode = flowDao.queryExecutionNode(props.getExecId(), props.getNodeName());
            executionNode.setLogLinks(logLinks);
            flowDao.updateExecutionNode(executionNode);
          }
        }
        // jobContext.appendLog(line);
        logger.info(line);
      }
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
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

  protected String findLogLinks(String line) {
    if (line.contains("The url to track the job:")) {
      return line.substring(line.indexOf("job:") + "job:".length() + 1);
    }
    return null;
  }

  @Override
  public void cancel() throws Exception {
    super.cancel();

    if (appid != null) {
      String commandFile = "/tmp/" + jobIdLog + ".kill";
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
      logger.info("run cmd:{}", runCmd);
      Runtime.getRuntime().exec(runCmd);

      completeLatch.await(KILL_TIME_MS, TimeUnit.MILLISECONDS);
    }

  }
}
