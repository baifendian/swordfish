package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by caojingwei on 2017/4/3.
 */
public class ProjectFlowMapperTest {
  ProjectFlowMapper projectFlowMapper;

  @Before
  public void before() {
    projectFlowMapper = ConnectionFactory.getSqlSession().getMapper(ProjectFlowMapper.class);
  }

  @Test
  public void testFindByName() {
    ProjectFlow projectFlow = projectFlowMapper.findByName(1,"bdi_base");
    assertTrue(projectFlow!=null);
  }
}
