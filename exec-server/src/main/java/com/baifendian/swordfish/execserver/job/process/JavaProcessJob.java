/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月14日
 * File Name      : JavaProcessJob.java
 */

package com.baifendian.swordfish.execserver.job.process;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
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
    private final List<String> args;

    /** 环境变量 */
    private final Map<String, String> envMap;

    static {
        // java 可选参数
        String javaOpts = PropertiesConfiguration.getValue("job.java.opts", "");
        if (StringUtils.isNotEmpty(javaOpts)) {
            javaOptList.addAll(Arrays.asList(javaOpts.split(" ")));
        }
    }

    /**
     * @param args
     * @param envMap
     * @param jobIdLog
     */
    public JavaProcessJob(String jobId, org.apache.commons.configuration.PropertiesConfiguration props, Logger logger) throws IOException {
        super(jobId, props, logger);

        args = (List<String>)jobParams.get("args");

        envMap = (Map<String, String>)jobParams.get("envMap");
    }

    @Override
    public ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder(JAVA_COMMAND);
        if (!javaOptList.isEmpty()) {
            processBuilder.command().addAll(javaOptList);
        }
        if (args != null) {
            processBuilder.command().addAll(args);
        }
        if (envMap != null) {
            processBuilder.environment().putAll(envMap);
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

}
