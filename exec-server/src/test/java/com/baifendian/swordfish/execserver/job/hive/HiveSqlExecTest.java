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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : liujin
 * @date : 2017-03-08 11:36
 */
public class HiveSqlExecTest {

    @Test
    public void testExecuteQueue() throws Exception {
        List<String> sqls = new ArrayList<>();
        sqls.add("select count(*) from test");
        Logger logger = LoggerFactory.getLogger(HiveSqlExecTest.class);
        HiveSqlExec hiveSqlExec = new HiveSqlExec(sqls, "hadoop", null, false, logger);
        hiveSqlExec.run();
        System.out.println(hiveSqlExec.getResults());
    }

}
