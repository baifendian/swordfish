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
package com.baifendian.swordfish.masterserver.master;

public class ExecFlowInfo {

  private String host;
  private int port;
  private int execId;

  public ExecFlowInfo() {
  }

  public ExecFlowInfo(int execId) {
    this.execId = execId;
  }

  public ExecFlowInfo(String host, int port, int execId) {
    this.host = host;
    this.port = port;
    this.execId = execId;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
  }

  @Override
  public String toString() {
    return "ExecFlowInfo{" +
        "host='" + host + '\'' +
        ", port=" + port +
        ", execId=" + execId +
        '}';
  }
}
