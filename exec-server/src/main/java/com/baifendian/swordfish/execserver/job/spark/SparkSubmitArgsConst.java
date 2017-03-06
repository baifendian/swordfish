/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月9日
 * File Name      : SparkSubmitArgsConst.java
 */

package com.baifendian.swordfish.execserver.job.spark;

/**
 * spark 提交的参数常量
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月9日
 */
public class SparkSubmitArgsConst {

    public static final String MASTER = "--master";

    public static final String DEPLOY_MODE = "--deploy-mode";

    /** --class CLASS_NAME */
    public static final String CLASS = "--class";

    /** --arg ARG */
    public static final String ARGS = "--arg";

    /** --driver-cores NUM */
    public static final String DRIVER_CORES = "--driver-cores";

    /** --driver-memory MEM */
    public static final String DRIVER_MEMORY = "--driver-memory";

    /** --num-executors NUM */
    public static final String NUM_EXECUTORS = "--num-executors";

    /** --executor-cores NUM */
    public static final String EXECUTOR_CORES = "--executor-cores";

    /** --executor-memory MEM */
    public static final String EXECUTOR_MEMORY = "--executor-memory";

    /** --addJars jars */
    public static final String JARS = "--addJars";

    /** --files files */
    public static final String FILES = "--files";

    /** --archives archives */
    public static final String ARCHIVES = "--archives";

    /** --queue QUEUE */
    public static final String QUEUE = "--queue";
}
