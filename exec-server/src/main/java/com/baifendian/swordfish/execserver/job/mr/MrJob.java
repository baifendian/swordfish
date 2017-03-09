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
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import com.baifendian.swordfish.execserver.utils.OsUtil;
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
import java.util.concurrent.TimeUnit;

/**
 * mr 作业
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月21日
 */
public class MrJob extends AbstractProcessJob {

    /** hadoop */
    private static final String HADOOP_COMMAND = "hadoop";

    /** jar */
    private static final String HADOOP_JAR = "jar";

    private MrParam mrParam;

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
        mrParam = (MrParam) param;
        mrParam.setQueue(props.getQueue());
    }

    public List<String> buildCommand(){
        if (mrParam.getArgs() != null) {
            String args = ParamHelper.resolvePlaceholders(mrParam.getArgs(), definedParamMap);
            mrParam.setArgs(args);
        }
        return HadoopJarArgsUtil.buildArgs(mrParam);
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
                logger.info("MR execute log : {}", line);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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

    public void cancel() throws Exception {
        super.cancel();

        if(appid != null) {
            String cmd = "yarn application -kill " + appid;

            if (props.getProxyUser() != null) {
                cmd = "sudo -u " + props.getProxyUser() + " " + cmd;
            }
            Runtime.getRuntime().exec(cmd);

            completeLatch.await(KILL_TIME_MS, TimeUnit.MILLISECONDS);
        }

    }

}
