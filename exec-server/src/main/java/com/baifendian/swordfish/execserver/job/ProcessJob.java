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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * 以 shell 命令的方式运行一个 job
 */
public class ProcessJob {

  /**
   * 构建 process 工具
   */
  private ProcessBuilder processBuilder;

  /**
   * 具体的进程
   */
  private Process process;

  /**
   * 日志处理器
   */
  private Consumer<List<String>> logHandler;

  /**
   * 长任务是否完成
   **/
  private BooleanSupplier isCompleted;

  /**
   * 是否长任务
   */
  private boolean isLongJob;

  /**
   * 工作目录
   */
  private String workDir;

  /**
   * 任务的运行 id, 和 workDir 一起构成了 command 文件名称
   */
  private String jobAppId;

  /**
   * 代理用户名称
   */
  private String proxyUser;

  /**
   * 环境文件
   */
  private String envFile;

  /**
   * 起始运行时间
   */
  private Date startTime;

  /**
   * 超时时间
   */
  private int timeout;

  /**
   * 日志记录
   */
  private Logger logger;


  /**
   * 日志记录
   */
  private final List<String> logs;

  public ProcessJob(Consumer<List<String>> logHandler, BooleanSupplier isCompleted,
      boolean isLongJob, String workDir, String jobAppId, String proxyUser, String envFile,
      Date startTime, int timeout, Logger logger) {
    this.logHandler = logHandler;
    this.isCompleted = isCompleted;
    this.isLongJob = isLongJob;
    this.workDir = workDir;
    this.jobAppId = jobAppId;
    this.proxyUser = proxyUser;
    this.envFile = envFile;
    this.startTime = startTime;
    this.timeout = timeout;
    this.logger = logger;
    this.logs = Collections.synchronizedList(new ArrayList<>());
  }

  /**
   * 计算节点的超时时间（s） <p>
   *
   * @return 超时时间
   */
  private long calcNodeTimeout() {
    long usedTime = (System.currentTimeMillis() - startTime.getTime()) / 1000;
    long remainTime = timeout - usedTime;

    if (remainTime <= 0 && !isLongJob) {
      throw new ExecTimeoutException("workflow or streaming job execution time out");
    }

    return remainTime;
  }

  /**
   * 具体的任务处理, 这里是获取 shell 命令得到执行
   *
   * @param command 待运行的命令
   * @return 返回运行的退出码, 0 表示成功, 其它表示失败
   */
  public int runCommand(String command) {
    // 如果超时, 直接退出
    long remainTime = calcNodeTimeout();
    int exitCode;

    try {
      // 初始化
      processBuilder = new ProcessBuilder();

      // 得到每个具体 job 的具体构建方式
      if (StringUtils.isEmpty(command)) {
        exitCode = 0;
        return exitCode;
      }

      // 命令语句
      String commandFile = String.format("%s/%s.command", workDir, jobAppId);

      logger.info("proxy user:{}, work dir:{}", proxyUser, workDir);

      // 不存在则创建, 因为可能重试任务
      if (!Files.exists(Paths.get(commandFile))) {
        logger.info("generate command file:{}", commandFile);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#!/bin/sh\n");
        stringBuilder.append("BASEDIR=$(cd `dirname $0`; pwd)\n");
        stringBuilder.append("cd $BASEDIR\n");

        if (envFile != null) {
          stringBuilder.append("source " + envFile + "\n");
        }

        stringBuilder.append("\n\n");
        stringBuilder.append(command);

        // 写数据到文件
        FileUtils.writeStringToFile(new File(commandFile), stringBuilder.toString(),
            Charset.forName("UTF-8"));

        // 替换 ^M, windows 的字符串
        String cmd = String.format("sed -i -e 's/\r$//' %s", commandFile);
        Runtime.getRuntime().exec(cmd);
      }

      // 设置运行命令

      processBuilder.command("sudo", "-u", proxyUser, "sh", commandFile);

      // 设置工作目录
      processBuilder.directory(new File(workDir));

      // 将 error 信息 merge 到标准输出流
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();

      // 打印进程的启动命令行
      printCommand(processBuilder);

      // 读取控制台输出
      readProcessOutput();

      int pid = getProcessId(process);

      logger.info("Process start, process id is: {}", pid);

      // 长任务是比较特殊的
      if (isLongJob) {
        // 如果没有完成, 会循环, 认为是没有提交
        // 对于流任务, 最多等待 10 分钟, 不然会认为超时退出
        while (!isCompleted.getAsBoolean() && process.isAlive()) {
          Thread.sleep(3000);
        }

        logger.info("streaming job has exit, work dir:{}, pid:{}", workDir, pid);

        // 对有的框架来说, 比如 storm, 不能根据这个状态来判断正确性
        exitCode = (isCompleted.getAsBoolean()) ? 0 : -1;
      } else {// 等待运行完毕
        boolean status = process.waitFor(remainTime, TimeUnit.SECONDS);

        if (status) {
          exitCode = process.exitValue();
          logger.info("job has exit, work dir:{}, pid:{}", workDir, pid);
        } else {
          cancel();
          exitCode = -1;
          logger.info("job has timeout, work dir:{}, pid:{}", workDir, pid);
        }
      }
    } catch (InterruptedException e) {
      logger.error("interrupt exception, maybe task has been cancel or killed.");
      exitCode = -1;
      throw new ExecException("Process has been interrupted. Exit code is " + exitCode);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      exitCode = -1;
      throw new ExecException("Process error. Exit code is " + exitCode);
    }

    return exitCode;
  }

