/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author : liujin
 * @date : 2017-03-08 8:58
 */
public class HadoopJarArgsUtilTest {
    private MrParam param;

    @Before
    public void testBefore(){
        String jsonStr = "{\"mainJar\":\"hadoop-mapreduce-examples-2.7.3.jar\",\"mainClass\":\"org.apache.hadoop.examples.ExampleDriver\",\"args\":[\"3\",\"5\"],\"properties\":[{\"prop\":\"aa\",\"value\":\"11\"},{\"prop\":\"bb\",\"value\":\"55\"}],\"jars\":[\"hadoop.jar\",\"test.jar\"],\"files\":[\"x.conf\"],\"archives\":null,\"queue\":null,\"dargs\":[\"aa=11\",\"bb=55\"]}";
        param = JsonUtil.parseObject(jsonStr, MrParam.class);

    }

    @Test
    public void testBuildArgs(){
        List<String> args = HadoopJarArgsUtil.buildArgs(param);
        String[] result = {"hadoop-mapreduce-examples-2.7.3.jar", "org.apache.hadoop.examples.ExampleDriver", "3", "5", "-D", "aa=11", "-D", "bb=55", "-libjars", "hadoop.jar,test.jar", "--files", "x.conf"};
        assertEquals(args, Arrays.asList(result));
    }
}
