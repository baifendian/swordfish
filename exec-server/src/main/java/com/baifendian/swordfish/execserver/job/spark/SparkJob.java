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

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.spark.SparkParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.yarn.AbstractYarnJob;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Spark 作业 <p>
 */
public class SparkJob extends AbstractYarnJob {

  /**
   * 提交的参数
   */
  private SparkParam sparkParam;

  public SparkJob(String jobId, JobProps props, Logger logger) {
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

  protected String findLogLinks(String line) {
    if (line.contains("tracking URL:")) {
      return line.substring(line.indexOf("URL:") + "URL:".length() + 1);
    }
    return null;
  }

  @Override
  public BaseParam getParam() {
    return sparkParam;
  }

}
