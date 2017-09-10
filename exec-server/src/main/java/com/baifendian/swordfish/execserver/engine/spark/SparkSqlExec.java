package com.baifendian.swordfish.execserver.engine.spark;

import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import com.baifendian.swordfish.common.job.struct.resource.ResourceInfo;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkSqlExec {
  /**
   * 记录日志的实例
   */
  private Logger logger;

  public SparkSqlExec(Logger logger){
    this.logger = logger;
  }

  /**
   * 执行多个 sql 语句 并返回查询的语句, 注意, 多次调用 execute, 上下文是不相关的
   *  @param createFuncs 创建自定义函数语句
   * @param sqls 执行的 sql
   * @param resultCallback 回调, 执行的结果处理
   * @param queryLimit 结果限制
   * @param remainTime 剩余运行时间, 暂没实现
   */
  public boolean execute(String jobId, List<UdfsInfo> createFuncs, List<String> sqls, boolean isContinue,
      ResultCallback resultCallback, Integer queryLimit, int remainTime) throws SQLException {
    // 没有剩余运行的时间
    if (remainTime <= 0) {
      return false;
    }

    List<UdfInfo> rpcUdfList = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(createFuncs)){
      for (UdfsInfo udf: createFuncs){
        UdfInfo udfInfo = new UdfInfo();
        udfInfo.setClassName(udf.getClassName());
        udfInfo.setFunc(udf.getFunc());
        List<String> paths = new ArrayList<>();
        for (ResourceInfo resourceInfo: udf.getLibJars()){
          paths.add(resourceInfo.getRes());
        }
        udfInfo.setLibJars(paths);
        rpcUdfList.add(udfInfo);
      }
    }
    if (resultCallback == null) {
      SparkSqlClient.getInstance().execEtl(jobId, rpcUdfList, sqls, remainTime, logger);
    }else {
      SparkSqlClient.getInstance()
          .executeAdhoc(jobId, rpcUdfList, sqls, resultCallback, queryLimit, remainTime, logger);
    }

    return true;
  }
}
