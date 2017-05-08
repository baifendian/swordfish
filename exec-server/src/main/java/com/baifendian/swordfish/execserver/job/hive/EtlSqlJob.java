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
package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.job.AbstractJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class EtlSqlJob extends AbstractJob {
  private static final Logger logger = LoggerFactory.getLogger(EtlSqlJob.class);

  protected SqlParam param;

  protected List<ExecResult> results;

  public EtlSqlJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);
  }

  @Override
  public void initJobParams() {
    this.param = JsonUtil.parseObject(props.getJobParams(), SqlParam.class);
  }

  @Override
  public void process() throws Exception {
    String sqls = param.getSql();
    sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
    List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), jobId, getWorkingDirectory(), false);

    logger.info("exec sql:{}, funcs:{}", sqls, funcs);

    List<String> execSqls = CommonUtil.sqlSplit(sqls);
    HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, getProxyUser(), null, false, null, null, logger);
    hiveSqlExec.run();

    results = hiveSqlExec.getResults();
  }

  @Override
  public List<ExecResult> getResults() {
    return results;
  }

  @Override
  public BaseParam getParam() {
    return param;
  }
}
