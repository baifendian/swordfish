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

import com.baifendian.swordfish.common.hadoop.YarnRestClient;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

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
    // 检测没有完成的任务
    List<StreamingResult> streamingResults = streamingDao.findNoFinishedJob();

    Date now = new Date();

    // 如果有没有完成的任务
    if (CollectionUtils.isNotEmpty(streamingResults)) {
      // 遍历流任务列表
      for (StreamingResult streamingResult : streamingResults) {
        // 得到 app id 列表
        List<String> appIds = streamingResult.getAppLinkList();
        // 列表为空, 就不管了
        if (CollectionUtils.isNotEmpty(appIds)) {
          FlowStatus status = FlowStatus.SUCCESS;

          // 可能有好多个子任务, 都完成算真的完成, 有一个失败, 算失败
          for (String appId : appIds) {
            try {
              FlowStatus tmpStatus = YarnRestClient.getInstance().getApplicationStatus(appId);

              // 任务不存在
              if (tmpStatus == null) {
                logger.error("application not exist: {}", streamingResult.getId());
                status = FlowStatus.KILL;
                break;
              } else if (!tmpStatus.typeIsSuccess()) {// 如果没有完成
                status = tmpStatus;
                break;
              }
            } catch (Exception e) {
              logger.error(String.format("get application exception: {}", streamingResult.getId()), e);
            }
          }

          // 更新状态
          if (status != streamingResult.getStatus()) {
            // 设置状态和结束时间
            streamingResult.setStatus(status);

            if (status.typeIsFinished()) {
              streamingResult.setEndTime(now);
            }

            streamingDao.updateResult(streamingResult);
          }
        } else if (System.currentTimeMillis() - streamingResult.getScheduleTime().getTime() >=
            MasterConfig.streamingTimeoutThreshold * 1000) { // 提交很久了, 没有任何执行和接受
          // 设置状态和结束时间
          streamingResult.setStatus(FlowStatus.KILL);
          streamingResult.setEndTime(now);

          streamingDao.updateResult(streamingResult);
        }
      }
    }
  }
}