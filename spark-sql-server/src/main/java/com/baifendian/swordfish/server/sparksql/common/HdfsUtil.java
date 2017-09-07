package com.baifendian.swordfish.server.sparksql.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsUtil {

  private static Logger logger = LoggerFactory.getLogger(HdfsUtil.class);

  static private String tmpPath = "/tmp/swordfish/sparkSqlServer/";

  static {
    File file = new File(tmpPath);
    if (!file.exists() && !file.mkdirs()){
      throw new RuntimeException("Create path failed. name:"+tmpPath);
    }
  }

  static public String readInputStream(InputStream inputStream) {
    try {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }

      return result.toString();
    } catch (IOException e) {
      logger.info("Read failed.", e);
      e.printStackTrace();
    }

    return "";
  }

  public static String downloadHdfs(String jobId, String hdfsFile)
      throws IOException, InterruptedException {
    String tmpFileName = tmpPath + jobId + System.currentTimeMillis();
    String cmd = "hadoop fs -get " + hdfsFile + " " + tmpFileName;
    logger.info("Begin run command: {}", cmd);
    Runtime runtime = Runtime.getRuntime();
    Process process = runtime.exec(cmd);
    process.waitFor();

    logger.info(readInputStream(process.getInputStream()));
    logger.info(readInputStream(process.getErrorStream()));
    return tmpFileName;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    downloadHdfs("aa", "aa");
  }


}
