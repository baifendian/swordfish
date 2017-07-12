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
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Storm shell 方式提交任务参数
 */
public class StormShellParam implements IStormParam {
  private ResourceInfo resources;
  private String command;

  public ResourceInfo getResources() {
    return resources;
  }

  public void setResources(ResourceInfo resources) {
    this.resources = resources;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  @Override
  public boolean checkValid() {
    return resources != null &&
            StringUtils.isNotEmpty(command);
  }

  @Override
  public List<String> getProjectResourceFiles() {
    List<String> resFiles = new ArrayList<>();
    if (resources.isProjectScope()) {
      resFiles.add(resources.getRes());
    }
    return resFiles;
  }
}
