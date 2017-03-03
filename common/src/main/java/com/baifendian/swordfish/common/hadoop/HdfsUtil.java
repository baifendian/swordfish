package com.baifendian.swordfish.common.hadoop;

import com.baifendian.swordfish.common.job.exception.ExecException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author : liujin
 * @date : 2017-03-03 16:59
 */
public class HdfsUtil {

    /**
     * 将hdfs上的文件或目录下载到本地
     * @param src hdfs目录
     * @param dst 本地目录
     */
    public static void GetFile(String src, String dst) throws IOException, InterruptedException, ExecException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String cmd = String.format("hdfs dfs -get %s %s", src, dst);
        processBuilder.command(cmd);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int ret = process.waitFor();
        if(ret != 0){
            throw new ExecException(String.format("call cmd %s error, %s", cmd, IOUtils.toString(process.getInputStream(), "UTF-8")));
        }
    }
}
