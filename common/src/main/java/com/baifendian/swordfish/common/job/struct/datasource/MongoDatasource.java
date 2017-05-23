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
package com.baifendian.swordfish.common.job.struct.datasource;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDatasource extends Datasource {

  private static Logger logger = LoggerFactory.getLogger(MongoDatasource.class.getName());

  private String address;

  private String database;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  @Override
  public void isConnectable() throws Exception {
    MongoClient mongoClient = new MongoClient(new MongoClientURI(this.address));
    try {
      MongoClientOptions options = MongoClientOptions.builder().connectTimeout(10)
              .socketKeepAlive(false).build();

      MongoDatabase db = mongoClient.getDatabase(this.database);
      for (Document doc : db.listCollections()) {
        logger.debug("{}", doc);
      }
    } finally {
      mongoClient.close();
    }
  }
}
