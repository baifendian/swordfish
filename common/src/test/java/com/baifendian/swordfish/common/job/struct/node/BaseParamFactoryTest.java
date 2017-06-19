package com.baifendian.swordfish.common.job.struct.node;

import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by caojingwei on 2017/6/12.
 */
public class BaseParamFactoryTest {
  @Test
  public void testGetBaseParam() {
    String parameter = "{\"type\":\"MYSQL_TO_HDFS\",\"reader\":{\"column\":[\"`id`\",\"`name`\",\"`email`\",\"`desc`\",\"`phone`\",\"`password`\",\"`role`\",\"`proxy_users`\",\"`create_time`\",\"`modify_time`\"],\"datasource\":\"test11111\",\"table\":[\"user\"]},\"writer\":{\"path\":\"/test/temp/here\",\"fileName\":\"filetest\",\"writeMode\":\"APPEND\",\"fileType\":\"ORC\",\"column\":[{\"name\":\"`id`\",\"type\":\"VARCHAR\"},{\"name\":\"`name`\",\"type\":\"VARCHAR\"},{\"name\":\"`email`\",\"type\":\"VARCHAR\"},{\"name\":\"`desc`\",\"type\":\"VARCHAR\"},{\"name\":\"`phone`\",\"type\":\"VARCHAR\"},{\"name\":\"`password`\",\"type\":\"VARCHAR\"},{\"name\":\"`role`\",\"type\":\"VARCHAR\"},{\"name\":\"`proxy_users`\",\"type\":\"VARCHAR\"},{\"name\":\"`create_time`\",\"type\":\"VARCHAR\"},{\"name\":\"`modify_time`\",\"type\":\"VARCHAR\"}]},\"setting\":{\"speed\":{\"channel\":1,\"byte\":104857600},\"errorLimit\":{\"record\":3,\"percentage\":0.05}}}";
    String type = "IMPEXP";

    ImpExpParam impExpParam = (ImpExpParam) BaseParamFactory.getBaseParam(type, parameter);

    assertTrue(impExpParam.checkValid());
  }
}
