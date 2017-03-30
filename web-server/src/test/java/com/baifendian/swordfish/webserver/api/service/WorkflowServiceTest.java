package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.RestfulApiApplication;
import com.baifendian.swordfish.webserver.api.service.mock.MockDataService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by caojingwei on 2017/3/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RestfulApiApplication.class)
@WebAppConfiguration
@Transactional
public class WorkflowServiceTest {

  private static Logger logger = LoggerFactory.getLogger(ProjectServiceTest.class.getName());

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private MockDataService mockDataService;

  private User user;

  @Before
  public void setUp() throws Exception {
  }

  public void testCreateWorkflow(){
    {

    }
  }
}
