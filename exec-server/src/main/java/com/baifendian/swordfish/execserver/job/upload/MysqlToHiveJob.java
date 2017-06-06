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

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.HiveWriter;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.MysqlReader;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;

import static com.baifendian.swordfish.common.job.struct.node.JobType.IMPORT;

/**
 * mysql 导入 hive 任务
 */
public class MysqlToHiveJob extends UploadJob {

  private MysqlReader mysqlReader;
  private HiveWriter hiveWriter;

  public MysqlToHiveJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  @Override
  public String getDataXJson() throws JSONException {
    //TODO setting json化
    JSONObject wirter = new JSONObject(this.hiveWriter.getDataXWriter());
    JSONObject reader = new JSONObject(this.mysqlReader.getDataXReader());
    JSONObject json = new JSONObject(this.DATAXJSON);

    return null;
  }

  @Override
  public BaseParam getParam() {
    return null;
  }

  @Override
  public String createCommand() throws Exception {
    return null;
  }

  @Override
  public void after() throws Exception {
    super.after();
  }

  @Override
  public void initJob() {
    super.initJob();
    this.mysqlReader = (MysqlReader) impExpParam.getReader();
    this.hiveWriter = (HiveWriter) impExpParam.getWriter();
  }
}
