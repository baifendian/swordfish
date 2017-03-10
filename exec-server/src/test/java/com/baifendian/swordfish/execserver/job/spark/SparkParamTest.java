/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author : liujin
 * @date : 2017-03-10 14:28
 */
public class SparkParamTest {

    private SparkParam param;
    @Before
    public void testBefore(){
        String jsonStr = "{   \"mainClass\": \"com.baifendian.spark.WordCount\",   \"mainJar\": {       \"scope\": \"project\",       \"res\": \"spark-wc-examples.jar\"   },   \"args\": \"/user/joe/wordcount/input /user/joe/wordcount/output\",   \"properties\": [{       \"prop\": \"wordcount.case.sensitive\",       \"value\": \"true\"     }, {       \"prop\": \"stopwords\",        \"value\": \"the,who,a,then\"     }   ],   \"files\": [{       \"res\": \"ABC.conf\",       \"alias\": \"aa\"     }, {       \"scope\": \"workflow\",       \"res\": \"conf/HEL.conf\",       \"alias\": \"hh\"     }   ],   \"archives\": [{       \"res\": \"JOB.zip\",       \"alias\": \"jj\"     }   ],   \"libJars\": [{       \"scope\": \"workflow\",        \"res\": \"lib/tokenizer-0.1.jar\"     }   ],   \"driverCores\": 2,   \"driverMemory\": \"2048M\",   \"numExecutors\": 2,   \"executorMemory\": \"4096M\",   \"executorCores\": 2 }";
        param = JsonUtil.parseObject(jsonStr, SparkParam.class);
    }

    @Test
    public void testGetResourceFiles(){
        List<String> resFiles = param.getResourceFiles();
        String result = "[spark-wc-examples.jar, ABC.conf, JOB.zip]";
        assertEquals(result, resFiles.toString());
    }
}
