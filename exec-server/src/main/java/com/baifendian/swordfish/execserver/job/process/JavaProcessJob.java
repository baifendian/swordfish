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
package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java 进程 <p>
 *
 * @author : dsfan
 * @date : 2016年11月14日
 */
public class JavaProcessJob extends AbstractProcessJob {

  /**
   * java
   */
  private static final String JAVA_COMMAND = "java";

  /**
   * java 命令可选参数
   */
  private static List<String> javaOptList = new ArrayList<>();

  /**
   * 参数
   */
  private ProcessParam param;

  static {
      // java 可选参数
      Configuration conf = null;
      try {
        conf = new PropertiesConfiguration("job/hive.properties");
      } catch (ConfigurationException e) {
        e.printStackTrace();
      }
    String javaOpts = conf.getString("job.java.opts", "");
    if (StringUtils.isNotEmpty(javaOpts)) {
      javaOptList.addAll(Arrays.asList(javaOpts.split(" ")));
    }
  }

  public JavaProcessJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);

  }

  @Override
  public void initJobParams() {
    param = JsonUtil.parseObject(props.getJobParams(), ProcessParam.class);
  }

  @Override
  public ProcessBuilder createProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder(JAVA_COMMAND);
    if (!javaOptList.isEmpty()) {
      processBuilder.command().addAll(javaOptList);
    }
    if (param.getArgs() != null) {
      processBuilder.command().addAll(param.getArgs());
    }
    if (param.getEnvMap() != null) {
      processBuilder.environment().putAll(param.getEnvMap());
    }

    // List<String> commands=new ArrayList<String>();
    // String command = JAVA_COMMAND + " ";
    // command += getJVMArguments() + " ";
    // command += "-Xms" + getInitialMemorySize() + " ";
    // command += "-Xmx" + getMaxMemorySize() + " ";
    // if(getClassPaths()!=null && !getClassPaths().trim().equals("")){
    // command += "-cp " + getClassPaths()+ " ";
    // }
    // command += getJavaClass() + " ";
    // command += getMainArguments();

    // commands.add(command);

    return processBuilder;
  }

  @Override
  public BaseParam getParam() {
    return param;
  }

}
