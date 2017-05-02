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
package com.baifendian.swordfish.common.datasource.mongodb;

import com.baifendian.swordfish.common.datasource.DataSourceHandler;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBHandler implements DataSourceHandler {

  private static final Logger logger = LoggerFactory.getLogger(MongoDBHandler.class);

  private MongoDBParam param;

  public MongoDBHandler(DbType dbType, String paramStr){
    param = JsonUtil.parseObject(paramStr, MongoDBParam.class);
  }

  public void isConnectable(){
    MongoClient mongoClient = new MongoClient(new MongoClientURI(param.getAddress()));
    try {
      MongoClientOptions  options = MongoClientOptions.builder().connectTimeout(10)
              .socketKeepAlive(false).build();

      MongoDatabase db = mongoClient.getDatabase(param.getDatabase());
      for (Document doc : db.listCollections()) {
        logger.debug("{}", doc);
      }
    } finally {
      mongoClient.close();
    }
  }
}
