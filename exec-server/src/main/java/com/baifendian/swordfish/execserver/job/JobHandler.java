package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mysql.enums.FlowStatus;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ExecutionNode;
import com.baifendian.swordfish.dao.mysql.model.FlowNode;
import com.baifendian.swordfish.execserver.exception.ExecTimeoutException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author : liujin
 * @date : 2017-03-02 12:56
 */
public class JobHandler {

    private final Logger logger = LoggerFactory.getLogger(JobHandler.class);

    private final Layout DEFAULT_LAYOUT = new EnhancedPatternLayout(
            "%d{yyyy-MM-dd HH:mm:ss} %c{1} %p - %m\n");

    /** 作业执行本地基础目录 */
    private String BASE_PATH;

    /** 作业执行日志存放目录 */
    private String JOB_LOG_PATH_FORMAT = "{0}/job_log/{1}/{2}";

    /** 作业执行文件存放目录 base_path/job_script/yyyy-MM-dd/jobId */
    private String JOB_SCRIPT_PATH_FORMAT = "{0}/job_script/{1}/{2}";

    private String DATE_FORMAT="yyyyMMddHHmmss";

    private FlowNode node;

    private ExecutionFlow executionFlow;

    private ExecutionNode executionNode;

    private FlowDao flowDao;

    private String jobId;

    private ExecutorService executorService;

    private int timeout;

    private final long startTime;

    public JobHandler(FlowDao flowDao, ExecutionFlow executionFlow, ExecutionNode executionNode, FlowNode node, ExecutorService executorService, int timeout,
                      Map<String, String> systemParamMap, Map<String, String> customParamMap){
        this.flowDao = flowDao;
        this.node = node;
        this.executionFlow = executionFlow;
        this.executorService = executorService;
        this.timeout = timeout;
        this.startTime = System.currentTimeMillis();
        this.jobId = MessageFormat.format("NODE_{0}_{1}", node.getId(), BFDDateUtils.now(DATE_FORMAT));

    }

    public FlowStatus handle() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String jobId = MessageFormat.format("NODE_{0}_{1}", node.getId(), BFDDateUtils.now(DATE_FORMAT));
        String jobLogFile = MessageFormat.format(JOB_LOG_PATH_FORMAT, BASE_PATH, BFDDateUtils.now(Constants.BASE_DATE_FORMAT),
                jobId + ".log");
        String jobScriptPath = MessageFormat.format(JOB_SCRIPT_PATH_FORMAT, BASE_PATH, BFDDateUtils.now(Constants.BASE_DATE_FORMAT), jobId);

        // 建立资源文件软链接
        Logger jobLogger = LoggerFactory.getLogger("xx");
        PropertiesConfiguration props = new PropertiesConfiguration();
        props.addProperty(AbstractProcessJob.JOB_PARAMS, node.getParam());

        Job job = JobTypeManager.newJob(jobId, node.getType().name(), props, jobLogger);
        // 更新executionNode状态
        executionNode.setStatus(FlowStatus.RUNNING);
        flowDao.updateExecutionNode(executionNode);
        Boolean result;
        try {
            result = submitJob(job);
        }catch (Exception e){
            result = false;
            logger.error("run job error", e);
        }
        FlowStatus status;
        if(result){
            status = FlowStatus.SUCCESS;
        }else{
            status = FlowStatus.FAILED;
        }
        return status;
    }

    private FileAppender createFileAppender(String logName) throws IOException {
        // Attempt to create FileAppender
        FileAppender fileAppender = new FileAppender(DEFAULT_LAYOUT, logName);

        logger.info("Created file appender for job " + this.jobId);
        return fileAppender;
    }

    /**
     * 运行一个 job
     * <p>
     *
     * @return 成功或失败
     */
    protected boolean submitJob(Job job) {
        // 异步提交 job
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean isSuccess = true;
                try {
                    job.exec(); // run job
                    if (job.getExitCode() != 0) {
                        isSuccess = false;
                    }
                } finally {
                    // insertLogToDb(job.getContext().getExecLogger()); //
                    // 插入日志到数据库中
                }
                return isSuccess;
            }
        });

        boolean isSuccess = false;

        // 短任务，需要设置超时时间
        if (!node.getType().typeIsLong()) {
            try {
                isSuccess = future.get(calcNodeTimeout(), TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new ExecTimeoutException("execute task time out", e);
            } catch (InterruptedException | ExecutionException e) {
                throw new ExecException("execute task get error", e);
            }
        } else { // 长任务则不需要等待执行完成
            isSuccess = true;
        }

        return isSuccess;
    }

    /**
     * 计算节点的超时时间（s），
     * <p>
     *
     * @return 超时时间
     */
    private int calcNodeTimeout() {
        int usedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        if (timeout <= usedTime) {
            throw new ExecTimeoutException("当前 workflow 已经执行超时");
        }
        return timeout - usedTime;
    }
}
