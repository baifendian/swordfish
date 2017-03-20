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

package com.baifendian.swordfish.execserver.job.upload;

import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.junit.Test;

/**
 * @author : liujin
 * @date : 2017-03-17 11:37
 */
public class UploadParamTest {

  @Test
  public void testParse(){
    String jsonStr = "{\"file\":\"country.txt\",\"separator\":\";\",\"coding\":\"UTF-8\",\"hasTitle\":true,\"targetDB\":\"bfd_test\",\"targetTable\":\"country\",\"mappingRelation\":[{\"originFieldIndex\":0,\"targetField\":\"name\"},{\"originFieldIndex\":1,\"targetField\":\"province\"}],\"writerMode\":\"insert\"}\n";
    UploadParam param = JsonUtil.parseObject(jsonStr, UploadParam.class);
    System.out.println(param);
  }
}
