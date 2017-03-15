/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.mapper.AdHocResultMapper;
import com.baifendian.swordfish.dao.mysql.model.AdHocJsonObject;
import com.baifendian.swordfish.dao.mysql.model.AdHocResult;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author : liujin
 * @date : 2017-03-08 12:47
 */
public class AdHocSqlJob extends EtlSqlJob {

    private AdHocResultMapper adHocResultMapper;

    public AdHocSqlJob(String jobId, JobProps props, Logger logger) throws IOException {
        super(jobId, props, logger);
        adHocResultMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(AdHocResultMapper.class);
    }

    @Override
    public void process() throws Exception {
        String sqls = param.getSql();
        sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
        List<String> funcs = FunctionUtil.createFuncs(param.getUdfs(), jobId);
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
