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
package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.StreamingJobMapper;
import com.baifendian.swordfish.dao.mapper.StreamingResultMapper;
import com.baifendian.swordfish.dao.model.StreamingResult;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StreamingDao extends BaseDao {
  @Autowired
  private StreamingJobMapper streamingJobMapper;

  @Autowired
  private StreamingResultMapper streamingResultMapper;

  @Override
  protected void init() {
    streamingJobMapper = ConnectionFactory.getSqlSession().getMapper(StreamingJobMapper.class);
    streamingResultMapper = ConnectionFactory.getSqlSession().getMapper(StreamingResultMapper.class);
  }


  /**
   * 获取 flow 执行详情 <p>
   *
   * @param execId
   * @return streaming 的执行信息
   * @see StreamingResult
   */
  public StreamingResult queryStreamingExec(int execId) {
    return streamingResultMapper.selectById(execId);
  }

  /**
   * 查找没有完成的流任务
   *
   * @return
   */
  public List<StreamingResult> findNoFinishedJob() {
    return streamingResultMapper.findNoFinishedJob();
  }

  /**
   * 更新流任务的状态
   *
   * @param result
   * @return
   */
  public int updateResult(StreamingResult result) {
    return streamingResultMapper.updateResult(result);
  }
}
