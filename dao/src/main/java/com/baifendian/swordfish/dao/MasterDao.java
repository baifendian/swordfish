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
package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.model.MasterServer;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class MasterDao extends BaseDao {

  @Autowired
  MasterServerMapper masterServerMapper;

  @Override
  protected void init() {
    masterServerMapper = ConnectionFactory.getSqlSession().getMapper(MasterServerMapper.class);
  }

  public MasterServer getMasterServer() {
    return masterServerMapper.query();
  }

  public int registerMasterServer(String host, int port) {
    MasterServer masterServer = new MasterServer();
    masterServer.setHost(host);
    masterServer.setPort(port);
    masterServer.setCreateTime(new Date());
    masterServer.setModifyTime(new Date());
    return masterServerMapper.insert(masterServer);
  }

  public int updateMasterServer(String host, int port) {
    MasterServer masterServer = new MasterServer();
    masterServer.setHost(host);
    masterServer.setPort(port);
    masterServer.setModifyTime(new Date());
    return masterServerMapper.update(masterServer);
  }
}
