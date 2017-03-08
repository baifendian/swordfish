
package com.baifendian.swordfish.execserver.job.shell;

import com.baifendian.swordfish.common.job.AbstractJob;
import com.baifendian.swordfish.common.job.AbstractProcessJob;
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

  private ShellParam param;

  /**
   * 当前执行的路径
   */
  private String currentPath;

  public ShellJob(String jobId, JobProps props, Logger logger) throws IOException {
    super(jobId, props, logger);

    if (param.getValue() == null || StringUtils.isEmpty(param.getValue())) {
      throw new ExecException("ShellJob value param can't be null");
    }
    this.currentPath = getWorkingDirectory();

    logger.info("script:\n{}", param.getValue());
    logger.info("currentPath: {}", currentPath);
  }

  @Override
  public void initJobParams(){
    param = JsonUtil.parseObject(props.getJobParams(), ShellParam.class);
    String value = param.getValue();
    value = ParamHelper.resolvePlaceholders(value, props.getDefinedParams());
    param.setValue(value);
  }

  @Override
  public ProcessBuilder createProcessBuilder() throws IOException {
    String fileName = currentPath + "/run_" + UUID.randomUUID().toString().substring(0, 8) + ".sh";
    Path path = new File(fileName).toPath();

    Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

    Files.createFile(path, attr);

    Files.write(path, param.getValue().getBytes(), StandardOpenOption.APPEND);
    ProcessBuilder processBuilder = new ProcessBuilder(fileName);

    return processBuilder;
  }

}
