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
package com.baifendian.swordfish.utils;

import com.baifendian.swordfish.dao.mapper.utils.EqualUtils;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import org.apache.commons.lang.StringUtils;

/**
 * 单元测试工具类
 */
public class TestUtils {
  /**
   * 检测两个projectFlow对象是否完全一致
   * @param p1
   * @param p2
   * @return
   */
  public static boolean projectFlowEquals(ProjectFlow p1, ProjectFlow p2){
    if (p1 == null && p2 != null){
      return false;
    }
    if (p1 != null && p1 == null){
      return false;
    }
    if (p1 == null && p2 == null){
      return true;
    }

    return StringUtils.equals(p1.getName(),p2.getName()) &&
            StringUtils.equals(p1.getDesc(),p2.getDesc()) &&
            StringUtils.equals(p1.getProjectName(),p2.getProjectName()) &&
            StringUtils.equals(p1.getOwner(),p2.getOwner()) &&
            p1.getOwnerId() == p2.getOwnerId() &&
            p1.getProjectId() == p2.getProjectId() &&
            StringUtils.equals(p1.getProxyUser(),p2.getProxyUser()) &&
            StringUtils.equals(p1.getUserDefinedParams(),p2.getUserDefinedParams()) &&
            StringUtils.equals(p1.getExtras(),p2.getExtras()) &&
            StringUtils.equals(p1.getQueue(),p2.getQueue()) &&
            EqualUtils.equalLists(p1.getFlowsNodes(), p2.getFlowsNodes());
  }

  public boolean flowNodeEquals(FlowNode f1,FlowNode f2){
    if (f1 == null && f2 != null){
      return false;
    }

    if (f1 != null && f2 == null){
      return false;
    }

    if (f1 == null && f2 == null){
      return true;
    }

    return StringUtils.equals(f1.getName(),f1.getName()) &&
            StringUtils.equals(f1.getDesc(),f2.getDesc()) &&
            StringUtils.equals(f1.getDep(),f2.getDep()) &&
            StringUtils.equals(f1.getExtras(),f2.getExtras()) &&
            StringUtils.equals(f1.getParameter(),f2.getParameter()) &&
            StringUtils.equals(f1.getType(),f2.getType());
  }
}
