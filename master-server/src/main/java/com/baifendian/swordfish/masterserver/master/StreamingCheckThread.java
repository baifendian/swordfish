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
package com.baifendian.swordfish.masterserver.master;

import static com.baifendian.swordfish.common.job.struct.node.JobType.SPARK_STREAMING;
import static com.baifendian.swordfish.common.job.struct.node.JobType.STORM;

import com.baifendian.swordfish.common.hadoop.YarnRestClient;
import com.baifendian.swordfish.common.mail.EmailManager;
import com.baifendian.swordfish.common.storm.StormRestUtil;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检测流任务, 对于完成的流任务, 更新其状态
 */
public class StreamingCheckThread implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(StreamingCheckThread.class);

  // 流数据库接口
  private StreamingDao streamingDao;

  public StreamingCheckThread(StreamingDao streamingDao) {
    this.streamingDao = streamingDao;
  }

  @Override
  public void run() {
    try {
      // 检测没有完成的任务
      List<StreamingResult> streamingResults = streamingDao.findNoFinishedJob();

      Date now = new Date();

      // 如果有没有完成的任务
      if (CollectionUtils.isNotEmpty(streamingResults)) {
        logger.info("find not finish jobs, size: {}", streamingResults.size());

        // 遍历流任务列表
        for (StreamingResult streamingResult : streamingResults) {
          List<String> appIds = streamingResult.getAppLinkList();

          FlowStatus status = streamingResult.getStatus();

          // 如果有 appid 根据不同调度平台处理
          if (StringUtils.equalsIgnoreCase(streamingResult.getType(), SPARK_STREAMING)) {
            // 如果更本没有 appid
            if (CollectionUtils.isEmpty(appIds)) {
              // 如果已经很久没有 appid 了就失败
              if (System.currentTimeMillis() - streamingResult.getScheduleTime().getTime() >=
                  MasterConfig.streamingTimeoutThreshold * 1000) { // 提交很久了, 没有任何执行和接受
                // 设置状态和结束时间
                status = FlowStatus.FAILED;
              } else {
                continue;
              }
            } else {
              // 可能有好多个子任务, 都完成算真的完成, 有一个失败, 算失败
              String appId = appIds.get(appIds.size() - 1);

              try {
                FlowStatus tmpStatus = YarnRestClient.getInstance().getApplicationStatus(appId);

                // 任务不存在
                if (tmpStatus == null) {
                  logger.error("application not exist: {}", appId);
                  status = FlowStatus.FAILED;
                } else {
                  status = tmpStatus;
                }
              } catch (Exception e) {
                logger.error(String.format("get application exception: %s", appId), e);
              }
            }
          } else if (StringUtils.equalsIgnoreCase(streamingResult.getType(), STORM)) {
            // 为空, 继续
            if (CollectionUtils.isEmpty(appIds)) {
              continue;
            }

            // storm 只有一个 appId
            String topologyId = appIds.get(0);

            try {
              FlowStatus tmpStatus = StormRestUtil.getTopologyStatus(topologyId);

              if (tmpStatus == null) {
                status = FlowStatus.FAILED;
                logger.error("Not found topology: {}", topologyId);
              } else {
                status = tmpStatus;
              }
            } catch (Exception e) {
              logger.error(String.format("get topology id exception: %s", topologyId), e);
            }
          } else {
            logger.error("No support type: {}", streamingResult.getType());
          }

          // 更新状态
          if (status != streamingResult.getStatus()) {
            // 设置状态和结束时间
            streamingResult.setStatus(status);

            if (status.typeIsFinished()) {
              streamingResult.setEndTime(now);
            }

            streamingDao.updateResult(streamingResult);

            // 发送报警
            if (status.typeIsFinished()) {
              EmailManager.sendMessageOfStreamingJob(streamingResult);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }
}
