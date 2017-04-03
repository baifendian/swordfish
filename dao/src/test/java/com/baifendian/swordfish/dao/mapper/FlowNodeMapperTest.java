package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.model.FlowNode;
import org.junit.Before;
import org.junit.Test;

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
    List<FlowNode> flowNodeList = flowNodeMapper.selectByFlowId(1);
    assertTrue(flowNodeList!=null);
  }

}
