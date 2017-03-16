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
package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.BaseParam;

import java.util.List;
import java.util.Map;

/**
 * @author : liujin
 * @date : 2017-03-07 16:43
 */
public class ProcessParam extends BaseParam {

  private String script;

  private List<String> args;

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
  public List<String> getResourceFiles() {
    return null;
  }
}
