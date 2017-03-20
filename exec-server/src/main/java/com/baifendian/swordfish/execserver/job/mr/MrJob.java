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
package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * mr 作业 <p>
 *
 * @author : dsfan
 * @date : 2016年11月21日
 */
public class MrJob extends AbstractProcessJob {

  /**
   * hadoop
   */
  private static final String HADOOP_COMMAND = "hadoop";

  /**
   * jar
   */
  private static final String HADOOP_JAR = "jar";

  private MrParam mrParam;

  /**
   * yarn 的 application id
   */
  private String appid;

  /**
   *
   * @param jobIdLog
   * @param props
   * @param logger
   * @throws IOException
   */
  public MrJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);
  }

  @Override
  public void initJobParams() {
    mrParam = JsonUtil.parseObject(props.getJobParams(), MrParam.class);
    mrParam.setQueue(props.getQueue());
  }

  public List<String> buildCommand() {
    if (mrParam.getArgs() != null) {
      String args = ParamHelper.resolvePlaceholders(mrParam.getArgs(), definedParamMap);
      mrParam.setArgs(args);
    }
    return HadoopJarArgsUtil.buildArgs(mrParam);
  }

  @Override
  public ProcessBuilder createProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder(HADOOP_COMMAND);
    processBuilder.command().add(HADOOP_JAR);
    List<String> args = buildCommand();
    if (args != null) {
      processBuilder.command().addAll(args);
    }

    return processBuilder;
  }

  public String getAppId() {
    return appid;
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
        // jobContext.appendLog(line);
        logger.info("MR execute log : {}", line);
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
    if (line.contains("impl.YarnClientImpl: Submitted application")) {
      return line.substring(line.indexOf("application") + "application".length() + 1);
    }
    return null;
  }

  public void cancel() throws Exception {
    super.cancel();

    if (appid != null) {
      String cmd = "yarn application -kill " + appid;

      if (props.getProxyUser() != null) {
        cmd = "sudo -u " + props.getProxyUser() + " " + cmd;
      }
      Runtime.getRuntime().exec(cmd);

      completeLatch.await(KILL_TIME_MS, TimeUnit.MILLISECONDS);
    }

  }

  @Override
  public BaseParam getParam() {
    return mrParam;
  }

}
