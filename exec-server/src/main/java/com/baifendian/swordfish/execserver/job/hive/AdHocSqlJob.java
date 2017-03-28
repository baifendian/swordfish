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

import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.AdHocResultMapper;
import com.baifendian.swordfish.dao.model.AdHocJsonObject;
import com.baifendian.swordfish.dao.model.AdHocResult;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * 即席查询作业
 */
public class AdHocSqlJob extends EtlSqlJob {

  private AdHocResultMapper adHocResultMapper;

  public AdHocSqlJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    super(jobIdLog, props, logger);
    adHocResultMapper = ConnectionFactory.getSqlSession().getMapper(AdHocResultMapper.class);
  }

  @Override
  public void process() throws Exception {
    String sqls = param.getSql();
    // 不支持参数替换
    //sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
    List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), jobIdLog, getWorkingDirectory());
    List<String> execSqls = CommonUtil.sqlSplit(sqls);
    /** 查询结果写入数据库 */
    ResultCallback resultCallback = new ResultCallback() {
      @Override
      public void handleResult(ExecResult execResult) {
        AdHocResult adHocResult = new AdHocResult();
        adHocResult.setExecId(props.getExecId());
        adHocResult.setNodeId(props.getNodeId());
        adHocResult.setStm(execResult.getStm());
        adHocResult.setIndex(execResult.getIndex());
        adHocResult.setStatus(execResult.getStatus());
        AdHocJsonObject adHocJsonObject = new AdHocJsonObject();
        adHocJsonObject.setTitles(execResult.getTitles());
        adHocJsonObject.setValues(execResult.getValues());
        adHocResult.setResult(JsonUtil.toJsonString(adHocJsonObject));

        adHocResultMapper.update(adHocResult); // 更新结果到数据库中
      }
    };
    HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, getProxyUser(), null, false, resultCallback, null, logger);
    hiveSqlExec.run();
    results = hiveSqlExec.getResults();
  }
}
