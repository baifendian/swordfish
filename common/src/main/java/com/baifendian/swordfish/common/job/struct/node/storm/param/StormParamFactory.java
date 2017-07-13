package com.baifendian.swordfish.common.job.struct.node.storm.param;

import com.baifendian.swordfish.common.enums.StormType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

/**
 * Storm param 工厂
 */
public class StormParamFactory {
  public static IStormParam getStormParam(StormType type, String param) {
    switch (type) {
      case JAR:
        return JsonUtil.parseObject(param, StormJarParam.class);
      case SQL:
        return JsonUtil.parseObject(param, StormSqlParam.class);
      case SHELL:
        return JsonUtil.parseObject(param, StormSqlParam.class);
      default:
        return null;
    }
  }
}
