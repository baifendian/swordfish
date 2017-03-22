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
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * 默认的进程 job <p>
 */
public class DefaultProcessJob extends AbstractProcessJob {

  /**
   * 参数
   */
  private ProcessParam param;

  /**
   *
   * @param jobIdLog
   * @param props
   * @param logger
   */
  public DefaultProcessJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);

    if (param.getScript() == null || StringUtils.isEmpty(param.getScript())) {
      throw new ExecException("DefaultProcessJob script param must not null");
    }

  }

  @Override
  public void initJobParams() {
    param = JsonUtil.parseObject(props.getJobParams(), ProcessParam.class);
  }

  @Override
  public ProcessBuilder createProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder(param.getScript());
    if (param.getArgs() != null) {
      processBuilder.command().addAll(param.getArgs());
    }
    if (param.getArgs() != null) {
      processBuilder.environment().putAll(param.getEnvMap());
    }

    return processBuilder;
  }

  @Override
  public BaseParam getParam() {
    return param;
  }

}
