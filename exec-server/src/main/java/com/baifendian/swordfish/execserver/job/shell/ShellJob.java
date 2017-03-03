
package com.baifendian.swordfish.execserver.job.shell;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.exception.ExecException;
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

  /**
   * shell 脚本
   */
  private final String script;

  /**
   * 超时时间(单位: 毫秒)
   */
  private long timeout;

  /**
   * 当前执行的路径
   */
  private String currentPath;

  public ShellJob(String jobId, PropertiesConfiguration props, Logger logger) throws IOException {
    super(jobId, props, logger);

    this.script = (String)jobParams.get("value");
    if (script == null || StringUtils.isEmpty(script)) {
      throw new ExecException("ShellJob value param can't be null");
    }
    this.timeout = TIME_OUT;
    this.currentPath = (String)jobParams.get("currentPath");

    logger.info("script:\n{}", script);
    logger.info("timeout: {}, currentPath: {}", timeout, currentPath);
  }

  @Override
  public ProcessBuilder createProcessBuilder() {
    return null;
  }

  @Override
  protected void readProcessOutput() {
  }

  private void readInputStream(InputStream inputStream) throws IOException {
    int length = inputStream.available();

    if (length <= 0) {
      return;
    }

    byte[] buffer = new byte[length];

    inputStream.read(buffer, 0, length);

    logger.info("{}", new String(buffer));
  }

  @Override
  public void exec() {
    Path path = Paths.get(currentPath, "run_" + UUID.randomUUID().toString().substring(0, 8) + ".sh");

    try {
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr--");
      FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

      Files.createFile(path, attr);

      Files.write(path, script.getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
      exitCode = -1;
      complete = true;

      logger.error("create file exception", e);
      return;
    }

    CommandLine cmdLine = new CommandLine(path.toFile());

    DefaultExecutor executor = new DefaultExecutor();
    executor.setWorkingDirectory(new File(currentPath));

    PipedOutputStream outputStream = new PipedOutputStream();
    PipedInputStream inputStream = new PipedInputStream(BUFFER_SIZE);

    try {
      inputStream.connect(outputStream);
    } catch (IOException e) {
      logger.error(" Init input stream error", e);
      return;
    }

    executor.setExitValue(0);
    executor.setStreamHandler(new PumpStreamHandler(outputStream, outputStream));

    ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
    executor.setWatchdog(watchdog);

    DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

    try {
      executor.execute(cmdLine, resultHandler);

      while (!resultHandler.hasResult()) {
        resultHandler.waitFor(TIME_OUT);

        readInputStream(inputStream);
      }

      readInputStream(inputStream);
    } catch (IOException e) {
      logger.error(" Execute task failed", e);
    } catch (InterruptedException e) {
      logger.error(" Execute task failed", e);
    }

    exitCode = resultHandler.getExitValue();
    complete = true;

    logger.info(" End of run, exitCode: {}", exitCode);
  }

  public static void main(String[] args) throws IOException {
    PropertiesConfiguration props = new PropertiesConfiguration();
    props.addProperty(AbstractProcessJob.PROXY_USER, "hadoop");
    props.addProperty(AbstractProcessJob.WORKING_DIR, "/opt/hadoop");
    props.addProperty(AbstractProcessJob.JOB_PARAMS, "{\"script\":\"ls -l\"}");
    props.addProperty("timeout", 100);
    Logger logger = LoggerFactory.getLogger("shellJob");
    ShellJob job = new ShellJob("NODE_1_2017", props, logger);
    job.exec();
    System.out.println("run finished!");
  }
}
