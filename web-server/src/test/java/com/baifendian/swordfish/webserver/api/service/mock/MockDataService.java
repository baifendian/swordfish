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
package com.baifendian.swordfish.webserver.api.service.mock;

import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.mapper.*;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.avro.data.Json;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 单元测试模拟数据工具
 */
@Service
public class MockDataService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectUserMapper projectUserMapper;

  @Autowired
  private DataSourceMapper dataSourceMapper;

  @Autowired
  private FlowNodeMapper flowNodeMapper;

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private ScheduleMapper scheduleMapper;

  /**
   * 获取一个随机字符串
   *
   * @return
   */
  public String getRandomString() {
    //return RandomStringUtils.random(10, new char[]{'a', 'b', 'c', 'd', 'e', 'f','g','h','i','j'});
    return getRandomString(5);
  }

  public String getRandomString(int length) {
    //随机字符串的随机字符库
    String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuffer sb = new StringBuffer();
    int len = KeyString.length();
    for (int i = 0; i < length; i++) {
      sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
    }
    return sb.toString();
  }

  /**
   * 创建一个的用户
   *
   * @return
   */
  public User createUser(UserRoleType userRoleType) {
    User user = new User();
    Date now = new Date();

    user.setName(getRandomString());
    user.setPassword(getRandomString());
    user.setDesc(getRandomString());
    user.setEmail(getRandomString());
    user.setPhone(getRandomString());
    user.setRole(userRoleType);
    user.setProxyUsers("*");
    user.setCreateTime(now);
    user.setModifyTime(now);

    userMapper.insert(user);
    return user;
  }

  /**
   * 创建一个普通用户
   *
   * @return
   */
  public User createGeneralUser() {
    return createUser(UserRoleType.GENERAL_USER);
  }

  /**
   * 创建一个管理员用户
   *
   * @return
   */
  public User createAdminUser() {
    return createUser(UserRoleType.ADMIN_USER);
  }

  /**
   * 创建一个项目
   *
   * @param user
   * @return
   */
  public Project createProject(User user) {
    Project project = new Project();
    Date now = new Date();

    project.setName(getRandomString());
    project.setDesc(getRandomString());
    project.setCreateTime(now);
    project.setModifyTime(now);
    project.setOwnerId(user.getId());
    project.setOwner(user.getName());

    projectMapper.insert(project);
    return project;
  }

  /**
   * 创建一个用户项目关系
   *
   * @param projectId
   * @param userId
   * @param perm
   * @return
   */
  public ProjectUser createProjectUser(int projectId, int userId, int perm) {
    ProjectUser projectUser = new ProjectUser();
    Date now = new Date();

    projectUser.setProjectId(projectId);
    projectUser.setUserId(userId);
    projectUser.setPerm(perm);
    projectUser.setCreateTime(now);
    projectUser.setModifyTime(now);
    projectUser.setPerm(0);

    projectUserMapper.insert(projectUser);

    return projectUser;
  }

  /**
   * 创建一个数据源
   *
   * @param projectId
   * @param userId
   * @return
   */
  public DataSource createDataSource(int projectId, int userId) {
    DataSource dataSource = new DataSource();
    Date now = new Date();

    dataSource.setName(getRandomString());
    dataSource.setDesc(getRandomString());
    dataSource.setType(DbType.MYSQL);
    dataSource.setOwnerId(userId);
    dataSource.setProjectId(projectId);
    dataSource.setParameter(getRandomString());
    dataSource.setCreateTime(now);
    dataSource.setModifyTime(now);

    dataSourceMapper.insert(dataSource);

    return dataSource;
  }

  public String MR_PARAMETER = "{\"mainClass\":\"com.baifendian.mr.WordCount\",\"mainJar\":{\"scope\":\"project\",\"res\":\"wordcount-examples.jar\"},\"args\":\"/user/joe/wordcount/input /user/joe/wordcount/output\",\"properties\":[{\"prop\":\"wordcount.case.sensitive\",\"value\":\"true\"},{\"prop\":\"stopwords\",\"value\":\"the,who,a,then\"}],\"files\":[{\"res\":\"ABC.conf\",\"alias\":\"aa\"},{\"scope\":\"workflow\",\"res\":\"conf/HEL.conf\",\"alias\":\"hh\"}],\"archives\":[{\"res\":\"JOB.zip\",\"alias\":\"jj\"}],\"libJars\":[{\"scope\":\"workflow\",\"res\":\"lib/tokenizer-0.1.jar\"}]}";

  public String USER_DEFINED_PARAMETER = "[{\"prop\":\"year\",\"value\":\"$[yyyy]\"}]";

  /**
   * 虚拟一个mr节点
   *
   * @return
   */
  public FlowNode mocNode(String[] depList, int flowId, String parameter, String extras,boolean write) throws JsonProcessingException {
    FlowNode flowNode = new FlowNode();
    flowNode.setName(getRandomString());
    flowNode.setDesc(getRandomString());
    flowNode.setExtras(getRandomString());
    flowNode.setFlowId(flowId);
    flowNode.setParameter(parameter);
    flowNode.setType("MR");
    flowNode.setDepList(Arrays.asList(depList));
    flowNode.setExtras(extras);

    if(write){
      flowNodeMapper.insert(flowNode);
    }
    return flowNode;
  }

  /**
   * 虚拟一个正常的MR节点 并写入数据库
   *
   * @return
   */
  public FlowNode mocRmNode(String[] depList, int flowId) throws JsonProcessingException {
    return mocNode(depList, flowId, MR_PARAMETER, MR_PARAMETER,true);
  }

  /**
   * 虚拟一个RM节点，但是不写入数据库
   * @param depList
   * @param flowId
   * @return
   * @throws JsonProcessingException
   */
  public FlowNode mocRmNodeJson(String[] depList, int flowId) throws JsonProcessingException {
    return mocNode(depList, flowId, MR_PARAMETER, MR_PARAMETER,false);
  }



  /**
   * 虚拟一个projectFlowData
   *
   * @return
   */
  public ProjectFlow.ProjectFlowData mocProjectFlowData(int flowId) throws IOException {
    FlowNode flowNode1 = mocRmNodeJson(new String[]{}, flowId);
    FlowNode flowNode2 = mocRmNodeJson(new String[]{flowNode1.getName()}, flowId);
    FlowNode flowNode3 = mocRmNodeJson(new String[]{flowNode2.getName()}, flowId);

    List<FlowNode> flowNodeList = Arrays.asList(new FlowNode[]{flowNode1, flowNode2, flowNode3});

    ProjectFlow.ProjectFlowData projectFlowData = new ProjectFlow.ProjectFlowData();
    projectFlowData.setExtras(MR_PARAMETER);
    projectFlowData.setUserDefParams(Arrays.asList(new Property[]{new Property("year","$[yyyy]")}));
    projectFlowData.setNodes(flowNodeList);

    return projectFlowData;
  }

  /**
   * 虚拟一个projectFlowData 字符串
   *
   * @param flowId
   * @return
   */
  public String mocProjectFlowDataJson(int flowId) throws IOException {
    ProjectFlow.ProjectFlowData projectFlowData = mocProjectFlowData(flowId);
    return JsonUtil.toJsonString(projectFlowData);
  }

  /**
   * 虚拟一个 正常pojectFlow 工作流
   *
   * @return
   */
  public ProjectFlow mocProjectFlow(int projectId, int userId) throws JsonProcessingException {
    ProjectFlow projectFlow = new ProjectFlow();
    Date now = new Date();

    projectFlow.setName(getRandomString());
    projectFlow.setProjectId(projectId);
    projectFlow.setDesc(getRandomString());
    projectFlow.setCreateTime(now);
    projectFlow.setModifyTime(now);
    projectFlow.setProxyUser(getRandomString());
    projectFlow.setQueue(getRandomString());
    projectFlow.setOwnerId(userId);
    projectFlow.setUserDefinedParams(USER_DEFINED_PARAMETER);
    projectFlow.setExtras(MR_PARAMETER);

    projectFlowMapper.insertAndGetId(projectFlow);

    FlowNode flowNode1 = mocRmNode(new String[]{}, projectFlow.getId());
    FlowNode flowNode2 = mocRmNode(new String[]{flowNode1.getName()}, projectFlow.getId());
    FlowNode flowNode3 = mocRmNode(new String[]{flowNode2.getName()}, projectFlow.getId());

    List<FlowNode> flowNodeList = Arrays.asList(new FlowNode[]{flowNode1, flowNode2, flowNode3});

    projectFlow.setFlowsNodes(flowNodeList);

    return projectFlow;
  }

  /**
   * 虚拟一个Schedule
   * @return
   */
  public Schedule mockSchedule(String projectName,int flowId,int userId) throws IOException {
    Schedule schedule = new Schedule();
    Date now = new Date();
    schedule.setFlowId(flowId);
    schedule.setStartDate(now);
    schedule.setEndDate(now);
    schedule.setCrontab("0 8 * * * * ?");
    schedule.setNotifyType(NotifyType.FAILURE);
    schedule.setNotifyMailsStr(JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"})));
    schedule.setMaxTryTimes(2);
    schedule.setFailurePolicy(FailurePolicyType.END);
    schedule.setDepWorkflowsStr(JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(projectName,getRandomString())})));
    schedule.setDepPolicy(DepPolicyType.NO_DEP_PRE);
    schedule.setTimeout(3600);
    schedule.setOwnerId(userId);
    schedule.setCreateTime(now);
    schedule.setModifyTime(now);
    schedule.setScheduleStatus(ScheduleStatus.OFFLINE);
    scheduleMapper.insert(schedule);
    return schedule;
  }
}
