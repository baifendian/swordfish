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

import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.hadoop.HdfsExecException;
import com.baifendian.swordfish.execserver.utils.ProcessUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class AbstractProcessJob extends AbstractJob {

  protected boolean started = false;

  protected final CountDownLatch completeLatch;

  protected final long KILL_TIME_MS = 5000;

  /**
   * @param jobIdLog  生成的作业idLog
   * @param props  作业配置信息,各类作业根据此配置信息生成具体的作业
   * @param logger 日志
   */
  protected AbstractProcessJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);
    completeLatch = new CountDownLatch(1);
  }

  /**
   * 创建 ProcessBuilder <p>
   *
   * @return {@link ProcessBuilder}
   */
  public abstract ProcessBuilder createProcessBuilder() throws Exception;

  @Override
  public void before() throws Exception {
  }

  @Override
  public void process() throws Exception {
    try {
      ProcessBuilder processBuilder = createProcessBuilder();
      if (processBuilder == null) {
        exitCode = 0;
        complete = true;
        return;
      }

      String proxyUser = getProxyUser();
      String workDir = getWorkingDirectory();
      logger.info("jobId:{} proxyUser:{} workDir:{}", jobId, proxyUser, workDir);
      if (proxyUser != null) {
        String commandFile = workDir + File.separator + jobId + ".command";
        logger.info("generate command file:{}", commandFile);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#!/bin/sh\n");
        stringBuilder.append("BASEDIR=$(cd `dirname $0`; pwd)\n");
        stringBuilder.append("cd $BASEDIR\n");
        if (props.getEnvFile() != null) {
          stringBuilder.append("source " + props.getEnvFile() + "\n");
        }
        stringBuilder.append("\n\n");
        stringBuilder.append(StringUtils.join(processBuilder.command(), " "));
        FileUtils.writeStringToFile(new File(commandFile), stringBuilder.toString());
        processBuilder.command("sudo", "-u", proxyUser, "sh", commandFile);
      } else {
        List<String> commands = processBuilder.command();
        processBuilder.command("sh", "-c");
        processBuilder.command().addAll(commands);
      }
      logger.info("run command:{}", processBuilder.command());
      processBuilder.directory(new File(workDir));
      // 将 error 信息 merge 到标准输出流
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();

      started = true;

      // 打印 进程的启动命令行
      printCommand(processBuilder);

      readProcessOutput();
      exitCode = process.waitFor();

      completeLatch.countDown();
    } catch (Exception e) {
      // jobContext.getExecLogger().appendLog(e);
      logger.error(e.getMessage(), e);
      exitCode = -1;
    }
    if (exitCode != 0) {
      throw new HdfsExecException("Process error. Exit code is " + exitCode);
    }
    complete = true;

  }

  @Override
  public void cancel() throws Exception {
    if (process == null) {
      throw new IllegalStateException("not started.");
    }
    int processId = getProcessId(process);
    logger.info("job:{} cancel job. kill process:{}", jobId, processId);

    boolean killed = softKill(processId, KILL_TIME_MS, TimeUnit.MILLISECONDS);
    if (!killed) {
      logger.warn("Kill with signal TERM failed. Killing with KILL signal.");
      hardKill(processId);
    }

  }

  private boolean isStarted() {
    return started;
  }

  private boolean isRunning() {
    return isStarted() && !isCompleted();
  }

  private void checkStarted() {
    if (!isStarted()) {
      throw new IllegalStateException("process has not yet started.");
    }
  }

  private boolean softKill(int processId, final long time, final TimeUnit unit) throws InterruptedException {
    checkStarted();
    if (processId != 0 && isStarted()) {
      try {
        String cmd;
        if (props.getProxyUser() != null) {
          cmd = String.format("sudo -u %s kill %d", props.getProxyUser(), processId);
        } else {
          cmd = String.format("kill %d", processId);
        }
        Runtime.getRuntime().exec(cmd);
        return completeLatch.await(time, unit);
      } catch (IOException e) {
        logger.info("kill attempt failed.", e);
      }
      return false;
    }
    return false;
  }

  public void hardKill(int processId) {
    checkStarted();
    if (isRunning()) {
      if (processId != 0) {
        try {
          String cmd;
          if (props.getProxyUser() != null) {
            cmd = String.format("sudo -u %s kill -9 %d", props.getProxyUser(), processId);
          } else {
            cmd = String.format("kill -9 %d", processId);
          }
          Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
          logger.error("Kill attempt failed.", e);
        }
      }
      process.destroy();
    }
  }

  private void printCommand(ProcessBuilder processBuilder) {
    String cmdStr;
    try {
      cmdStr = ProcessUtil.genCmdStr(processBuilder.command());
      logger.info("job run command：{}", cmdStr);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private int getProcessId(Process process) {
    int processId = 0;
    try {
      Field f = process.getClass().getDeclaredField("pid");
      f.setAccessible(true);

      processId = f.getInt(process);
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return processId;
  }

  /**
   * 获取进程的标准输出 <p>
   */
  protected void readProcessOutput() {
    String threadLoggerInfoName = "LoggerInfo-" + jobId;

    Thread loggerInfoThread = new Thread(new Runnable() {
      @Override
      public void run() {
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
          String line;
          while ((line = reader.readLine()) != null) {
            logger.info("{}", line);
          }
        } catch (Exception e) {
          logger.error("{}", e.getMessage(), e);
        }
      }
    }, threadLoggerInfoName);

    try {
      loggerInfoThread.setDaemon(true);
      loggerInfoThread.start();
      // loggerErrorThread.start();
      // loggerErrorThread.join();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

}
