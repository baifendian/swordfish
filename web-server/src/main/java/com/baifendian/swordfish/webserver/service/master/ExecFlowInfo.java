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
package com.baifendian.swordfish.webserver.service.master;

public class ExecFlowInfo {
  private long execId;

  public ExecFlowInfo(){
  }

  public ExecFlowInfo(long execId){
    this.execId = execId;
  }

  public long getExecId() {
    return execId;
  }

  public ExecFlowInfo setExecId(long execId) {
    this.execId = execId;
    return this;
  }

  @Override
  public String toString(){
    return String.format("execId:%d", execId);
  }
}
