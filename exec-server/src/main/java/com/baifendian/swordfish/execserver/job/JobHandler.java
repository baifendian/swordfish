package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.hadoop.HdfsUtil;
import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.hadoop.hdfs.HdfsPathManager;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ExecutionNode;
import com.baifendian.swordfish.dao.mysql.model.FlowNode;
import com.baifendian.swordfish.execserver.exception.ExecTimeoutException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author : liujin
 * @date : 2017-03-02 12:56
 */
public class JobHandler {

    private final Logger logger = LoggerFactory.getLogger(JobHandler.class);

    private String JOB_SCRIPT_PATH_FORMAT = "{0}/job_script/{1}/{2}";

    private String DATETIME_FORMAT="yyyyMMddHHmmss";

    private FlowNode node;

    private ExecutionFlow executionFlow;

    private ExecutionNode executionNode;

    private FlowDao flowDao;

    private String jobId;

    private ExecutorService executorService;

    private int timeout;

    private final long startTime;

    private Map<String, String> systemParamMap;

    private Map<String, String> customParamMap;

    private Map<String, String> allParamMap;

    public JobHandler(FlowDao flowDao, ExecutionFlow executionFlow, ExecutionNode executionNode, FlowNode node, ExecutorService executorService, int timeout,
                      Map<String, String> systemParamMap, Map<String, String> customParamMap){
        this.flowDao = flowDao;
        this.executionFlow = executionFlow;
        this.executionNode = executionNode;
        this.node = node;
        this.executorService = executorService;
        this.timeout = timeout;
        this.systemParamMap = systemParamMap;
        this.customParamMap = customParamMap;
        this.startTime = System.currentTimeMillis();
        this.jobId = MessageFormat.format("{0}_{1}_{2}_{3}", node.getType().name(), BFDDateUtils.now(DATETIME_FORMAT),
                    node.getId(), executionNode.getId());
        // custom参数会覆盖system参数
        allParamMap = new HashMap<>();
        allParamMap.putAll(systemParamMap);
        allParamMap.putAll(customParamMap);

    }

    public FlowStatus handle() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        String jobScriptPath = MessageFormat.format(JOB_SCRIPT_PATH_FORMAT, BFDDateUtils.now(Constants.BASE_DATE_FORMAT), jobId);
        logger.info("job:{} script path:{}", jobId, jobScriptPath);

        // 下载节点级别的资源文件到脚本目录
        String nodeResPath = HdfsPathManager.genNodeHdfsPath("aa");
        HdfsUtil.GetFile(nodeResPath + "/*", jobScriptPath);
        // 建立资源文件软链接
        String flowResLocalPath = HdfsPathManager.genFlowLocalPath(executionFlow.getProjectName(), executionFlow.getFlowName(), executionFlow.getId());
        File dirFile = new File(flowResLocalPath);
        for(File file:Arrays.asList(dirFile.listFiles())){
            String targetFileName = jobScriptPath + "/" + file.getName();
            File targetFile = new File(targetFileName);
            if(!targetFile.exists()){
                Files.createSymbolicLink(file.toPath(), targetFile.toPath());
            }
        }

        // 作业参数配置
        PropertiesConfiguration props = new PropertiesConfiguration();
        props.addProperty(AbstractProcessJob.JOB_PARAMS, node.getParam());
        props.addProperty(AbstractProcessJob.WORKING_DIR, jobScriptPath);
        props.addProperty(AbstractProcessJob.PROXY_USER, executionFlow.getProxyUser());
        props.addProperty(AbstractProcessJob.DEFINED_PARAMS, allParamMap);
        props.addProperty(AbstractProcessJob.PROJECT_ID, executionFlow.getProjectId());

        logger.info("props:{}", props);
        Job job = JobTypeManager.newJob(jobId, node.getType().name(), props, logger);
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
                    job.run(); // run job
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
