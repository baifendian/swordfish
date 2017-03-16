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
  public void before() {
    String paramStr = "{\"sql\":\"select count(*) from bfd_test.test;\", \"udfs\":[{ \"func\": \"md4\", \"className\": \"com.baifendian.hive.udf.Md5\", \"libJar\": { \"scope\": \"project\", \"res\": \"udf.jar\" } }]}\n";
    param = JsonUtil.parseObject(paramStr, SqlParam.class);
  }

  @Test
  public void testGetResourceFiles() {
    List<String> resources = param.getResourceFiles();
    String result = "udf.jar";
    assertEquals(result, StringUtils.join(resources, ""));
  }
}