  /**
   * 取消一个具体的 shell 任务
   */
  public void cancel() throws Exception {
    if (process == null) {
      return;
    }

    // 清理一下日志
    clear();

    int processId = getProcessId(process);

    logger.info("cancel process: {}", processId);

    // kill, 等待完成
    boolean killed = softKill(processId);

    if (!killed) {
      // 强制关闭
      hardKill(processId);

      // destory
      process.destroy();

      // 强制为 null
      process = null;
    }
  }

  /**
   * @param processId 进程 id
   */
  private boolean softKill(int processId)
      throws InterruptedException {
    // 不是 0 号进程, 且进程存在
    if (processId != 0 && process.isAlive()) {
      try {
        // 注意通过 sudo -u user command 运行的命令, 是不能直接通过 user 来 kill 的
        String cmd = String.format("sudo kill %d", processId);

        logger.info("softkill job:{}, process id:{}, cmd:{}", jobAppId, processId, cmd);

        Runtime.getRuntime().exec(cmd);
      } catch (IOException e) {
        logger.info("kill attempt failed.", e);
      }
    }

    return process.isAlive();
  }

  /**
   * 直接 kill
   *
   * @param processId 进程 id
   */
  private void hardKill(int processId) {
    // 不是 0 号进程, 且进程存在
    if (processId != 0 && process.isAlive()) {
      try {
        String cmd = String.format("sudo kill -9 %d", processId);

        logger.info("hardKill job:{}, process id:{}, cmd:{}", jobAppId, processId, cmd);

        Runtime.getRuntime().exec(cmd);
      } catch (IOException e) {
        logger.error("Kill attempt failed.", e);
      }
    }
  }

  /**
   * 打印命令
   *
   * @param processBuilder 进程构建器
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
   * @param process 进程信息
   * @return 得到进程的 id
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
   * 执行清理
   */
  private void clear() {
    if (!logs.isEmpty()) {
      // 日志处理器
      logHandler.accept(logs);

      logs.clear();
    }
  }

  /**
   * 获取进程的标准输出, 并进行处理
   */
  private void readProcessOutput() {
    String threadLoggerInfoName = String.format("LoggerInfo-%s", jobAppId);

    Thread loggerInfoThread = new Thread(() -> {
      BufferedReader reader = null;

      try {
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        long preFlushTime = System.currentTimeMillis();

        while ((line = reader.readLine()) != null) {
          logs.add(line);

          long now = System.currentTimeMillis();

          // 到一定日志量就输出处理
          if (logs.size() >= Constants.defaultLogBufferSize
              || now - preFlushTime > Constants.defaultLogFlushInterval) {
            preFlushTime = now;

            // 日志处理器
            logHandler.accept(logs);

            logs.clear();
          }
        }
      } catch (Exception e) {
        // Do Nothing
      } finally {
        // 还有日志, 继续输出
        if (!logs.isEmpty()) {
          // 日志处理器
          logHandler.accept(logs);

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