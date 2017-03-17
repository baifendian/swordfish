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
package com.baifendian.swordfish.execserver.job.shell;

import com.baifendian.swordfish.common.job.AbstractJob;
import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.exec.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.UUID;

/**
 * author: smile8 date: 17/12/2016 desc:
 */
public class ShellJob extends AbstractProcessJob {

  /**
   * pipe 的缓冲大小
   */
  private static int BUFFER_SIZE = 1024 * 10 * 4;

  /**
   * 监听间隔
   */
  private static int TIME_OUT = 500;

  private ShellParam shellParam;

  /**
   * 当前执行的路径
   */
  private String currentPath;

  public ShellJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);

    if (!shellParam.checkValid()) {
      throw new ExecException("ShellJob script param can't be null");
    }
    this.currentPath = getWorkingDirectory();

  }

  @Override
  public void initJobParams() {
    shellParam = JsonUtil.parseObject(props.getJobParams(), ShellParam.class);
    String script = shellParam.getScript();
    script = ParamHelper.resolvePlaceholders(script, props.getDefinedParams());
    shellParam.setScript(script);
  }

  @Override
  public ProcessBuilder createProcessBuilder() throws IOException {
    logger.info("script:\n{}", shellParam.getScript());
    logger.info("currentPath: {}", currentPath);
    String fileName = currentPath + "/" + jobIdLog + "_" + UUID.randomUUID().toString().substring(0, 8) + ".sh";
    Path path = new File(fileName).toPath();

    Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

    Files.createFile(path, attr);

    Files.write(path, shellParam.getScript().getBytes(), StandardOpenOption.APPEND);
    ProcessBuilder processBuilder = new ProcessBuilder(fileName);

    return processBuilder;
  }

  @Override
  public BaseParam getParam() {
    return shellParam;
  }

}
