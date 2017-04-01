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
package com.baifendian.swordfish.webserver.api.dto;

import com.baifendian.swordfish.webserver.api.dto.enums.MRScope;
import java.util.List;

/**
 * 类型检测dto
 */
public class NodeParamMR {
  private String mainClass;
  private MainJar mainJar;
  private String args;
  private List<Prop> properties;
  private List<File> files;
  private List<File> archives;
  private List<File> libJars;

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public MainJar getMainJar() {
    return mainJar;
  }

  public void setMainJar(MainJar mainJar) {
    this.mainJar = mainJar;
  }

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
  }

  public List<Prop> getProperties() {
    return properties;
  }

  public void setProperties(List<Prop> properties) {
    this.properties = properties;
  }

  public List<File> getFiles() {
    return files;
  }

  public void setFiles(List<File> files) {
    this.files = files;
  }

  public List<File> getArchives() {
    return archives;
  }

  public void setArchives(List<File> archives) {
    this.archives = archives;
  }

  public List<File> getLibJars() {
    return libJars;
  }

  public void setLibJars(List<File> libJars) {
    this.libJars = libJars;
  }

  public NodeParamMR() {
  }

  public static class MainJar{
    private MRScope scope;
    private String res;

    public MainJar() {
    }

    public MRScope getScope() {
      return scope;
    }

    public void setScope(MRScope scope) {
      this.scope = scope;
    }

    public String getRes() {
      return res;
    }

    public void setRes(String res) {
      this.res = res;
    }
  }

  public static class File{
    private MRScope scope;
    private String res;
    private String alias;

    public File() {
    }

    public MRScope getScope() {
      return scope;
    }

    public void setScope(MRScope scope) {
      this.scope = scope;
    }

    public String getRes() {
      return res;
    }

    public void setRes(String res) {
      this.res = res;
    }

    public String getAlias() {
      return alias;
    }

    public void setAlias(String alias) {
      this.alias = alias;
    }
  }

  public static class Prop{
    private String prop;
    private String value;

    public Prop() {
    }

    public String getProp() {
      return prop;
    }

    public void setProp(String prop) {
      this.prop = prop;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

}


