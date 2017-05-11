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
package com.baifendian.swordfish.execserver.job.hql;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.hql.SqlParam;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.FunctionUtil;
import com.baifendian.swordfish.execserver.engine.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.job.AbstractJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.util.List;

public class EtlSqlJob extends AbstractJob {
  protected SqlParam param;

  protected List<ExecResult> results;

  public EtlSqlJob(String jobId, JobProps props, Logger logger) {
    super(jobId, props, logger);
  }

  @Override
  public void initJobParams() {
    this.param = JsonUtil.parseObject(props.getJobParams(), SqlParam.class);
  }

  @Override
  public void process() throws Exception {
    String sqls = param.getSql();
    sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
    List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), props.getExecId(), logger, getWorkingDirectory(), false);

    logger.info("exec sql:{}, funcs:{}", sqls, funcs);

    List<String> execSqls = CommonUtil.sqlSplit(sqls);
    HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, getProxyUser(), false, null, null, logger);
    hiveSqlExec.execute();

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
