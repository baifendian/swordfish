package com.baifendian.swordfish.common.job.struct.node.impexp;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

/**
 * Created by caojingwei on 2017/6/6.
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
