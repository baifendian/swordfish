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
package com.baifendian.swordfish.execserver.job;

import java.io.IOException;
import org.slf4j.Logger;

public abstract class AbstractYarnProcessJob extends AbstractYarnJob {

  private ProcessJob processJob;

  /**
   * @param props
   * @param logger
   * @throws IOException
   */
  public AbstractYarnProcessJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);

    this.processJob = new ProcessJob(this::logProcess, this::isCompleted, isLongJob,
        props.getWorkDir(), props.getJobAppId(),
        props.getProxyUser(), props.getEnvFile(), props.getExecJobStartTime(),
        props.getExecJobTimeout(), logger);
  }

  @Override
  public void process() throws Exception {
    try {
      started = true;

      // 构造进程
      exitCode = processJob.runCommand(createCommand());
    } catch (Exception e) {
      logger.error("Yarn process exception", e);
      exitCode = -1;
    } finally {
      complete = true;
    }
  }

  @Override
  public void cancel(boolean cancelApplication) throws Exception {
    super.cancel(cancelApplication);

    // 关闭进程
    processJob.cancel();
  }

  /**
   * 具体运行的命令
   */
  protected abstract String createCommand() throws Exception;
}
