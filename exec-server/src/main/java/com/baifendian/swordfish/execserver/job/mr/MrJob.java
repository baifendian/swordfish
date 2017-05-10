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

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.mr.MrParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.yarn.AbstractYarnJob;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * mr 作业 <p>
 */
public class MrJob extends AbstractYarnJob {

  /**
   * hadoop
   */
  private static final String HADOOP_COMMAND = "hadoop";

  /**
   * jar
   */
  private static final String HADOOP_JAR = "jar";

  /**
   * mapreduce param
   */
  private MrParam mrParam;

  /**
   * @param jobId
   * @param props
   * @param logger
   * @throws IOException
   */
  public MrJob(String jobId, JobProps props, Logger logger) throws IOException {
    super(jobId, props, logger);
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

  @Override
  public BaseParam getParam() {
    return mrParam;
  }

}
