/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月4日
 * File Name      : NoopJob.java
 */

package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
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
    protected NoopJob(String jobId, PropertiesConfiguration props, Logger logger) throws IOException {
        super(jobId, props, logger);
    }

    @Override
    public ProcessBuilder createProcessBuilder(){
        return null;
    }


}
