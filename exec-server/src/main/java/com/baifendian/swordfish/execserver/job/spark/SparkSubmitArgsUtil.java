/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月9日
 * File Name      : SparkSubmitArgsBuilder.java
 */

package com.baifendian.swordfish.execserver.job.spark;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Spark 提交参数构建器
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月9日
 */
public class SparkSubmitArgsUtil {

    /**
     * 构建参数数组
     * <p>
     *
     * @return 参数数组
     */
    public static List<String> buildArgs(SparkParam param) {
        List<String> args = new ArrayList<>();

        args.add(SparkSubmitArgsConst.MASTER);
        args.add("yarn");

        args.add(SparkSubmitArgsConst.DEPLOY_MODE);
        args.add("cluster");

        if (StringUtils.isNotEmpty(param.getMainClass())) {
            args.add(SparkSubmitArgsConst.CLASS);
            args.add(param.getMainClass());
        }

        if (StringUtils.isNotEmpty(param.getDriverCores())) {
            args.add(SparkSubmitArgsConst.DRIVER_CORES);
            args.add(param.getDriverCores());
        }

        if (StringUtils.isNotEmpty(param.getDriverMemory())) {
            args.add(SparkSubmitArgsConst.DRIVER_MEMORY);
            args.add(param.getDriverMemory());
        }

        if (StringUtils.isNotEmpty(param.getNumExecutors())) {
            args.add(SparkSubmitArgsConst.NUM_EXECUTORS);
            args.add(param.getNumExecutors());
        }

        if (StringUtils.isNotEmpty(param.getExecutorCores())) {
            args.add(SparkSubmitArgsConst.EXECUTOR_CORES);
            args.add(param.getExecutorCores());
        }

        if (StringUtils.isNotEmpty(param.getExecutorMemory())) {
            args.add(SparkSubmitArgsConst.EXECUTOR_MEMORY);
            args.add(param.getExecutorMemory());
        }

        if (param.getJars() != null && !param.getJars().isEmpty()) {
            args.add(SparkSubmitArgsConst.JARS);
            args.add(StringUtils.join(param.getJars(), ","));
        }

        if (param.getFiles() != null && !param.getFiles().isEmpty()) {
            args.add(SparkSubmitArgsConst.FILES);
            args.add(StringUtils.join(param.getFiles(), ","));
        }

        if (!param.getArchives().isEmpty()) {
            args.add(SparkSubmitArgsConst.ARCHIVES);
            args.add(StringUtils.join(param.getArchives(), ","));
        }

        if (StringUtils.isNotEmpty(param.getQueue())) {
            args.add(SparkSubmitArgsConst.QUEUE);
            args.add(param.getQueue());
        }

        if (StringUtils.isNotEmpty(param.getMainJar())) {
            args.add(param.getMainJar());
        }

        if (!param.getAppArgs().isEmpty()) {
            for (String appArg : param.getAppArgs()) {
                args.add(appArg);
            }
        }
        return args;
    }

}
