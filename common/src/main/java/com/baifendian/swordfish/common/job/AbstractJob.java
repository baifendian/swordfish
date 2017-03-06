package com.baifendian.swordfish.common.job;

import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.job.logger.JobLogger;
import com.baifendian.swordfish.common.job.utils.ProcessUtil;
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

/**
 * @author : liujin
 * @date : 2017-03-06 10:56
 */
public class AbstractJob implements Job {
    /** 作业执行目录key 值为String **/
    public static final String WORKING_DIR = "working.dir";

    /** 作业执行用户key 值为String **/
    public static final String PROXY_USER = "proxy.user";

    /** 作业配置参数key 值为json字符串 **/
    public static final String JOB_PARAMS = "job.params";

    /** 自定义参数key 值为Map<String, String> **/
    public static final String DEFINED_PARAMS = "defined.params";

    /** 项目id  **/
    public static final String PROJECT_ID = "project.id";

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

    protected Map<String, String> definedParamMap;

    protected int projectId;

    /**
     *
     * @param jobId 生成的作业id
     * @param props 作业配置信息,各类作业根据此配置信息生成具体的作业
     * @param logger 日志
     */
    protected AbstractJob(String jobId, PropertiesConfiguration props, Logger logger) throws IOException {
        this.jobId = jobId;
        this.props = props;
        this._logger = logger;
        this.logger = new JobLogger(jobId, logger);
        initJobParams();
        this.definedParamMap = (Map<String, String>)props.getProperty(DEFINED_PARAMS);
        this.projectId = props.getInt(PROJECT_ID);
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public void before() throws Exception{}

    @Override
    public void process() throws Exception {

    }

    @Override
    public void run() throws Exception {
        before();
        process();
        after();
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

    @Override
    public boolean hasResults(){
        return false;
    }

    @Override
    public List<ExecResult> getResults(){
        return null;
    }

}
