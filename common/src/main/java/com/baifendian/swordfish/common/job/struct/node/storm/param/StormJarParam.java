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
 * jar 方式提交任务参数
 */
public class StormJarParam implements IStormParam {
  private String mainClass;
  private ResourceInfo mainJar;
  private String args;
  private List<ResourceInfo> jars;
  private String artifacts;
  private String artifactRepositories;

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public ResourceInfo getMainJar() {
    return mainJar;
  }

  public void setMainJar(ResourceInfo mainJar) {
    this.mainJar = mainJar;
  }

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
  }

  public List<ResourceInfo> getJars() {
    return jars;
  }

  public void setJars(List<ResourceInfo> jars) {
    this.jars = jars;
  }

  public String getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(String artifacts) {
    this.artifacts = artifacts;
  }

  public String getArtifactRepositories() {
    return artifactRepositories;
  }

  public void setArtifactRepositories(String artifactRepositories) {
    this.artifactRepositories = artifactRepositories;
  }

  @Override
  public boolean checkValid() {
    return StringUtils.isNotEmpty(mainClass) &&
            mainJar != null;
  }

  @Override
  public List<String> getProjectResourceFiles() {
    List<String> resFiles = new ArrayList<>();
    if (mainJar.isProjectScope()) {
      resFiles.add(mainJar.getRes());
    }

    addProjectResourceFiles(jars, resFiles);
    return resFiles;
  }
}
