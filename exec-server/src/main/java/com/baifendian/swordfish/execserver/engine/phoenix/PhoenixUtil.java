package com.baifendian.swordfish.execserver.engine.phoenix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.phoenix.queryserver.client.ThinClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class PhoenixUtil {

  private static final Logger logger = LoggerFactory.getLogger(PhoenixUtil.class);

  /**
   * phoenix Host
   */
  static private String phoenixHost;
  /**
   * phoenix port
   */
  static private int port;

  static private Properties phoenixPro = new Properties();

  static {
    try {
      Class.forName("org.apache.phoenix.queryserver.client.Driver");
      //Class.forName("org.apache.phoenix.queryserver.client");
    } catch (ClassNotFoundException e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    phoenixPro.put("phoenix.trace.frequency", "always");
    phoenixPro.put("phoenix.functions.allowUserDefinedFunctions", "true");
    phoenixPro.put("phoenix.annotation.myannotation", "abc");
    phoenixPro.put("user", "shuanghu");

    try {
      Properties properties = new Properties();

      File dataSourceFile = ResourceUtils.getFile("classpath:common/phoenix.properties");
      InputStream is = new FileInputStream(dataSourceFile);
      properties.load(is);

      phoenixHost = properties.getProperty("phoenix.host");
      port = Integer.parseInt(properties.getProperty("phoenix.port"));
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public static Connection getPhoenixConnection() {
    try {
      return DriverManager
          .getConnection(ThinClientUtil.getConnectionUrl( phoenixHost, port), "u_bloxy", "");
    } catch (RuntimeException e) {
      if (e.getCause().getClass().getName().equals("java.net.ConnectException")) {
        throw new RuntimeException("Failed to connect phoenix");
      }
      logger.error("url:" + ThinClientUtil.getConnectionUrl(phoenixHost, port), e);
    } catch (SQLException e) {
      logger.error(e.getMessage(), e);
    }

    return null;
  }
}
