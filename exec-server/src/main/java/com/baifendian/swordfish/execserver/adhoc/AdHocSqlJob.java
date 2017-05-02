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
package com.baifendian.swordfish.execserver.adhoc;

import com.baifendian.swordfish.common.job.struct.hql.AdHocParam;
import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.model.AdHocJsonObject;
import com.baifendian.swordfish.dao.model.AdHocResult;
import com.baifendian.swordfish.execserver.job.hive.FunctionUtil;
import com.baifendian.swordfish.execserver.job.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.job.hive.ResultCallback;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 即席查询作业
 */
public class AdHocSqlJob {

  private AdHocDao adHocDao;

  private AdHocParam param;

  private String jobIdLog;

  private JobProps props;

  private Logger logger;

  public AdHocSqlJob(String jobIdLog, JobProps props, Logger logger) throws IOException {
    this.jobIdLog = jobIdLog;
    this.logger = logger;
    this.props = props;
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    param = JsonUtil.parseObject(props.getJobParams(), AdHocParam.class);
  }

  public void process() throws Exception {
    logger.debug("{}", props.getJobParams());
    String sqls = param.getStms();
    // 不支持参数替换
    //sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
    try {
      List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), jobIdLog, BaseConfig.getHdfsResourcesDir(props.getProjectId()), true);
      List<String> execSqls = CommonUtil.sqlSplit(sqls);
      logger.info("exec sql:{}, funcs:{}", sqls, funcs);
      /** 查询结果写入数据库 */
      ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void handleResult(ExecResult execResult, Date startTime, Date endTime) {
          AdHocResult adHocResult = new AdHocResult();
          adHocResult.setExecId(props.getAdHocId());
          adHocResult.setStm(execResult.getStm());
          adHocResult.setIndex(execResult.getIndex());
          adHocResult.setStatus(execResult.getStatus());
          AdHocJsonObject adHocJsonObject = new AdHocJsonObject();
          adHocJsonObject.setTitles(execResult.getTitles());
          adHocJsonObject.setValues(execResult.getValues());
          adHocResult.setResult(JsonUtil.toJsonString(adHocJsonObject));
          adHocResult.setStartTime(startTime);
          adHocResult.setEndTime(endTime);

          adHocDao.updateAdHocResult(adHocResult); // 更新结果到数据库中
        }
      };
      HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, props.getProxyUser(), null, true, resultCallback, param.getLimit(), logger);
      adHocDao.initAdHocResult(props.getAdHocId(), execSqls);
      hiveSqlExec.run();
    } catch (Exception e){
      logger.error("ad hoc job run error", e);
      throw e;
    }
  }

}
