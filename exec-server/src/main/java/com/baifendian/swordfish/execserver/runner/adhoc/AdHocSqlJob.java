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
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.struct.hql.AdHocParam;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.model.AdHocJsonObject;
import com.baifendian.swordfish.dao.model.AdHocResult;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.hql.FunctionUtil;
import com.baifendian.swordfish.execserver.job.hql.HiveSqlExec;
import com.baifendian.swordfish.execserver.job.hql.ResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.baifendian.swordfish.common.utils.StructuredArguments.jobValue;

/**
 * 即席查询作业
 */
public class AdHocSqlJob {
  private static Logger logger = LoggerFactory.getLogger(AdHocSqlJob.class.getName());

  /**
   * job 通用参数
   */
  private JobProps props;

  /**
   * 日志记录的唯一 id
   */
  private String jobId;

  /**
   * 即席的数据库连接参数
   */
  private AdHocDao adHocDao;

  /**
   * 即席的参数信息
   */
  private AdHocParam param;

  public AdHocSqlJob(JobProps props) throws IOException {
    this.props = props;
    this.jobId = props.getJobId();

    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.param = JsonUtil.parseObject(props.getJobParams(), AdHocParam.class);
  }

  /**
   * 具体执行的过程
   *
   * @throws Exception
   */
  public void process() throws Exception {
    logger.debug("{} {}", jobValue(jobId), props.getJobParams());

    // 得到查询语句
    String sqls = param.getStms();

    // 需要支持变量参数替换, 如 $[yyyyMMdd], TODO::
    // sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);

    try {
      // 创建自定义函数
      List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), jobId, BaseConfig.getHdfsResourcesDir(props.getProjectId()), true);

      logger.info("{} exec sql:{}, funcs:{}", jobValue(jobId), sqls, funcs);

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

      List<String> execSqls = CommonUtil.sqlSplit(sqls);

      HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, props.getProxyUser(), true, resultCallback, param.getLimit());
      hiveSqlExec.run();
    } catch (Exception e) {
      logger.error("ad hoc job run error", e);
      throw e;
    }
  }
}
