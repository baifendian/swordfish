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
package com.baifendian.swordfish.common.job.struct.node.impexp.reader;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.common.enums.ReaderType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.apache.avro.data.Json;

/**
 * 读配置工厂
 */
public class ReaderFactory {

  public static Reader getReader(ReaderType type, String reader) {
    switch (type) {
      case MYSQL:
        return JsonUtil.parseObject(reader, MysqlReader.class);
      case HIVE:
        return JsonUtil.parseObject(reader, HiveReader.class);
      case FILE:
        return JsonUtil.parseObject(reader, FileReader.class);
      case POSTGRE:
        return JsonUtil.parseObject(reader, PostgreReader.class);
      default:
        return null;
    }
  }

  public static Reader getReader(ImpExpType type, String reader) {
    return getReader(type.getReader(), reader);
  }
}
