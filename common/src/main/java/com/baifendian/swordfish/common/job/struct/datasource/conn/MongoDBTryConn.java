package com.baifendian.swordfish.common.job.struct.datasource.conn;

import com.baifendian.swordfish.common.job.struct.datasource.MongoDBParam;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mongo数据源连接测试
 */
public class MongoDBTryConn extends TryConn<MongoDBParam> {

  private static Logger logger = LoggerFactory.getLogger(MongoDBTryConn.class.getName());

  public MongoDBTryConn(MongoDBParam param) {
    super(param);
  }

  @Override
  public void isConnectable() throws Exception {
    MongoClient mongoClient = new MongoClient(new MongoClientURI(param.getAddress()));
    try {
      MongoClientOptions options = MongoClientOptions.builder().connectTimeout(10)
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
