package com.baifendian.swordfish.common.job.struct.node.impexp;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

/**
 * 写配置工厂
 */
public class WriterFactory {

  public static Writer getWriter(ImpExpType type, String writer) {
    switch (type) {
      case MYSQLTOHIVE:
        return JsonUtil.parseObject(writer, HiveWriter.class);
      default:
        return null;
    }
  }
}
