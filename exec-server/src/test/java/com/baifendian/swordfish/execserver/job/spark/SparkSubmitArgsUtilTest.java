/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author : liujin
 * @date : 2017-03-10 13:26
 */
public class SparkSubmitArgsUtilTest {
    private SparkParam param;
    @Before
    public void testBefore(){
        String jsonStr = "{   \"mainClass\": \"com.baifendian.spark.WordCount\",   \"mainJar\": {       \"scope\": \"project\",       \"res\": \"spark-wc-examples.jar\"   },   \"args\": \"/user/joe/wordcount/input /user/joe/wordcount/output\",   \"properties\": [{       \"prop\": \"wordcount.case.sensitive\",       \"value\": \"true\"     }, {       \"prop\": \"stopwords\",        \"value\": \"the,who,a,then\"     }   ],   \"files\": [{       \"res\": \"ABC.conf\",       \"alias\": \"aa\"     }, {       \"scope\": \"workflow\",       \"res\": \"conf/HEL.conf\",       \"alias\": \"hh\"     }   ],   \"archives\": [{       \"res\": \"JOB.zip\",       \"alias\": \"jj\"     }   ],   \"libJars\": [{       \"scope\": \"workflow\",        \"res\": \"lib/tokenizer-0.1.jar\"     }   ],   \"driverCores\": 2,   \"driverMemory\": \"2048M\",   \"numExecutors\": 2,   \"executorMemory\": \"4096M\",   \"executorCores\": 2 }";
        param = JsonUtil.parseObject(jsonStr, SparkParam.class);

    }

    @Test
    public void testBuildArgs(){
        List<String> args = SparkSubmitArgsUtil.buildArgs(param);
        String result = "--master yarn --deploy-mode cluster --class com.baifendian.spark.WordCount --driver-cores 2 --driver-memory 2048M --num-executors 2 --executor-cores 2 --executor-memory 4096M --jars lib/tokenizer-0.1.jar --files ABC.conf#aa,conf/HEL.conf#hh --archives JOB.zip#jj --conf \"wordcount.case.sensitive=true\" --conf \"stopwords=the,who,a,then\" spark-wc-examples.jar /user/joe/wordcount/input /user/joe/wordcount/output";
        assertEquals(result, String.join(" ", args));
    }
}
