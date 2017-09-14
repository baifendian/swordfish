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
package com.baifendian.swordfish.common.job.struct.node.common;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import java.util.List;
import java.util.Map;

public class ProcessParam extends BaseParam {

  /**
   * 具体的脚本
   */
  private String script;

  /**
   * 脚本参数
   */
  private List<String> args;

  /**
   * 环境变量
   */
  private Map<String, String> envMap;

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public List<String> getArgs() {
    return args;
  }

  public void setArgs(List<String> args) {
    this.args = args;
  }

  public Map<String, String> getEnvMap() {
    return envMap;
  }

  public void setEnvMap(Map<String, String> envMap) {
    this.envMap = envMap;
  }

  @Override
  public List<String> getProjectResourceFiles() {
    return null;
  }
}
