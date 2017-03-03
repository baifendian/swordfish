/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月26日
 * File Name      : ProcessJob.java
 */

package com.baifendian.swordfish.common.job;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.logger.JobLogger;
import com.baifendian.swordfish.common.job.utils.ProcessUtil;
import com.baifendian.swordfish.common.job.exception.ExecException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 进程 Job
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月26日
 */
public abstract class AbstractProcessJob implements Job {

    /** 作业执行目录key 值为String **/
    public static final String WORKING_DIR = "working.dir";

    /** 作业执行用户key 值为String **/
    public static final String PROXY_USER = "proxy.user";

    /** 作业配置参数key 值为json字符串 **/
    public static final String JOB_PARAMS = "job.params";

    /** 自定义参数key 值为Map<String, String> **/
    public static final String DEFINED_PARAMS = "defined.params";

    /** LOGGER */
    protected final JobLogger logger;

    private final Logger _logger;

    /** jobId **/
    protected final String jobId;

    /** {@link Process} */
    protected Process process;

    /** 配置参数 **/
    PropertiesConfiguration props;

    protected String jobPath;

    protected int exitCode;

    protected boolean complete = false;

    protected boolean canceled = false;

    protected Map<String, Object> jobParams;

    /**
     *
     * @param jobId 生成的作业id
     * @param props 作业配置信息,各类作业根据此配置信息生成具体的作业
     * @param logger 日志
     */
    protected AbstractProcessJob(String jobId, PropertiesConfiguration props, Logger logger) throws IOException {
        this.jobId = jobId;
        this.props = props;
        this._logger = logger;
        this.logger = new JobLogger(jobId, logger);
        initJobParams();
    }

    /**
     * 创建 ProcessBuilder
     * <p>
     *
     * @return {@link ProcessBuilder}
     */
    public abstract ProcessBuilder createProcessBuilder();

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public void before() throws Exception{}

    @Override
    public void exec() throws Exception {
        before();
        try {
            ProcessBuilder processBuilder = createProcessBuilder();
            if(processBuilder == null){
                exitCode = 0;
                complete = true;
                return;
            }
            ProcessBuilder curProcessBuilder;
            String proxyUser = getProxyUser();
            String workDir = getWorkingDirectory();
            logger.info("jobId:{} proxyUser:{} workDir:{}", jobId, proxyUser, workDir);
            if(proxyUser != null){
                curProcessBuilder = new ProcessBuilder();
                String commandFile = workDir + File.separator + jobId + ".command";
                logger.info("generate command file:{}", commandFile);
                FileUtils.writeStringToFile(new File(commandFile), StringUtils.join(processBuilder.command(), " "));
                curProcessBuilder.command().add(String.format("sudo -u %s sh %s", proxyUser, commandFile));
            } else {
                curProcessBuilder = processBuilder;
            }
            curProcessBuilder.directory(new File(workDir));
            // 将 error 信息 merge 到标准输出流
            curProcessBuilder.redirectErrorStream(true);
            process = curProcessBuilder.start();

            // 打印 进程的启动命令行
            printCommand(curProcessBuilder);

            readProcessOutput();
            exitCode = process.waitFor();
        } catch (Exception e) {
            // jobContext.getExecLogger().appendLog(e);
            logger.error(e.getMessage(), e);
            exitCode = -1;
        }
        if(exitCode != 0){
            throw new ExecException("Process error. Exit code is " + exitCode);
        }
        after();
        complete = true;

    }

    @Override
    public void after() throws Exception{}

    @Override
    public void cancel() throws Exception {
        // 暂不支持
    }

    @Override
    public boolean isCanceled(){
        return canceled;
    }

    @Override
    public boolean isCompleted() {
        return complete;
    }

    @Override
    public int getExitCode(){
        return exitCode;
    }

    @Override
    public PropertiesConfiguration getProperties(){
        return props;
    }

    public void initJobParams() throws IOException {
        // json字符串
        String jobParamsStr = props.getString(JOB_PARAMS);
        if(jobParamsStr != null && StringUtils.isNotEmpty(jobParamsStr)) {
            ObjectMapper mapper = new ObjectMapper();
            jobParams = mapper.readValue(jobParamsStr, Map.class);
        }else{
            jobParams = new HashMap<>();
        }
    }

    public String getWorkingDirectory() {
        String workingDir = getProperties().getString(WORKING_DIR, jobPath);
        if (workingDir == null) {
            return "";
        }

        return workingDir;
    }

    public String getProxyUser() {
        return getProperties().getString(PROXY_USER);
    }

    private void printCommand(ProcessBuilder processBuilder) {
        String cmdStr;
        try {
            cmdStr = ProcessUtil.genCmdStr(processBuilder.command());
            logger.info("任务进程的启动命令 ：{}", cmdStr);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取进程的标准输出
     * <p>
     */
    protected void readProcessOutput() {
        String threadLoggerInfoName = "LoggerInfo-" + jobId;

        Thread loggerInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("{}", line);
                    }
                } catch (Exception e) {
                    logger.error("{}", e.getMessage(), e);
                }
            }
        }, threadLoggerInfoName);

        try {
            loggerInfoThread.start();
            loggerInfoThread.join();
            // loggerErrorThread.start();
            // loggerErrorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasResult(){
        return false;
    }

    @Override
    public List<String> getResult(){
        return null;
    }

}
