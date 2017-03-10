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
        String jsonStr = "{   \"mainClass\": \"com.baifendian.mr.WordCount\",   \"mainJar\": {       \"scope\": \"project\",  \"res\": \"wordcount-examples.jar\"   },   \"args\": \"/user/joe/wordcount/input /user/joe/wordcount/output\",   \"properties\": [{       \"prop\": \"wordcount.case.sensitive\",       \"value\": \"true\"     }, {       \"prop\": \"stopwords\",        \"value\": \"the,who,a,then\"     }   ],   \"files\": [{       \"res\": \"ABC.conf\",       \"alias\": \"aa\"     }, {       \"scope\": \"workflow\",   \"res\": \"conf/HEL.conf\",       \"alias\": \"hh\"     }   ],   \"archives\": [{       \"res\": \"JOB.zip\",       \"alias\": \"jj\"     }   ],   \"libJars\": [{       \"scope\": \"workflow\",        \"res\": \"lib/tokenizer-0.1.jar\"     }   ] } \n";
        param = JsonUtil.parseObject(jsonStr, MrParam.class);

    }

    @Test
    public void testBuildArgs(){
        List<String> args = HadoopJarArgsUtil.buildArgs(param);
        String result = "wordcount-examples.jar com.baifendian.mr.WordCount -Dwordcount.case.sensitive=true -Dstopwords=the,who,a,then -files ABC.conf#aa,conf/HEL.conf#hh -libjars lib/tokenizer-0.1.jar -archives JOB.zip#jj /user/joe/wordcount/input /user/joe/wordcount/output";
        assertEquals(result, String.join(" ", args));
    }
}
