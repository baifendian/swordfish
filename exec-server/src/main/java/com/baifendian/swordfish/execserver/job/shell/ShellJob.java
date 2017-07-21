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

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.shell.ShellParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.exception.ExecException;
import com.baifendian.swordfish.execserver.job.Job;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.ProcessJob;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import org.slf4j.Logger;

public class ShellJob extends Job {

  private ShellParam shellParam;

  /**
   * 当前执行的路径
   */
  private String currentPath;

  private ProcessJob processJob;

  public ShellJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);

    this.currentPath = props.getWorkDir();

    this.processJob = new ProcessJob(this::logProcess, this::isCompleted, isLongJob,
        props.getWorkDir(), props.getJobAppId(),
        props.getProxyUser(), props.getEnvFile(), props.getExecJobStartTime(),
        props.getExecJobTimeout(), logger);
  }

  @Override
  public void init() {
    logger.debug("job params {}", props.getJobParams());

    shellParam = JsonUtil.parseObject(props.getJobParams(), ShellParam.class);

    if (!shellParam.checkValid()) {
      throw new ExecException("ShellJob script param can't be null");
    }
  }

  @Override
  public void process() throws Exception {
    try {
      // 构造进程
      exitCode = processJob.runCommand(createCommand());
    } catch (Exception e) {
      logger.error("Shell process exception", e);
      exitCode = -1;
    }
  }

  @Override
  public void cancel(boolean cancelApplication) throws Exception {
    // 关闭进程
    processJob.cancel();
  }

  /**
   * 构建 shell 命令
   *
   * @return 返回构建的 shell 命令
   */
  private String createCommand() throws Exception {
    // 生成的脚本文件
    String fileName = String.format("%s/%s_node.sh", currentPath, props.getJobAppId());
    Path path = new File(fileName).toPath();

    if (Files.exists(path)) {
      return fileName;
    }

    String script = shellParam.getScript();
    script = ParamHelper.resolvePlaceholders(script, props.getDefinedParams());

    shellParam.setScript(script);

    logger.info("script:\n{}", shellParam.getScript());
    logger.info("currentPath:{}", currentPath);

    Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

    Files.createFile(path, attr);

    Files.write(path, shellParam.getScript().getBytes(), StandardOpenOption.APPEND);

    // 替换 ^M, windows 的字符串
    String cmd = String.format("sed -i -e 's/\r$//' %s", fileName);
    Runtime.getRuntime().exec(cmd);

    return fileName;
  }

  @Override
  public BaseParam getParam() {
    return shellParam;
  }
}
