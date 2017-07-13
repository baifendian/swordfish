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

import java.util.ArrayList;
import java.util.List;

/**
 * storm sql 方式提交任务参数
 */
public class StormSqlParam implements IStormParam {

  private ResourceInfo sqlFile;
  private List<ResourceInfo> jars;
  private String artifacts;
  private String artifactRepositories;

  public ResourceInfo getSqlFile() {
    return sqlFile;
  }

  public void setSqlFile(ResourceInfo sqlFile) {
    this.sqlFile = sqlFile;
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
    return sqlFile != null;
  }

  @Override
  public List<String> getProjectResourceFiles() {
    List<String> resFiles = new ArrayList<>();
    if (sqlFile.isProjectScope()) {
      resFiles.add(sqlFile.getRes());
    }

    addProjectResourceFiles(jars, resFiles);
    return resFiles;
  }
}
