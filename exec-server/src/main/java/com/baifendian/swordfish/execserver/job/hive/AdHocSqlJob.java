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

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.AdHocResultMapper;
import com.baifendian.swordfish.dao.model.AdHocJsonObject;
import com.baifendian.swordfish.dao.model.AdHocResult;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
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
    logger.debug("{}", props.getJobParams());
    logger.debug("is continue:{}", param.isBeContinue());
    String sqls = param.getSql();
    // 不支持参数替换
    //sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
    List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), jobIdLog, BaseConfig.getHdfsResourcesDir(props.getProjectId()) , true);
    List<String> execSqls = CommonUtil.sqlSplit(sqls);
    logger.info("exec sql:{}, funcs:{}", sqls, funcs);
    /** 查询结果写入数据库 */
    ResultCallback resultCallback = new ResultCallback() {
      @Override
      public void handleResult(ExecResult execResult) {
        AdHocResult adHocResult = new AdHocResult();
        adHocResult.setAdHocId(props.getAdHocId());
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
    HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, getProxyUser(), null, param.isBeContinue(), resultCallback, null, logger);
    initAdHocResult(execSqls);
    hiveSqlExec.run();
    results = hiveSqlExec.getResults();
  }

  private void initAdHocResult(List<String> execSqls){
    if(CollectionUtils.isNotEmpty(execSqls)){
      adHocResultMapper.delete(props.getAdHocId());
      int index=0;
      for(String stm: execSqls){
        AdHocResult adHocResult = new AdHocResult();
        adHocResult.setAdHocId(props.getAdHocId());
        adHocResult.setStm(stm);
        adHocResult.setIndex(index++);
        adHocResult.setStatus(FlowStatus.INIT);
        adHocResult.setCreateTime(new Timestamp(System.currentTimeMillis()));
        adHocResultMapper.insert(adHocResult);
      }
    }
  }

}
