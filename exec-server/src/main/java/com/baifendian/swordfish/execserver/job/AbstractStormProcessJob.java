package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import org.slf4j.Logger;

/**
 * Created by caojingwei on 2017/7/17.
 */
public abstract class AbstractStormProcessJob extends AbstractStormJob {

  private ProcessJob processJob;

  public AbstractStormProcessJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
    processJob = new ProcessJob(this::logProcess, this::isCompleted, isLongJob,
            props.getWorkDir(), props.getJobAppId(),
            props.getProxyUser(), props.getEnvFile(), props.getExecJobStartTime(),
            props.getExecJobTimeout(), logger);
  }

  @Override
  public void process() throws Exception {
    try {
      // 构造进程
      exitCode = processJob.runCommand(createCommand());
    } catch (Exception e) {
      logger.error("Storm process exception", e);
      exitCode = -1;
    }
  }

  @Override
  public void cancel(boolean cancelApplication) throws Exception {
    cancel = true;

    // 关闭进程
    processJob.cancel();

    super.cancel(cancelApplication);
  }

  @Override
  public BaseParam getParam() {
    return null;
  }

  /**
   * 具体运行的命令
   */
  protected abstract String createCommand() throws Exception;
}
