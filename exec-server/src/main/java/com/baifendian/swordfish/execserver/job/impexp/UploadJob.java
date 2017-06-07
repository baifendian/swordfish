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
package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;

import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.AbstractProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import org.apache.commons.io.FileUtils;

import static com.baifendian.swordfish.common.job.struct.node.JobType.*;

import org.json.JSONException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * 导入任务接口
 */
abstract class UploadJob extends AbstractProcessJob {

  protected final String DATAXFILENAME = "dataXJson";
  protected final String DATAXJSON = "{\"job\":{\"content\":[{\"reader\":{0},\"writer\":{1}}],\"setting\":{2}}}";

  protected ImpExpParam impExpParam;

  public UploadJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  @Override
  public void initJob() {
    this.impExpParam = (ImpExpParam) BaseParamFactory.getBaseParam(IMPORT, props.getJobParams());
  }

  /**
   * 生成datax需要的json文件
   *
   * @return
   */
  public String getDataXJson() throws Exception {
    return MessageFormat.format(getDataXReader(), getDateXWriter(), JsonUtil.toJsonString(impExpParam.getSetting()));
  }

  /**
   * 获取dataX的reader
   * @return
   */
  abstract String getDataXReader() throws JSONException, NoSuchFieldException, Exception;

  /**
   * 获取dataX的writer
   * @return
   */
  abstract String getDateXWriter();
  /**
   * 生成datax 文件
   *
   * @return
   */
  public File createDataXParam(String dataXJson) throws Exception {
    // 工作目录
    String fileName = DATAXFILENAME + UUID.randomUUID() + ".json";
    String path = MessageFormat.format("{0}/{1}", getWorkingDirectory(), fileName);
    File file = new File(path);
    try {
      FileUtils.writeStringToFile(file, dataXJson, Charset.forName("utf-8"));
    } catch (IOException e) {
      logger.error("Create dataX json file error", e);
      throw e;
    }
    return file;
  }

  @Override
  public BaseParam getParam() {
    return this.impExpParam;
  }
}
