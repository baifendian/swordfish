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

import com.baifendian.swordfish.common.datasource.DataSourceManager;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.mapper.DataSourceMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.dto.BaseResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Service
public class DatasourceService {

  private static Logger logger = LoggerFactory.getLogger(DatasourceService.class.getName());

  @Autowired
  private DataSourceMapper dataSourceMapper;

  @Autowired
  private ProjectMapper projectMapper;

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
   * @param response
   * @return
   */
  public DataSource createDataSource(User operator, String projectName, String name, String desc, DbType type, String parameter, HttpServletResponse response) {
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    // 没有权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 构建数据源
    DataSource dataSource = new DataSource();
    Date now = new Date();

    dataSource.setName(name);
    dataSource.setDesc(desc);
    dataSource.setOwnerId(operator.getId());
    dataSource.setOwnerName(operator.getName());
    dataSource.setType(type);
    dataSource.setProjectId(project.getId());
    dataSource.setProjectName(project.getName());
    dataSource.setParams(parameter);
    dataSource.setCreateTime(now);
    dataSource.setModifyTime(now);

    try {
      dataSourceMapper.insert(dataSource);
    } catch (DuplicateKeyException e) {
      logger.error("DataSource has exist, can't create again.", e);
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    return dataSource;
  }

  /**
   * 测试一个数据源
   *
   * @param type
   * @param parameter
   * @param response
   * @return
   */
  public BaseResponse testDataSource(DbType type, String parameter, HttpServletResponse response) {
    int status = 0;
    String msg = null;

    try {
      DataSourceManager.getHandler(type, parameter).isConnectable();
    } catch (Exception e) {
      status = 1;
      msg = e.toString();
    }

    return new BaseResponse(status, msg);
  }

  /**
   * 修改一个数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param response
   * @param create      true 表示不存在则创建, false 表示不存在则报错
   * @return
   */
  public DataSource modifyDataSource(User operator, String projectName, String name, String desc, DbType type, String parameter, HttpServletResponse response, boolean create) {
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      if (create) { // 需要创建
        return createDataSource(operator, projectName, name, desc, type, parameter, response);
      }

      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    // 没有权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    DataSource dataSource = new DataSource();
    Date now = new Date();

    dataSource.setName(name);
    dataSource.setDesc(desc);
    dataSource.setOwnerId(operator.getId());
    dataSource.setOwnerName(operator.getName());
    dataSource.setType(type);
    dataSource.setParams(parameter);
    dataSource.setProjectId(project.getId());
    dataSource.setProjectName(project.getName());
    dataSource.setModifyTime(now);

    int count = dataSourceMapper.update(dataSource);
    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    return dataSource;
  }

  /**
   * 删除一个数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  public void deleteDataSource(User operator, String projectName, String name, HttpServletResponse response) {
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    //不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    //没有权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    int count = dataSourceMapper.deleteByProjectAndName(project.getId(), name);
    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    return;
  }

  /**
   * 查看项目下的所有数据源
   *
   * @param operator
   * @param projectName
   * @param response
   * @return
   */
  public List<DataSource> query(User operator, String projectName, HttpServletResponse response) {
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    // 没有权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return dataSourceMapper.getByProjectId(project.getId());
  }

  /**
   * 查询某个具体的数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  public DataSource queryByName(User operator, String projectName, String name, HttpServletResponse response) {
    // 查询项目信息
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    // 没有权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return dataSourceMapper.getByName(project.getId(), name);
  }
}
