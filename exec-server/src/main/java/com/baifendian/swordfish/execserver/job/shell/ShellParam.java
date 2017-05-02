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
package com.baifendian.swordfish.execserver.job.shell;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.struct.ResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ShellParam extends BaseParam {

  /**
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * 原始 shell 语句
   */
  private String script;

  private List<ResourceInfo> resources;

  @Override
  public boolean checkValid() {
    return script != null && !script.isEmpty();
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public List<ResourceInfo> getResources() {
    return resources;
  }

  public void setResources(List<ResourceInfo> resources) {
    this.resources = resources;
  }

  @Override
  public List<String> getResourceFiles() {
    if (resources != null) {
      return resources.stream().filter(p->p.isProjectScope())
              .map(p -> p.getRes()).collect(Collectors.toList());
    } else {
      return null;
    }
  }
}
