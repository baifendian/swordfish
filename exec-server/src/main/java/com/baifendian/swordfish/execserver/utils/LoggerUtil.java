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

package com.baifendian.swordfish.execserver.utils;

import com.baifendian.swordfish.dao.enums.FlowType;

/**
 * 日志记录工具类 <p>
 *
 * @author : dsfan
 * @date : 2016年12月15日
 */
public class LoggerUtil {

  /**
   * 分隔符
   */
  public static final String SEPARATOR = "_";

  /**
   * 生成 jobId <p>
   *
   * @return jobId
   */
  public static String genJobId(FlowType flowType, long execId) {
    return genJobId(flowType, execId, null);
  }

  /**
   * 生成 jobId <p>
   *
   * @return jobId
   */
  public static String genJobId(FlowType flowType, long execId, Integer nodeId) {
    if (nodeId == null) {
      return flowType + SEPARATOR + execId;
    }
    return flowType + SEPARATOR + execId + SEPARATOR + nodeId;
  }

  public static void main(String[] args) {
    System.out.println(genJobId(FlowType.SHORT, 123));
    System.out.println(genJobId(FlowType.ADHOC, 123, 13));
  }
}
