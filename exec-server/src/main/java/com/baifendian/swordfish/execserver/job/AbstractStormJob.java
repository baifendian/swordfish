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

import com.baifendian.swordfish.common.job.struct.node.storm.dto.TopologyInfoDto;
import com.baifendian.swordfish.common.job.struct.node.storm.dto.TopologyOperationDto;
import com.baifendian.swordfish.common.job.utils.node.storm.StormRestUtil;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.model.StreamingResult;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;


/**
 * storm 框架任务
 */
public abstract class AbstractStormJob extends Job {

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

  protected static long waitTime = 18000;

  public AbstractStormJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  @Override
  public void init() throws Exception {
    super.init();
    stormConf = new PropertiesConfiguration("common/storm.properties");
    streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);
  }

  @Override
  public void logProcess(List<String> logs) {
    super.logProcess(logs);
    for (String log : logs) {
      //如果有提交完成的日志
      if (StringUtils.contains(log, "Finished submitting topology:")) {
        logger.info("Find finish log: {}", log);
        //获取name
        String[] logList = log.split(":");
        if (logList.length == 2) {
          topologyName = logList[1];
          logger.info("Get topologyName: {}", topologyName);
        } else {
          logger.error("Get topologyName error");
          continue;
        }
        //获取id
        try {
          topologyId = StormRestUtil.getTopologyId(topologyName);
          logger.info("Get topologyId: {}", topologyId);
        } catch (IOException e) {
          logger.error("Get topologyId error", e);
          continue;
        }
        //获取日志
        try {
          topologyLogs = StormRestUtil.getTopologyLogs(topologyId);
          logger.info("Get topology logs");
        } catch (Exception e) {
          logger.error("Get topology logs error", e);
          continue;
        }
        logger.info("Start update streaming_result dao...");
        StreamingResult streamingResult = streamingDao.queryStreamingExec(props.getExecId());
        if (streamingResult != null) {
          streamingResult.setAppLinks(topologyId);
          streamingResult.setJobLinkList(topologyLogs);
          streamingDao.updateResult(streamingResult);
        } else {
          logger.warn("Not found execId: {}", props.getExecId());
        }
        logger.info("Finish update streaming_result dao!");
      }
    }
  }


  @Override
  public boolean isCompleted() {
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

    return complete;
  }


  @Override
  public void cancel(boolean cancelApplication) throws Exception {
    super.cancel(cancelApplication);
    complete = true;
    if (cancelApplication && StringUtils.isNotEmpty(topologyId)) {
      cancelApplication(topologyId);
    }
  }

  /**
   * 杀死一个任务
   *
   * @param appLinks
   * @throws Exception
   */
  public static void cancelApplication(String appLinks) throws Exception {
    StormRestUtil.topologyKill(appLinks, waitTime);
  }

  /**
   * 恢复一个任务
   * @param appLinks
   * @throws Exception
   */
  public static void activateApplication(String appLinks) throws Exception {
    StormRestUtil.topologyActivate(appLinks);
  }

  /**
   * 暂停一个任务
   * @param appLinks
   * @throws Exception
   */
  public static void dedeactivate(String appLinks) throws Exception {
    StormRestUtil.topologyActivate(appLinks);
  }

}
