/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
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
