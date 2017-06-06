package com.baifendian.swordfish.common.job.struct.node.impexp;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

/**
 * 读配置工厂
 */
public class ReaderFactory {
  public static Reader getReader(ImpExpType type, String reader) {
    switch (type) {
      case MYSQLTOHIVE:
      case MYSQLTOHDFS:
        return JsonUtil.parseObject(reader, MysqlReader.class);
      default:
        return null;
    }
  }
}
