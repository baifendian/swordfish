/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月4日
 * File Name      : NoopJob.java
 */

package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * 空操作的作业
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月4日
 */
public class NoopJob extends AbstractProcessJob {

    /**
     * @param jobId  生成的作业id
     * @param props  作业配置信息,各类作业根据此配置信息生成具体的作业
     * @param logger 日志
     */
    public NoopJob(String jobId, JobProps props, Logger logger) throws IOException {
        super(jobId, props, logger);
    }

    @Override
    public ProcessBuilder createProcessBuilder(){
        return null;
    }

    @Override
    public void initJobParams(){
    }

    @Override
    public BaseParam getParam(){
        return null;
    }


}
