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
  public void before() {
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
