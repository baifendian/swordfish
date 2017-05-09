package com.baifendian.swordfish.common.job.struct.datasource.conn;

import com.baifendian.swordfish.common.job.struct.datasource.OracleParam;
import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * oracle数据源尝试连接类
 */
public class OracleTryConn extends TryConn<OracleParam> {

  private static Logger logger = LoggerFactory.getLogger(OracleTryConn.class.getName());

  public OracleTryConn(OracleParam param) {
    super(param);
  }

  @Override
  public void isConnectable() throws Exception {
    Connection con = null;
    try {
      OracleDataSource ods = new OracleDataSource();

      ods.setDriverType(param.getDriverType());
      ods.setServerName(param.getServerName());
      ods.setNetworkProtocol(param.getNetworkProtocol());
      ods.setDataSourceName(param.getDatabaseName());
      ods.setPortNumber(param.getPortNumber());
      ods.setUser(param.getUser());
      ods.setPassword(param.getPassword());

      con = ods.getConnection();
    } finally {
      if (con!=null){
        try {
          con.close();
        } catch (SQLException e) {
          logger.error("Oracle datasource try conn close conn error",e);
          throw e;
        }
      }
    }
  }
}
