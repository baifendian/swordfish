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
package com.baifendian.swordfish.webserver.service;


import com.baifendian.swordfish.common.job.struct.datasource.Datasource;
import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.mapper.DataSourceMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.BaseStatusDto;
import com.baifendian.swordfish.webserver.exception.NotFoundException;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import com.baifendian.swordfish.webserver.exception.PermissionException;
import com.baifendian.swordfish.webserver.exception.ServerErrorException;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class DatasourceService {

  private static Logger logger = LoggerFactory.getLogger(DatasourceService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private DataSourceMapper dataSourceMapper;

  @Autowired
  private ProjectService projectService;

  /**
   * 创建数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @return
   */
  public DataSource createDataSource(User operator, String projectName, String name, String desc, DbType type, String parameter) {

    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须要有用户写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 序列化数据源参数对象
    Datasource datasource = DatasourceFactory.getDatasource(type, parameter);
    if (datasource == null) {
      throw new ParameterException("Parameter \"{0}\" is not valid", parameter);
    }


    // 构建数据源
    DataSource dataSource = new DataSource();
    try {
      Date now = new Date();

      dataSource.setName(name);
      dataSource.setDesc(desc);
      dataSource.setOwnerId(operator.getId());
      dataSource.setOwnerName(operator.getName());
      dataSource.setType(type);
      dataSource.setProjectId(project.getId());
      dataSource.setProjectName(project.getName());
      dataSource.setParameter(parameter);
      dataSource.setCreateTime(now);
      dataSource.setModifyTime(now);
    } catch (Exception e) {
      logger.error("Datasource set value error", e);
      throw new ParameterException("Datasource set value error ");
    }

    try {
      dataSourceMapper.insert(dataSource);
    } catch (DuplicateKeyException e) {
      logger.error("DataSource has exist, can't create again.", e);
      throw new ServerErrorException("DataSource has exist, can't create again.");
    }

    return dataSource;
  }

  /**
   * 测试一个数据源
   *
   * @param type
   * @param parameter
   * @return
   */
  public BaseStatusDto testDataSource(DbType type, String parameter) {
    int status = 0;
    String msg = null;

    // 序列化数据源参数对象
    Datasource datasource = DatasourceFactory.getDatasource(type, parameter);
    if (datasource == null) {
      throw new ParameterException("Parameter \"{0}\" is not valid", parameter);
    }

    try {
      datasource.isConnectable();
    } catch (Exception e) {
      status = 1;
      msg = e.toString();
    }

    return new BaseStatusDto(status, msg);
  }

  /**
   * put 数据源, 不存在则创建
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @return
   */
  public DataSource putDataSource(User operator, String projectName, String name, String desc, DbType type, String parameter) {
    DataSource dataSource = dataSourceMapper.getByProjectNameAndName(projectName, name);

    if (dataSource == null) {
      return createDataSource(operator, projectName, name, desc, type, parameter);
    }

    return modifyDataSource(operator, projectName, name, desc, parameter);
  }

  /**
   * 修改一个数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param parameter
   * @return
   */
  public DataSource modifyDataSource(User operator, String projectName, String name, String desc, String parameter) {

    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须要有 project 写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 查找指定数据源
    DataSource dataSource = dataSourceMapper.getByName(project.getId(), name);
    if (dataSource == null) {
      throw new NotFoundException("Not found datasource \"{0}\" in project \"{1}\"", name, project.getName());
    }

    Date now = new Date();

    if (!StringUtils.isEmpty(desc)) {
      dataSource.setDesc(desc);
    }

    if (!StringUtils.isEmpty(parameter)) {
      Datasource datasource = DatasourceFactory.getDatasource(dataSource.getType(), parameter);
      if (datasource == null) {
        throw new ParameterException("Parameter \"{0}\" is not valid", parameter);
      }
      dataSource.setParameter(parameter);
    }

    dataSource.setModifyTime(now);
    dataSource.setOwnerId(operator.getId());
    dataSource.setOwnerName(operator.getName());

    dataSourceMapper.update(dataSource);

    return dataSource;
  }

  /**
   * 删除一个数据源
   *
   * @param operator
   * @param projectName
   * @param name
   */
  public void deleteDataSource(User operator, String projectName, String name) {

    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 查找指定数据源
    DataSource dataSource = dataSourceMapper.getByName(project.getId(), name);
    if (dataSource == null) {
      throw new NotFoundException("Not found datasource \"{0}\" in project \"{1}\"", name, project.getName());
    }

    int count = dataSourceMapper.deleteByProjectAndName(project.getId(), name);
    if (count <= 0) {
      throw new ServerErrorException("Not delete project count");
    }
  }

  /**
   * 查看项目下的所有数据源
   *
   * @param operator
   * @param projectName
   * @return
   */
  public List<DataSource> query(User operator, String projectName) {

    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    return dataSourceMapper.getByProjectId(project.getId());
  }

  /**
   * 查询某个具体的数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @return
   */
  public DataSource queryByName(User operator, String projectName, String name) {

    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须要有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    return dataSourceMapper.getByName(project.getId(), name);
  }

}
