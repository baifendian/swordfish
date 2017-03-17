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
package com.baifendian.swordfish.execserver.parameter;

import com.baifendian.swordfish.common.utils.TimePlaceholderUtil;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.FlowParamMapper;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.FlowParam;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * 自定义参数管理 <p>
 *
 * @author : liujin
 * @date : 2017年3月1日
 */
public class CustomParamManager {

  /**
   * LOGGER
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomParamManager.class);

  /**
   * {@link ProjectFlowMapper}
   */
  private static final ProjectFlowMapper projectFlowMapper = ConnectionFactory.getSqlSession().getMapper(ProjectFlowMapper.class);

  /**
   * 构建自定义参数值
   *
   * <p>
   *
   * @return 自定义参数
   */
  public static Map<String, String> buildCustomParam(ExecutionFlow executionFlow, String cycTimeStr) {
    // 获取工作流的参数
    ProjectFlow projectFlow = projectFlowMapper.findById(executionFlow.getFlowId());
    Map<String, String> valueMap = new HashMap<>();
    Date cycTime = executionFlow.getStartTime();

    if (StringUtils.isNotEmpty(cycTimeStr)) {
      try {
        cycTime = DateUtils.parseDate(cycTimeStr, new String[]{SystemParamManager.TIME_FORMAT});
      } catch (ParseException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    // 遍历 flow 的参数
    Map<String, String> userDefinedParamMap = projectFlow.getUserDefinedParamMap();
    if (userDefinedParamMap != null) {
      for (Map.Entry<String, String> entry : userDefinedParamMap.entrySet()) {
        String value = entry.getValue();

        // 基于系统参数 ${dw.system.cyctime} 的自定义变量
        if (StringUtils.isNotEmpty(value) && value.startsWith(TimePlaceholderUtil.PLACEHOLDER_PREFIX) && value.endsWith(TimePlaceholderUtil.PLACEHOLDER_SUFFIX)) {
          value = TimePlaceholderUtil.resolvePlaceholders(value, cycTime, true);
        }

        valueMap.put(entry.getKey(), value);
      }
    }

    return valueMap;
  }

  /**
   * 构建自定义参数值 <p>
   *
   * @return 自定义参数
   */
  public static Map<String, String> buildCustomParam(ExecutionFlow executionFlow, String cycTimeStr, Map<String, String> envMap) {
    Map<String, String> valueMap = new HashMap<>();

    Date cycTime = executionFlow.getStartTime();
    if (StringUtils.isNotEmpty(cycTimeStr)) {
      try {
        cycTime = DateUtils.parseDate(cycTimeStr, new String[]{SystemParamManager.TIME_FORMAT});
      } catch (ParseException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    Set<String> keySet = envMap.keySet();
    for (String key : keySet) {
      String value = envMap.get(key);
      // 基于系统参数 ${dw.system.cyctime} 的自定义变量
      if (StringUtils.isNotEmpty(value) && value.startsWith(TimePlaceholderUtil.PLACEHOLDER_PREFIX) && value.endsWith(TimePlaceholderUtil.PLACEHOLDER_SUFFIX)) {
        value = TimePlaceholderUtil.resolvePlaceholders(value, cycTime, true);
      }

      valueMap.put(key, value);
    }

    return valueMap;
  }

}
