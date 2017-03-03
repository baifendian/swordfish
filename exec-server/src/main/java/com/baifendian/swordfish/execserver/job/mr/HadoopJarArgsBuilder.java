/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月22日
 * File Name      : HadoopJarArgsBuilder.java
 */

package com.baifendian.swordfish.execserver.job.mr;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Hadoop jar 参数构建器
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月22日
 */
public class HadoopJarArgsBuilder {
    private String mainJar;

    private String mainClass;

    private List<String> appArgs = new ArrayList<>();

    private List<String> dArgs = new ArrayList<>();

    private List<String> jars = new ArrayList<>();

    private List<String> files = new ArrayList<>();

    private List<String> archives = new ArrayList<>();

    private String queue;

    /**
     * 构建参数数组
     * <p>
     *
     * @return 参数数组
     */
    public List<String> buildArgs() {
        List<String> args = new ArrayList<>();

        if (StringUtils.isNotEmpty(mainJar)) {
            args.add(mainJar);
        }

        if (StringUtils.isNotEmpty(mainClass)) {
            args.add(mainClass);
        }

        if (!appArgs.isEmpty()) {
            for (String appArg : appArgs) {
                args.add(appArg);
            }
        }

        if (!dArgs.isEmpty()) {
            for (String darg : dArgs) {
                args.add(HadoopJarArgsConst.D);
                args.add(darg);
            }
        }

        if (!jars.isEmpty()) {
            args.add(HadoopJarArgsConst.JARS);
            args.add(StringUtils.join(jars, ","));
        }

        if (!files.isEmpty()) {
            args.add(HadoopJarArgsConst.FILES);
            args.add(StringUtils.join(files, ","));
        }

        if (!archives.isEmpty()) {
            args.add(HadoopJarArgsConst.ARCHIVES);
            args.add(StringUtils.join(archives, ","));
        }

        if (StringUtils.isNotEmpty(queue)) {
            args.add(HadoopJarArgsConst.D);
            args.add(HadoopJarArgsConst.QUEUE + "=" + queue);
        }
        return args;
    }

    /**
     * setter method
     * 
     * @see HadoopJarArgsBuilder#mainJar
     * @param mainJar
     *            the mainJar to set
     */
    public void setMainJar(String mainJar) {
        this.mainJar = mainJar;
    }

    /**
     * 设置 mainClass
     * <p>
     *
     * @param mainClass
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    /**
     * 设置 queue
     * <p>
     *
     * @param queue
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setQueue(String queue) {
        this.queue = queue;
        return this;
    }

    /**
     * 设置 app args
     * <p>
     *
     * @param appArgs
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setAppArgs(List<String> appArgs) {
        if (appArgs != null) {
            this.appArgs = appArgs;
        }
        return this;
    }

    /**
     * 设置 -D
     * <p>
     *
     * @param appArgs
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setDArgs(List<String> dArgs) {
        if (dArgs != null) {
            this.dArgs = dArgs;
        }
        return this;
    }

    /**
     * 设置 jars
     * <p>
     *
     * @param jars
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setJars(List<String> jars) {
        if (jars != null) {
            this.jars = jars;
        }
        return this;
    }

    /**
     * 设置 files
     * <p>
     *
     * @param files
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setFiles(List<String> files) {
        if (files != null) {
            this.files = files;
        }
        return this;
    }

    /**
     * 设置 archives
     * <p>
     *
     * @param archives
     * @return {@link HadoopJarArgsBuilder}
     */
    public HadoopJarArgsBuilder setArchives(List<String> archives) {
        if (archives != null) {
            this.archives = archives;
        }
        return this;
    }
}
