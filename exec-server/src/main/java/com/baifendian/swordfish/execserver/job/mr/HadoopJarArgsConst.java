/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月22日
 * File Name      : HadoopJarArgsConst.java
 */

package com.baifendian.swordfish.execserver.job.mr;

/**
 * Hadoop jar 参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月22日
 */
public class HadoopJarArgsConst {

    /** -D */
    public static final String D = "-D";

    /** -libjars */
    public static final String JARS = "-libjars";

    /** --files files */
    public static final String FILES = "--files";

    /** -archives */
    public static final String ARCHIVES = "-archives";

    /** -D mapreduce.job.queuename=XXX */
    public static final String QUEUE = "mapreduce.job.queuename";
}
