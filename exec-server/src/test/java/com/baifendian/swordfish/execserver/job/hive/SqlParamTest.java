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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author : liujin
 * @date : 2017-03-15 15:23
 */
public class SqlParamTest {
    private SqlParam param;
    @Before
    public void before(){
        String paramStr = "{\"sql\":\"select count(*) from bfd_test.test;\", \"udfs\":[{ \"func\": \"md4\", \"className\": \"com.baifendian.hive.udf.Md5\", \"libJar\": { \"scope\": \"project\", \"res\": \"udf.jar\" } }]}\n";
        param = JsonUtil.parseObject(paramStr, SqlParam.class);
    }

    @Test
    public void testGetResourceFiles(){
        List<String> resources = param.getResourceFiles();
        String result = "udf.jar";
        assertEquals(result, StringUtils.join(resources, ""));
    }
}
