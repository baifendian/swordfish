/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author : liujin
 * @date : 2017-03-15 11:23
 */
public class FunctionUtilTest {
    private List<UdfsInfo> udfsInfos;

    @Before
    public void before(){
        udfsInfos = new ArrayList<>();
        String udfDefine = "{ \"func\": \"upper\", \"className\": \"com.baifendian.example.UpperUtils\", \"libJar\": { \"scope\": \"project\", \"res\": \"upper-0.1.jar\" } } ";
        UdfsInfo udfsInfo = JsonUtil.parseObject(udfDefine, UdfsInfo.class);
        udfsInfos.add(udfsInfo);
    }

    /*
    @Test
    public void testCreateFuncs() throws IOException, InterruptedException {
        List<String> funcs = FunctionUtil.createFuncs(udfsInfos, "JOB_11");
        String result = "add jar upper-0.1.jar;create temporary function upper as 'com.baifendian.example.UpperUtils'";
        assertEquals(result, StringUtils.join(funcs, ";"));
    }
    */
}
