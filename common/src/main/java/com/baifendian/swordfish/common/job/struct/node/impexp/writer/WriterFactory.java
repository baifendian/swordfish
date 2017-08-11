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
package com.baifendian.swordfish.common.job.struct.node.impexp.writer;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.common.enums.WriterType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.apache.avro.data.Json;

/**
 * 写配置工厂
 */
public class WriterFactory {

  public static Writer getWriter(WriterType type, String writer) {
    switch (type) {
      case HDFS:
        return JsonUtil.parseObject(writer, HdfsWriter.class);
      case HIVE:
        return JsonUtil.parseObject(writer, HiveWriter.class);
      case MYSQL:
        return JsonUtil.parseObject(writer, MysqlWriter.class);
      case MONGO:
        return JsonUtil.parseObject(writer, MongoWriter.class);
      default:
        return null;
    }
  }

  public static Writer getWriter(ImpExpType type, String writer) {
    return getWriter(type.getWriter(), writer);
  }
}
