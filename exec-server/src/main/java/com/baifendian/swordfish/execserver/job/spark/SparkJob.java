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
package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.PlaceholderUtil;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import com.baifendian.swordfish.execserver.utils.OsUtil;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Spark 作业 <p>
 *
 * @author : dsfan
 * @date : 2016年11月3日
 */
public class SparkJob extends AbstractProcessJob {

  /**
   * 提交的参数
   */
  private SparkParam sparkParam;

  /**
   * app id
   **/
  private String appid;

  public SparkJob(String jobId, JobProps props, Logger logger) throws IllegalAccessException, IOException {
    super(jobId, props, logger);
  }

  @Override
  public void initJobParams() {
    sparkParam = JsonUtil.parseObject(props.getJobParams(), SparkParam.class);
    sparkParam.setQueue(props.getQueue());
    if (sparkParam.getArgs() != null) {
      String args = ParamHelper.resolvePlaceholders(sparkParam.getArgs(), props.getDefinedParams());
      sparkParam.setArgs(args);
    }
  }

  public List<String> buildCommand() {
    return SparkSubmitArgsUtil.buildArgs(sparkParam);
  }

  @Override
  public ProcessBuilder createProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder("spark-submit");
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
    return sparkParam;
  }

}
