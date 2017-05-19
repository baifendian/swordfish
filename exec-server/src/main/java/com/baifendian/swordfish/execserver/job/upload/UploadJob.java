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
import com.baifendian.swordfish.common.job.struct.node.upload.UploadParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.AbstractProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import org.slf4j.Logger;

public class UploadJob extends AbstractProcessJob {
  private UploadParam param;

  private static final String DEFAULT_CHARSET = "UTF-8";

  public UploadJob(JobProps props, Logger logger) {
    super(props, logger);
  }

  @Override
  public void initJob() {
    this.param = JsonUtil.parseObject(props.getJobParams(), UploadParam.class);
  }

  @Override
  public String createCommand() throws Exception {
    return null;
  }

  @Override
  public BaseParam getParam() {
    return param;
  }
}
