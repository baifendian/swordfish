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
package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.common.job.struct.ResourceInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spark 节点的参数 <p>
 */
public class SparkParam extends BaseParam {

  private ResourceInfo mainJar;

  private String mainClass;

  private String deployMode;

  private String args;

  private int driverCores;

  private String driverMemory;

  private int numExecutors;

  private int executorCores;

  private String executorMemory;

  private List<Property> properties = new ArrayList<>();

  private List<ResourceInfo> libJars = new ArrayList<>();

  private List<ResourceInfo> files = new ArrayList<>();

  private List<ResourceInfo> archives = new ArrayList<>();

  private String queue;

  @Override
  public boolean checkValid() {
    return mainJar != null && StringUtils.isNotEmpty(mainClass);
  }

  public ResourceInfo getMainJar() {
    return mainJar;
  }

  public void setMainJar(ResourceInfo mainJar) {
    this.mainJar = mainJar;
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public String getDeployMode() {
    return deployMode;
  }

  public void setDeployMode(String deployMode) {
    this.deployMode = deployMode;
  }

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
  }

  public int getDriverCores() {
    return driverCores;
  }

  public void setDriverCores(int driverCores) {
    this.driverCores = driverCores;
  }

  public String getDriverMemory() {
    return driverMemory;
  }

  public void setDriverMemory(String driverMemory) {
    this.driverMemory = driverMemory;
  }

  public int getNumExecutors() {
    return numExecutors;
  }

  public void setNumExecutors(int numExecutors) {
    this.numExecutors = numExecutors;
  }

  public int getExecutorCores() {
    return executorCores;
  }

  public void setExecutorCores(int executorCores) {
    this.executorCores = executorCores;
  }

  public String getExecutorMemory() {
    return executorMemory;
  }

  public void setExecutorMemory(String executorMemory) {
    this.executorMemory = executorMemory;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  public List<ResourceInfo> getLibJars() {
    return libJars;
  }

  public void setJars(List<ResourceInfo> libJjars) {
    this.libJars = libJars;
  }

  public List<ResourceInfo> getFiles() {
    return files;
  }

  public void setFiles(List<ResourceInfo> files) {
    this.files = files;
  }

  public List<ResourceInfo> getArchives() {
    return archives;
  }

  public void setArchives(List<ResourceInfo> archives) {
    this.archives = archives;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  @Override
  public List<String> getResourceFiles() {
    List<String> resFiles = new ArrayList<>();
    if (mainJar.isProjectScope()) {
      resFiles.add(mainJar.getRes());
    }
    if (libJars != null && !libJars.isEmpty())
      resFiles.addAll(libJars.stream().filter(p -> p.isProjectScope())
              .map(p -> p.getRes()).collect(Collectors.toList()));
    if (files != null && !files.isEmpty())
      resFiles.addAll(files.stream().filter(p -> p.isProjectScope())
              .map(p -> p.getRes()).collect(Collectors.toList()));
    if (archives != null && !archives.isEmpty())
      resFiles.addAll(archives.stream().filter(p -> p.isProjectScope())
              .map(p -> p.getRes()).collect(Collectors.toList()));
    return resFiles;
  }
}
