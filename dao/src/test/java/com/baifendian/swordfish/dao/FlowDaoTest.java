/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ExecutionNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : liujin
 * @date : 2017-03-14 9:06
 */
public class FlowDaoTest {
    FlowDao flowDao;

    @Before
    public void before(){
        flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    }

    @Test
    public void testQueryExecutionNodeLastAttempt(){
        ExecutionNode executionNode = flowDao.queryExecutionNodeLastAttempt(411, 6);
        System.out.println(executionNode.getStatus());
    }

    @Test
    public void testQueryAllExecutionFlow(){
        List<ExecutionFlow> executionNodeList = flowDao.queryAllNoFinishFlow();
        System.out.println(executionNodeList.size());
    }
}
