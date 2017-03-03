/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月14日
 * File Name      : DefaultProcessJob.java
 */

package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.exception.ExecException;
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

    /** 命令 */
    private final String command;

    /** 参数 */
    private final List<String> args;

    /** 环境变量 */
    private final Map<String, String> envMap;

    /**
     *
     * @param jobId
     * @param props
     * @param logger
     */
    public DefaultProcessJob(String jobId, PropertiesConfiguration props, Logger logger) throws IOException {
        super(jobId, props, logger);

        command = (String)jobParams.get("value");
        if(command == null || StringUtils.isEmpty(command)){
            throw new ExecException("DefaultProcessJob command param must not null");
        }
        args = (List<String>)jobParams.get("args");

        envMap = (Map<String, String>)jobParams.get("envMap");

    }

    @Override
    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (args != null) {
            processBuilder.command().addAll(args);
        }
        if (envMap != null) {
            processBuilder.environment().putAll(envMap);
        }

        return processBuilder;
    }

    public static void main(String[] args) throws Exception {
        PropertiesConfiguration props = new PropertiesConfiguration();
        props.addProperty(AbstractProcessJob.PROXY_USER, "hadoop");
        props.addProperty(AbstractProcessJob.WORKING_DIR, "/home/swordfish");
        props.addProperty(AbstractProcessJob.JOB_PARAMS, "{\"command\":\"ls -l\"}");
        props.addProperty("timeout", 100);
        Logger logger = LoggerFactory.getLogger("shellJob");
        DefaultProcessJob job = new DefaultProcessJob("NODE_1_2017", props, logger);
        job.exec();
        System.out.println("run finished!");
    }

}
