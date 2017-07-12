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
package com.baifendian.swordfish.common.job.struct.node.storm.param;

import com.baifendian.swordfish.common.job.struct.resource.ResourceInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * storm param 接口
 */
public interface IStormParam {
  /**
   * 校验Storm param是否合法
   * @return
   */
  default boolean checkValid(){
    return true;
  }

  List<String> getProjectResourceFiles();

  default void addProjectResourceFiles(List<ResourceInfo> resourceInfos, List<String> resFiles) {
    if (CollectionUtils.isNotEmpty(resourceInfos)) {
      resFiles.addAll(resourceInfos.stream().filter(p -> p.isProjectScope())
              .map(p -> p.getRes()).collect(Collectors.toList()));
    }
  }
}
