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
package com.baifendian.swordfish.common.job.struct.node;

import com.baifendian.swordfish.common.job.struct.resource.ResourceInfo;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;

/**
 * 参数基类(需要校验参数和获取资源的子类需要 @Override 对应的方法)
 */
public abstract class BaseParam {

  /**
   * 校验参数是否合法 (需要校验的子类 @Override 该方法)
   *
   * @return 是否合法
   */
  public boolean checkValid() {
    return true;
  }

  /**
   * 获取 node 需要的项目级资源文件清单
   */
  public List<String> getProjectResourceFiles() {
    return null;
  }

  /**
   * @param resourceInfos
   * @param resFiles
   */
  public static void addProjectResourceFiles(List<ResourceInfo> resourceInfos, List<String> resFiles) {
    if (CollectionUtils.isNotEmpty(resourceInfos)) {
      resFiles.addAll(resourceInfos.stream().filter(p -> p.isProjectScope())
          .map(p -> p.getRes()).collect(Collectors.toList()));
    }
  }
}
