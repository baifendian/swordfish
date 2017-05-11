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
package com.baifendian.swordfish.execserver.runner.adhoc;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.job.struct.node.adhoc.AdHocParam;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.AdHocJsonObject;
import com.baifendian.swordfish.dao.model.AdHocResult;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.common.FunctionUtil;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.job.JobProps;
import org.slf4j.Logger;

import java.util.List;

/**
 * 即席查询作业
 */
public class AdHocSqlJob {
  /**
   * job 通用参数
   */
  private JobProps props;

  /**
   * 即席的数据库连接参数
   */
  private AdHocDao adHocDao;

  /**
   * 即席的参数信息
   */
  private AdHocParam param;

  /**
   * 记录日志
   */
  private Logger logger;

  public AdHocSqlJob(JobProps props, Logger logger) {
    this.props = props;
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.param = JsonUtil.parseObject(props.getJobParams(), AdHocParam.class);
    this.logger = logger;
  }

  /**
   * 具体执行的过程
   *
   * @throws Exception
   */
  public FlowStatus process() throws Exception {
    logger.debug("process job: {}", props.getJobParams());

    // 得到查询语句
    String sqls = param.getStms();

    // 需要支持变量参数替换, 如 $[yyyyMMdd], TODO::
    // sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);

    // 创建自定义函数
    List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), props.getExecId(), logger, BaseConfig.getHdfsResourcesDir(props.getProjectId()), true);

    logger.info("exec sql: {}, funcs: {}", sqls, funcs);

    // 查询结果写入数据库
    ResultCallback resultCallback = (execResult, startTime, endTime) -> {
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

      // 更新结果到数据库中
      adHocDao.updateAdHocResult(adHocResult);
    };

    // 切分 sql
    List<String> execSqls = CommonUtil.sqlSplit(sqls);

    HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, props.getProxyUser(), true, resultCallback, param.getLimit(), logger);

    return hiveSqlExec.execute() ? FlowStatus.SUCCESS : FlowStatus.FAILED;
  }
}
