/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月14日
 * File Name      : JavaProcessJob.java
 */

package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.bfd.harpc.common.configure.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Java 进程
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月14日
 */
public class JavaProcessJob extends AbstractProcessJob {

    /** java */
    private static final String JAVA_COMMAND = "java";

    /** java 命令可选参数 */
    private static List<String> javaOptList = new ArrayList<>();

    /** 参数 */
    private ProcessParam param;

    static {
        // java 可选参数
        String javaOpts = PropertiesConfiguration.getValue("job.java.opts", "");
        if (StringUtils.isNotEmpty(javaOpts)) {
            javaOptList.addAll(Arrays.asList(javaOpts.split(" ")));
        }
    }

    public JavaProcessJob(String jobId, JobProps props, Logger logger) throws IOException {
        super(jobId, props, logger);

    }

    @Override
    public void initJobParams(){
        param = JsonUtil.parseObject(props.getJobParams(), ProcessParam.class);
    }

    @Override
    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder(JAVA_COMMAND);
        if (!javaOptList.isEmpty()) {
            processBuilder.command().addAll(javaOptList);
        }
        if (param.getArgs() != null) {
            processBuilder.command().addAll(param.getArgs());
        }
        if (param.getEnvMap() != null) {
            processBuilder.environment().putAll(param.getEnvMap());
        }

        // List<String> commands=new ArrayList<String>();
        // String command = JAVA_COMMAND + " ";
        // command += getJVMArguments() + " ";
        // command += "-Xms" + getInitialMemorySize() + " ";
        // command += "-Xmx" + getMaxMemorySize() + " ";
        // if(getClassPaths()!=null && !getClassPaths().trim().equals("")){
        // command += "-cp " + getClassPaths()+ " ";
        // }
        // command += getJavaClass() + " ";
        // command += getMainArguments();

        // commands.add(command);

        return processBuilder;
    }

    @Override
    public BaseParam getParam(){
        return param;
    }

}
