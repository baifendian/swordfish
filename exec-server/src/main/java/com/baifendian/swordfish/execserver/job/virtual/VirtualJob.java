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
package com.baifendian.swordfish.execserver.job.virtual;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.AbstractProcessJob;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * 空操作的作业 <p>
 */
public class VirtualJob extends AbstractProcessJob {

  /**
   * @param jobId  生成的作业id
   * @param props  作业配置信息,各类作业根据此配置信息生成具体的作业
   * @param logger 日志
   */
  public VirtualJob(String jobId, JobProps props, Logger logger) throws IOException {
    super(jobId, props, logger);
  }

  @Override
  public ProcessBuilder createProcessBuilder() {
    return null;
  }

  @Override
  public void initJobParams() {
  }

  @Override
  public BaseParam getParam() {
    return null;
  }


}
