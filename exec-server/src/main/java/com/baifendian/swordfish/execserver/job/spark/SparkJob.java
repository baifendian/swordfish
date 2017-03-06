/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月3日
 * File Name      : SparkJob.java
 */

package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
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
 * Spark 作业
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月3日
 */
public class SparkJob extends AbstractProcessJob {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** 提交的参数 */
    private final SparkParam param;

    /** app id **/
    private String appid;

    public SparkJob(String jobId, PropertiesConfiguration props, Logger logger) throws IllegalAccessException, IOException {
        super(jobId, props, logger);

        this.param = JsonUtil.parseObject(props.getString(JOB_PARAMS), SparkParam.class);

    }

    public List<String> buildCommand(){
        return SparkSubmitArgsUtil.buildArgs(param);
    }

    @Override
    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder("spark-submit");
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
