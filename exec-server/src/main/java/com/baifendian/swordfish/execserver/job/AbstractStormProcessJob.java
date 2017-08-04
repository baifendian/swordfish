package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.storm.StormRestUtil;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.StreamingResult;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * storm 框架
 */
public abstract class AbstractStormProcessJob extends Job {

  protected PropertiesConfiguration stormConf;

  /**
   * storm 任务名称
   */
  protected String topologyName;

  /**
   * 流任务数据库接口
   */
  private StreamingDao streamingDao;

  /**
   * storm 任务Id
   */
  protected String topologyId;

  protected List<String> topologyLogs;

  /**
   * storm kill wait time
   */
  protected static long waitTime = 60000;

  protected long checkInterval = 5000;

  protected static long submitWait = 60 * 10 * 1000;


  /**
   * 启动时间
   */
  private Long startTime;


  private ProcessJob processJob;

  public AbstractStormProcessJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
    processJob = new ProcessJob(this::logProcess, this::isCompleted, isLongJob,
        props.getWorkDir(), props.getJobAppId(),
        props.getProxyUser(), props.getEnvFile(), props.getExecJobStartTime(),
        props.getExecJobTimeout(), logger);
    startTime = System.currentTimeMillis();
  }

  @Override
  public void init() throws Exception {
    stormConf = new PropertiesConfiguration("common/storm.properties");
    streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);
  }

  @Override
  public void logProcess(List<String> logs) {
    super.logProcess(logs);
    for (String log : logs) {
      // 如果有提交完成的日志
      if (StringUtils.contains(log, "Finished submitting topology:")) {
        logger.info("Find finish log: {}", log);
        // 获取 name
        String[] logList = log.split(":");
        if (logList.length == 2) {
          topologyName = logList[1].trim();
          logger.info("Get topologyName: {}", topologyName);
        } else {
          logger.error("Get topologyName error");
        }
      }
    }
  }

  @Override
  public boolean isCompleted() {
    /*
    if (StringUtils.isNotEmpty(topologyId)) {
      try {
        TopologyInfoDto topologyInfo = StormRestUtil.getTopologyInfo(topologyId);
        if (topologyInfo == null || topologyInfo.getStatus() == null) {
          complete = false;
        } else {
          logger.info("current status is: {}", topologyInfo.getStatus());
          complete = true;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    */

    long now = System.currentTimeMillis();

    // 如果能获取到 topologName 我们就认为提交完成了
    if (StringUtils.isNotEmpty(topologyName) || now - startTime > submitWait) {
      complete = true;
    }

    return complete;
  }

  /**
   * 杀死一个任务
   */
  public static void cancelApplication(String appLinks) throws Exception {
    StormRestUtil.topologyKill(appLinks, waitTime);
  }

  /**
   * 恢复一个任务
   */
  public static void activateApplication(String appLinks) throws Exception {
    StormRestUtil.topologyActivate(appLinks);
  }

  /**
   * 暂停一个任务
   */
  public static void deactivateApplication(String appLinks) throws Exception {
    StormRestUtil.topologyDeactivate(appLinks);
  }

  @Override
  public void process() throws Exception {
    try {
      // 构造进程
      processJob.runCommand(createCommand());

      logger.info("Start get topologyId...");

      // 尝试 3 次获取 id，如果不能获取到 id 就算任务失败
      int index = 0;

      while (true) {
        ++index;

        try {
          topologyId = StormRestUtil.getTopologyId(topologyName);
          logger.info("Get topologyId: {}", topologyId);
        } catch (Exception e) {
          logger.error("Get topologyId error", e);
        }

        if (index > 3) {
          throw new Exception("Not found topologyId!");
        }

        if (StringUtils.isEmpty(topologyId)) {
          Thread.sleep(checkInterval);
        } else {
          break;
        }
      }

      logger.info("Finish get topologyId!");

      // 获取日志
      try {
        topologyLogs = StormRestUtil.getTopologyLogs(topologyId);
        logger.info("Get topology logs");
      } catch (Exception e) {
        logger.error("Get topology logs error", e);
      }

      logger.info("Start update streaming result dao...");

      StreamingResult streamingResult = streamingDao.queryStreamingExec(props.getExecId());

      if (streamingResult != null) {
        streamingResult.setAppLinkList(Arrays.asList(topologyId));
        streamingResult.setJobLinkList(topologyLogs);
        streamingDao.updateResult(streamingResult);
      } else {
        logger.warn("Not found execId: {}", props.getExecId());
      }

      logger.info("Finish update streaming result dao!");
      exitCode = 0;
    } catch (Exception e) {
      logger.error("Storm process exception", e);
      exitCode = -1;
    }
  }

  @Override
  public void after() throws Exception {
    super.after();

    if (exitCode != 0) {
      logger.info("Job failed update streaming result dao...");

      StreamingResult streamingResult = streamingDao.queryStreamingExec(props.getExecId());

      if (streamingResult != null) {
        Date now = new Date();

        streamingResult.setStatus(FlowStatus.FAILED);
        streamingResult.setEndTime(now);

        streamingDao.updateResult(streamingResult);
      } else {
        logger.warn("Not found execId: {}", props.getExecId());
      }

      logger.info("Finish job failed update streaming result dao!");
    }
  }

  @Override
  public void cancel(boolean cancelApplication) throws Exception {
    cancel = true;

    // 关闭进程
    processJob.cancel();

    complete = true;

    if (cancelApplication && StringUtils.isNotEmpty(topologyId)) {
      cancelApplication(topologyId);
    }
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
