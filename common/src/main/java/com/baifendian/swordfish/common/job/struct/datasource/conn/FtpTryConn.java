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
package com.baifendian.swordfish.common.job.struct.datasource.conn;


import com.baifendian.swordfish.common.job.struct.datasource.FtpParam;
import org.apache.commons.net.ftp.FTPClient;

/**
 * ftp 尝试连接工具类
 */
public class FtpTryConn extends TryConn<FtpParam> {

  public FtpTryConn(FtpParam param) {
    super(param);
  }

  @Override
  public void isConnectable() throws Exception {
    FTPClient ftpClient = new FTPClient();
    ftpClient.connect(param.getHost(), param.getPort());
    ftpClient.login(param.getUser(), param.getPassword());
  }
}
