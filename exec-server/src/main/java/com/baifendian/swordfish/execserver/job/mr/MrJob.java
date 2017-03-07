/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月21日
 * File Name      : MrJob.java
 */

package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.PlaceholderUtil;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * mr 作业
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月21日
 */
public class MrJob extends AbstractProcessJob {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** hadoop */
    private static final String HADOOP_COMMAND = "hadoop";

    /** jar */
    private static final String HADOOP_JAR = "jar";

    private MrParam param;

    /** yarn 的 application id */
    private String appid;

    /**
     *
     * @param jobId
     * @param props
     * @param logger
     * @throws IOException
     */
    public MrJob(String jobId, JobProps props, Logger logger) throws IOException {
        super(jobId, props, logger);
    }

    @Override
    public void initJobParams(){
        param = JsonUtil.parseObject(props.getJobParams(), MrParam.class);
    }

    public List<String> buildCommand(){
        if (param.getArgs() != null) {
            List<String> appArgs = new ArrayList<>();
            for (String arg : param.getArgs()) {
                //arg = PlaceholderUtil.resolvePlaceholders(arg, systemParamMap, true);
                arg = PlaceholderUtil.resolvePlaceholders(arg, definedParamMap, true);
                appArgs.add(arg);
            }
            param.setArgs(appArgs);
        }
        return HadoopJarArgsUtil.buildArgs(param);
    }

    @Override
    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder(HADOOP_COMMAND);
        processBuilder.command().add(HADOOP_JAR);
        List<String> args = buildCommand();
        if (args != null) {
            processBuilder.command().addAll(args);
        }

        return processBuilder;
    }

    public String getAppId() {
        return appid;
    }

    @Override
    protected void readProcessOutput() {
        InputStream inputStream = process.getInputStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (appid == null) {
                    appid = findAppid(line);
                }
                // jobContext.appendLog(line);
                LOGGER.info("MR execute log : {}", line);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 获取 appid
     * <p>
     *
     * @param line
     * @return appid
     */
    private String findAppid(String line) {
        if (line.contains("impl.YarnClientImpl: Submitted application")) {
            return line.substring(line.indexOf("application") + "application".length() + 1);
        }
        return null;
    }

}
