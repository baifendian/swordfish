/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月14日
 * File Name      : DefaultProcessJob.java
 */

package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.utils.PlaceholderUtil;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.shell.ShellJob;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 默认的进程 job
 * <p>
 * 
 * @author : liujin
 * @date : 2017年3月2日
 */
public class DefaultProcessJob extends AbstractProcessJob {

    /** 参数 */
    private ProcessParam param;

    /**
     *
     * @param jobId
     * @param props
     * @param logger
     */
    public DefaultProcessJob(String jobId, JobProps props, Logger logger) throws IOException {
        super(jobId, props, logger);

        if(param.getScript() == null || StringUtils.isEmpty(param.getScript())){
            throw new ExecException("DefaultProcessJob script param must not null");
        }

    }

    @Override
    public void initJobParams(){
        param = JsonUtil.parseObject(props.getJobParams(), ProcessParam.class);
    }

    @Override
    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder(param.getScript());
        if (param.getArgs() != null) {
            processBuilder.command().addAll(param.getArgs());
        }
        if (param.getArgs() != null) {
            processBuilder.environment().putAll(param.getEnvMap());
        }

        return processBuilder;
    }

    @Override
    public BaseParam getParam(){
        return param;
    }

}
