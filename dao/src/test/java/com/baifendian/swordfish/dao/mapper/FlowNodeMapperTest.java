package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by caojingwei on 2017/4/3.
 */
public class FlowNodeMapperTest {
  FlowNodeMapper flowNodeMapper;

  @Before
  public void before() {
    flowNodeMapper = ConnectionFactory.getSqlSession().getMapper(FlowNodeMapper.class);
  }

  @Test
  public void testSelectByFlowId() {
    List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowId(2);
    assertTrue(flowNodeList!=null);
  }

  @Test
  public void testSelectByFlowIds() {
    List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowIds(Arrays.asList(new Integer[]{1,2,3}));
  }

  @Test
  public void testInsert() throws JsonProcessingException {
    FlowNode flowNode = new FlowNode();
    flowNode.setName("shelljob4");
    flowNode.setFlowId(2);
    flowNode.setDesc("shelljob4");
    flowNode.setType("SHELL");
    flowNode.setParameter("{\"script\":\"echo shelljob4\"}");
    flowNode.setDepList(new ArrayList<String>());
    flowNodeMapper.insert(flowNode);
  }

}
