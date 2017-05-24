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

import com.baifendian.swordfish.execserver.exception.ExecException;
import com.baifendian.swordfish.execserver.exception.ExecTimeoutException;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.baifendian.swordfish.execserver.utils.ProcessUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractProcessJob extends AbstractJob {

  /**
   * 构建 process 工具
   */
  private ProcessBuilder processBuilder;

  /**
   * 具体的进程
   */
  protected Process process;

  public AbstractProcessJob(JobProps props, Logger logger) {
    super(props, logger);
  }

  /**
   * 创建命令语句
   *
   * @return
   * @throws Exception
   */
  public abstract String createCommand() throws Exception;

  /**
   * 日志处理
   *
   * @param logs
   */
  protected void logProcess(List<String> logs) {
    logger.info("(stdout, stderr) -> \n{}", String.join("\n", logs));
  }

  @Override
  public void before() throws Exception {
  }

  /**
   * 计算节点的超时时间（s） <p>
   *
   * @return 超时时间
   */
  private long calcNodeTimeout() {
    long usedTime = (System.currentTimeMillis() - props.getFlowStartTime().getTime()) / 1000;

    long remainTime = props.getFlowTimeout() - usedTime;

    if (remainTime <= 0) {
      throw new ExecTimeoutException("workflow execution time out");
    }

    return remainTime;
  }

  @Override
  public void process() throws Exception {
    // 如果超时, 直接退出
    long remainTime = calcNodeTimeout();

    try {
      // 初始化
      processBuilder = new ProcessBuilder();

      // 得到每个具体 job 的具体构建方式
      String command = createCommand();

      if (StringUtils.isEmpty(command)) {
        exitCode = 0;
        complete = true;
        return;
      }

      // 工作目录
      String workDir = getWorkingDirectory();

      // 命令语句
      String commandFile = String.format("%s/%s.command", workDir, props.getJobAppId());

      // 得到代理执行用户和工作目录
      String proxyUser = props.getProxyUser();

      logger.info("proxyUser:{}, workDir:{}", proxyUser, workDir);

      // 不存在则创建, 因为可能重试任务
      if (!Files.exists(Paths.get(commandFile))) {
        logger.info("generate command file:{}", commandFile);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#!/bin/sh\n");
        stringBuilder.append("BASEDIR=$(cd `dirname $0`; pwd)\n");
        stringBuilder.append("cd $BASEDIR\n");

        if (props.getEnvFile() != null) {
          stringBuilder.append("source " + props.getEnvFile() + "\n");
        }

        stringBuilder.append("\n\n");
        stringBuilder.append(command);

        // 写数据到文件
        FileUtils.writeStringToFile(new File(commandFile), stringBuilder.toString(), Charset.forName("UTF-8"));
      }

      // 设置运行命令
      processBuilder.command("sudo", "-u", proxyUser, "sh", commandFile);

      // 设置工作目录
      processBuilder.directory(new File(workDir));

      // 将 error 信息 merge 到标准输出流
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();

      started = true;

      // 打印进程的启动命令行
      printCommand(processBuilder);

      // 读取控制台输出
      readProcessOutput();

      // 等待运行完毕
      process.waitFor(remainTime, TimeUnit.SECONDS);

      exitCode = process.exitValue();
    } catch (InterruptedException e) {
      logger.error("interrupt exception, maybe task has been cancel or killed.");
      exitCode = -1;
      throw new ExecException("Process has been interrupted. Exit code is " + exitCode);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      exitCode = -1;
      throw new ExecException("Process error. Exit code is " + exitCode);
    } finally {
      complete = true;
    }
  }

  @Override
  public void after() throws Exception {
  }

  @Override
  public void cancel() throws Exception {
    if (process == null) {
      throw new IllegalStateException("not started.");
    }

    int processId = getProcessId(process);

    logger.info("cancel job:{}, kill process:{}", props.getJobAppId(), processId);

    // kill, 等待完成
    boolean killed = softKill(processId, 500, TimeUnit.MILLISECONDS);

    if (!killed) {
      logger.warn("Kill with signal TERM failed. Killing with KILL signal.");
      hardKill(processId);
    }
  }

  /**
   * 是否还在运行
   *
   * @return
   */
  private boolean isRunning() {
    return isStarted() && !isCompleted();
  }

  /**
   * 检测是否启动过了
   */
  private void checkStarted() {
    if (!isStarted()) {
      throw new IllegalStateException("process has not yet started.");
    }
  }

  /**
   * @param processId
   * @param time
   * @param unit
   * @return
   * @throws InterruptedException
   */
  private boolean softKill(int processId, final long time, final TimeUnit unit) throws InterruptedException {
    checkStarted();

    if (processId != 0 && isRunning()) {
      try {
        String cmd;
        if (props.getProxyUser() != null) {
          cmd = String.format("sudo -u %s kill %d", props.getProxyUser(), processId);
        } else {
          cmd = String.format("kill %d", processId);
        }

        Runtime.getRuntime().exec(cmd);
      } catch (IOException e) {
        logger.info("kill attempt failed.", e);
      }

      return false;
    }

    return false;
  }

  /**
   * 直接 kill
   *
   * @param processId
   */
  public void hardKill(int processId) {
    checkStarted();

    if (processId != 0 && isRunning()) {
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

      process.destroy();
    }
  }

  /**
   * 打印命令
   *
   * @param processBuilder
   */
  private void printCommand(ProcessBuilder processBuilder) {
    String cmdStr;

    try {
      cmdStr = ProcessUtil.genCmdStr(processBuilder.command());
      logger.info("job run command:\n{}", cmdStr);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
   * 得到进程 id
   *
   * @param process
   * @return
   */
  private int getProcessId(Process process) {
    int processId = 0;

    try {
      Field f = process.getClass().getDeclaredField("pid");
      f.setAccessible(true);

      processId = f.getInt(process);
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
    }

    return processId;
  }

  /**
   * 获取进程的标准输出, 并进行处理
   */
  private void readProcessOutput() {
    String threadLoggerInfoName = String.format("LoggerInfo-%s", props.getJobAppId());

    Thread loggerInfoThread = new Thread(() -> {
      BufferedReader reader = null;

      List<String> logs = new ArrayList<>();

      try {
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        long preFlushTime = System.currentTimeMillis();

        while ((line = reader.readLine()) != null) {
          logs.add(line);

          long now = System.currentTimeMillis();

          // 到一定日志量就输出处理
          if (logs.size() >= Constants.defaultLogBufferSize || now - preFlushTime > Constants.defaultLogFlushInterval) {
            preFlushTime = now;
            logProcess(logs);
            logs.clear();
          }
        }
      } catch (Exception e) {
        // Do Nothing
      } finally {
        // 还有日志, 继续输出
        if (!logs.isEmpty()) {
          logProcess(logs);
          logs.clear();
        }

        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
    }, threadLoggerInfoName);

    try {
      loggerInfoThread.setDaemon(true);
      loggerInfoThread.start();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }
}