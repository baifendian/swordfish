/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月26日
 * File Name      : ProcessJob.java
 */

package com.baifendian.swordfish.common.job;

import com.baifendian.swordfish.common.job.utils.ProcessUtil;
import com.baifendian.swordfish.common.job.exception.ExecException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 进程 Job
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月26日
 */
public abstract class AbstractProcessJob extends AbstractJob {
    /**
     *
     * @param jobId 生成的作业id
     * @param props 作业配置信息,各类作业根据此配置信息生成具体的作业
     * @param logger 日志
     */
    protected AbstractProcessJob(String jobId, JobProps props, Logger logger) throws IOException {
        super(jobId, props, logger);
    }

    /**
     * 创建 ProcessBuilder
     * <p>
     *
     * @return {@link ProcessBuilder}
     */
    public abstract ProcessBuilder createProcessBuilder() throws IOException;

    @Override
    public void before() throws Exception{}

    @Override
    public void process() throws Exception {
        try {
            ProcessBuilder processBuilder = createProcessBuilder();
            if(processBuilder == null){
                exitCode = 0;
                complete = true;
                return;
            }

            String proxyUser = getProxyUser();
            String workDir = getWorkingDirectory();
            logger.info("jobId:{} proxyUser:{} workDir:{}", jobId, proxyUser, workDir);
            if(proxyUser != null){
                String commandFile = workDir + File.separator + jobId + ".command";
                logger.info("generate command file:{}", commandFile);
                FileUtils.writeStringToFile(new File(commandFile), StringUtils.join(processBuilder.command(), " "));
                processBuilder.command("sudo -u ", proxyUser, "sh -c", commandFile);
            } else {
                List<String> commands = processBuilder.command();
                processBuilder.command("sh -c");
                processBuilder.command(commands);
            }
            processBuilder.directory(new File(workDir));
            // 将 error 信息 merge 到标准输出流
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            // 打印 进程的启动命令行
            printCommand(processBuilder);

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
        complete = true;

    }

    @Override
    public void cancel() throws Exception {
        // 暂不支持
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

}
