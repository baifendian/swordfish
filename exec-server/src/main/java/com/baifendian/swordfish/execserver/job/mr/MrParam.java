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
package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.execserver.job.ResourceInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MR 节点的参数 <p>
 *
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class MrParam extends BaseParam {

  /**
   * 主jar包资源信息
   */
  private ResourceInfo mainJar;

  /**
   * 主程序
   */
  private String mainClass;

  /**
   * 参数信息
   */
  private String args;

  /**
   * 配置信息列表
   */
  private List<Property> properties = new ArrayList<>();

  /**
   * 额外的 jar 包，指的是本地的文件，这个可能很长
   */
  private List<ResourceInfo> libJars;

  /**
   * 额外的文件，指的是本地的文件，可能是多条配置
   */
  private List<ResourceInfo> files;

  /**
   * 额外的压缩文件，指的是本地的文件，可能是多条配置
   */
  private List<ResourceInfo> archives;

  /**
   * 执行队列
   */
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

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
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

  public void setLibJars(List<ResourceInfo> libJars) {
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

  public List<String> getDArgs() {
    if (properties.isEmpty()) {
      return new ArrayList<>();
    } else {
      return this.properties.stream().map(prop -> prop.getProp() + "=" + prop.getValue())
              .collect(Collectors.toList());
    }
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


