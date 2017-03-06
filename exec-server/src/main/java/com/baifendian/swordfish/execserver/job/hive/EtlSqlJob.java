package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.job.AbstractJob;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.hive.FunctionUtil;
import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author : liujin
 * @date : 2017-03-06 10:06
 */
public class EtlSqlJob extends AbstractJob {

    private SqlParam param;

    private Map<String, String> definedParamMap;

    private List<ExecResult> results;


    public EtlSqlJob(String jobId, PropertiesConfiguration props, Logger logger) throws IOException {
        super(jobId, props, logger);

        this.param = JsonUtil.parseObject(props.getString(JOB_PARAMS), SqlParam.class);
        this.definedParamMap = (Map<String, String>)props.getProperty(DEFINED_PARAMS);
    }

    @Override
    public void process() throws Exception {
        String sqls = param.getValue();
        sqls = ParamHelper.resolvePlaceholders(sqls, definedParamMap);
        List<String> funcs = FunctionUtil.createFuncs(sqls, projectId);
        List<String> execSqls = CommonUtil.sqlSplit(sqls);
        HiveSqlExec hiveSqlExec = new HiveSqlExec(funcs, execSqls, getProxyUser(), param.getDbName(), false, null, null, logger);
        hiveSqlExec.run();
        results = hiveSqlExec.getResults();
    }

    @Override
    public List<ExecResult> getResults(){
        return results;
    }

}
