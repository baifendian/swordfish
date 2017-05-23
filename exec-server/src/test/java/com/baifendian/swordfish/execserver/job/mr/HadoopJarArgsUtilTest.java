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
package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.struct.node.mr.MrParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author : liujin
 * @date : 2017-03-08 8:58
 */
public class HadoopJarArgsUtilTest {
  private MrParam param;

  @Before
  public void testBefore() {
    String jsonStr = "{   \"mainClass\": \"com.baifendian.mr.WordCount\",   \"mainJar\": {       \"scope\": \"project\",  \"res\": \"wordcount-examples.jar\"   },   \"args\": \"/user/joe/wordcount/input /user/joe/wordcount/output\",   \"properties\": [{       \"prop\": \"wordcount.case.sensitive\",       \"value\": \"true\"     }, {       \"prop\": \"stopwords\",        \"value\": \"the,who,a,then\"     }   ],   \"files\": [{       \"res\": \"ABC.conf\",       \"alias\": \"aa\"     }, {       \"scope\": \"workflow\",   \"res\": \"conf/HEL.conf\",       \"alias\": \"hh\"     }   ],   \"archives\": [{       \"res\": \"JOB.zip\",       \"alias\": \"jj\"     }   ],   \"libJars\": [{       \"scope\": \"workflow\",        \"res\": \"lib/tokenizer-0.1.jar\"     }   ] } \n";
    param = JsonUtil.parseObject(jsonStr, MrParam.class);

  }

  @Test
  public void testBuildArgs() {
    List<String> args = HadoopJarArgsUtil.buildArgs(param);
    String result = "wordcount-examples.jar com.baifendian.mr.WordCount -Dwordcount.case.sensitive=true -Dstopwords=the,who,a,then -files ABC.conf#aa,conf/HEL.conf#hh -libjars lib/tokenizer-0.1.jar -archives JOB.zip#jj /user/joe/wordcount/input /user/joe/wordcount/output";
    assertEquals(result, String.join(" ", args));
  }
}
