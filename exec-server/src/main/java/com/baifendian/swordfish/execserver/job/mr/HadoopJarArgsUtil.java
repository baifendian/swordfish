/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

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
public class HadoopJarArgsUtil {

    /**
     * 构建参数数组
     * <p>
     *
     * @return 参数数组
     */
    public static List<String> buildArgs(MrParam param) {
        List<String> args = new ArrayList<>();

        if (StringUtils.isNotEmpty(param.getMainJar())) {
            args.add(param.getMainJar());
        }

        if (StringUtils.isNotEmpty(param.getMainClass())) {
            args.add(param.getMainClass());
        }

        if (param.getDArgs() != null && !param.getDArgs().isEmpty()) {
            for (String darg : param.getDArgs()) {
                args.add(HadoopJarArgsConst.D);
                args.add(darg);
            }
        }

        if (param.getLibJars() != null && !param.getLibJars().isEmpty()) {
            args.add(HadoopJarArgsConst.JARS);
            args.add(StringUtils.join(param.getLibJars().stream().map(p -> p.getRes()).toArray(),","));
        }

        if (param.getFiles() != null && !param.getFiles().isEmpty()) {
            args.add(HadoopJarArgsConst.FILES);
            args.add(StringUtils.join(param.getFiles().stream().map(p -> p.getSymbolicRes()).toArray(), ","));
        }

        if (param.getArchives() != null && !param.getArchives().isEmpty()) {
            args.add(HadoopJarArgsConst.ARCHIVES);
            args.add(StringUtils.join(param.getArchives().stream().map(p -> p.getSymbolicRes()).toArray(), ","));
        }

        if (StringUtils.isNotEmpty(param.getQueue())) {
            args.add(HadoopJarArgsConst.D);
            args.add(HadoopJarArgsConst.QUEUE + "=" + param.getQueue());
        }

        if (StringUtils.isNotEmpty(param.getArgs())){
            args.add(param.getArgs());
        }
        return args;
    }

}

