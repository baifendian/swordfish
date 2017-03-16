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
package com.baifendian.swordfish.common.hadoop;

import com.baifendian.swordfish.common.job.exception.ExecException;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileStatus;

import java.io.IOException;

/**
 * @author : liujin
 * @date : 2017-03-03 16:59
 */
public class HdfsUtil {

  /**
   * 将hdfs上的文件或目录下载到本地
   *
   * @param src hdfs目录
   * @param dst 本地目录
   */
  public static void GetFile(String src, String dst) throws IOException, InterruptedException, ExecException {

    String cmd = "";
    HdfsClient hdfsClient = HdfsClient.getInstance();
    FileStatus fileStatus = hdfsClient.getFileStatus(src);
    if (fileStatus.isDirectory()) {
      FileStatus[] fileStatuses = hdfsClient.listFileStatus(src);
      if (fileStatuses.length > 0) {
        cmd = String.format("hdfs dfs -get %s/* %s", src, dst);
      }
    } else {
      cmd = String.format("hdfs dfs -get %s %s", src, dst);
    }
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("sh", "-c", cmd);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    int ret = process.waitFor();
    if (ret != 0) {
      String msg = String.format("call cmd %s error, %s", cmd, IOUtils.toString(process.getInputStream(), "UTF-8"));
      throw new ExecException(msg);
    }
  }
}
