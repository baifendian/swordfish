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
import com.baifendian.swordfish.common.job.struct.node.hql.HqlParam;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.FunctionUtil;
import com.baifendian.swordfish.execserver.engine.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.job.AbstractJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import java.util.List;
import org.slf4j.Logger;

public class EtlSqlJob extends AbstractJob {

  protected HqlParam param;

  protected List<ExecResult> results;

  public EtlSqlJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  @Override
  public void init() {
    this.param = JsonUtil.parseObject(props.getJobParams(), HqlParam.class);
  }

  @Override
  public void process() throws Exception {
    try {
      String sqls = param.getSql();

      // 解析其中的变量
      sqls = ParamHelper.resolvePlaceholders(sqls, props.getDefinedParams());
      List<String> funcs = FunctionUtil
          .createFuncs(param.getUdfs(), props.getExecId(), logger, getWorkingDirectory(), false);

      logger.info("\nhql:\n{}\nfuncs:\n{}", sqls, funcs);

      List<String> execSqls = CommonUtil.sqlSplit(sqls);
      HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, props.getProxyUser(), false, null,
          null, logger);

      started = true;

      exitCode = (hiveSqlExec.execute()) ? 0 : -1;
    } catch (Exception e) {
      logger.error(String.format("hql process exception, sql: %s", param.getSql()), e);
      exitCode = -1;
    } finally {
      complete = true;
    }
  }

  @Override
  public void cancel(boolean cancelApplication) throws Exception {
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
