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
package com.baifendian.swordfish.dao.mapper;

import static junit.framework.TestCase.assertTrue;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import org.junit.Before;
import org.junit.Test;

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
