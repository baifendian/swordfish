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
package com.baifendian.swordfish.webserver.service;

import com.baifendian.swordfish.common.search.EsSearch;
import com.baifendian.swordfish.webserver.dto.LogResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LogHelper {

  private static Logger logger = LoggerFactory.getLogger(LogHelper.class.getName());

  private EsSearch search = EsSearch.getInstance();

  public LogResult getLog(Integer from, Integer size, String query, String jobId) {
    // 日志 id 为空, 返回空的日志信息
    if (StringUtils.isEmpty(jobId)) {
      return LogResult.EMPTY_LOG_RESULT;
    }

    if (from == null) {
      from = 0;
    }

    if (size == null) {
      size = 100;
    }

    long start = System.currentTimeMillis();

    LogResult result = new LogResult();

    try {
      SearchResponse response = search.search(from, size, query, jobId/*, (sort == null) ? true : sort*/);

      if (response != null) {
        if (response.status() == RestStatus.OK) {
          SearchHits searchHits = response.getHits();

          result.setTotal(searchHits.getTotalHits()); // total
          result.setLength(searchHits.getHits().length); // real length
          result.setOffset(from); // offset

          List<String> contents = new ArrayList<>();

          for (SearchHit hit : searchHits.getHits()) {
            Map<String, Object> fieldMap = hit.getSource();

            if (fieldMap != null && fieldMap.containsKey("nest_msg")) {
              String message = fieldMap.get("nest_msg").toString();

              contents.add(message);
            } else {
              contents.add(StringUtils.EMPTY);
            }
          }

          result.setContent(contents);
        } else {
          logger.error("search status: {}", response.status());
        }
      }
    } catch (IOException e) {
      logger.error("Catch an exception", e);
      return null;
    }

    // 设置耗费的时间
    result.setTook(System.currentTimeMillis() - start);
    return result;
  }
}
