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
package com.baifendian.swordfish.execserver.job.upload;

import com.baifendian.swordfish.execserver.job.AbstractProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * 导入任务接口
 */
abstract class UploadJob extends AbstractProcessJob {

  private final String DATAXFILENAME = "dataXJson";

  public UploadJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  /**
   * 生成datax需要的json文件
   *
   * @return
   */
  abstract String getDataXJson();

  /**
   * 生成datax 文件
   *
   * @return
   */
  public File createDataXParam(String dataXJson) {
    // 工作目录
    String fileName = DATAXFILENAME + UUID.randomUUID() + ".json";
    String path = MessageFormat.format("{0}/{1}", getWorkingDirectory(), fileName);
    File file = new File(path);
    try {
      FileUtils.writeStringToFile(file, dataXJson, Charset.forName("utf-8"));
    } catch (IOException e) {
      logger.error("Create dataX json file error", e);
      return null;
    }
    return file;
  }
}
