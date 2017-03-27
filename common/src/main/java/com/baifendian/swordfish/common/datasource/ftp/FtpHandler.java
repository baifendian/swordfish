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
package com.baifendian.swordfish.common.datasource.ftp;

import com.baifendian.swordfish.common.datasource.DataSourceHandler;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FtpHandler implements DataSourceHandler {

  private static final Logger logger = LoggerFactory.getLogger(FtpHandler.class);
  private FtpParam param;

  public FtpHandler(DbType dbType, String paramStr){
    param = JsonUtil.parseObject(paramStr, FtpParam.class);
  }

  public void isConnectable() throws IOException {
    FTPClient ftpClient = new FTPClient();
    ftpClient.connect(param.getHost(), param.getPort());
    ftpClient.login(param.getUser(), param.getPassword());
  }
}
