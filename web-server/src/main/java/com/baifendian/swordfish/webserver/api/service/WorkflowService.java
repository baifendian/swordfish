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
package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class WorkflowService {

  private static Logger logger = LoggerFactory.getLogger(WorkflowService.class.getName());

  public ProjectFlow createWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file){

    List<FlowNode> flowNodeList;

    try{
      ObjectMapper mapper = new ObjectMapper();
      //flowNodeList = mapper.readValues(data, TypeFactory.defaultInstance(List.class,FlowNode.class));
    }catch (Exception e){

    }

    return null;
  }
}
