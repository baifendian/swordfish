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

import com.baifendian.swordfish.common.job.struct.datasource.*;
import com.baifendian.swordfish.common.job.struct.datasource.conn.*;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.mapper.DataSourceMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.BaseStatusDto;
import com.baifendian.swordfish.webserver.exception.NotFoundException;
import com.baifendian.swordfish.webserver.exception.NotModifiedException;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import com.baifendian.swordfish.webserver.exception.PermissionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
   * @return
   */
  public DataSource createDataSource(User operator, String projectName, String name, String desc, DbType type, String parameter) {
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 没有权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), projectName);
    }

    //TODO parameter参数检测

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
      throw new NotModifiedException("DataSource has exist, can't create again.");
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

    try {
      TryConn tryConn = null;
      switch (type) {
        case FTP: {
          FtpParam ftpParam = JsonUtil.parseObject(parameter, FtpParam.class);
          tryConn = new FtpTryConn(ftpParam);
          break;
        }
        case HBASE: {
          HBaseParam hBaseParam = JsonUtil.parseObject(parameter, HBaseParam.class);
          tryConn = new HBaseTryConn(hBaseParam);
          break;
        }
        case MYSQL: {
          MysqlParam mysqlParam = JsonUtil.parseObject(parameter, MysqlParam.class);
          tryConn = new MysqlTryConn(mysqlParam);
          break;
        }
        case ORACLE: {
          OracleParam oracleParam = JsonUtil.parseObject(parameter, OracleParam.class);
          tryConn = new OracleTryConn(oracleParam);
          break;
        }
        case MONGODB: {
          MongoDBParam mongoDBParam = JsonUtil.parseObject(parameter, MongoDBParam.class);
          tryConn = new MongoDBTryConn(mongoDBParam);
          break;
        }
        default: {
          throw new ParameterException("db type \"{0}\" is not support", type.name());
        }
      }
      tryConn.isConnectable();
    } catch (JsonProcessingException jsonProcessingException) {
      throw new ParameterException("Parameter \"{0}\" is not valid");
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
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 没有权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), projectName);
    }

    // 查找指定数据源

    DataSource dataSource = dataSourceMapper.getByName(project.getId(), name);
    Date now = new Date();

    if (dataSource == null) {
      throw new NotFoundException("Not found datasource \"{0}\"", name);
    }

    if (!StringUtils.isEmpty(desc)) {
      dataSource.setDesc(desc);
    }

    if (!StringUtils.isEmpty(parameter)) {
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
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    //不存在的项目名
    if (project == null) {
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    //没有权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), projectName);
    }

    int count = dataSourceMapper.deleteByProjectAndName(project.getId(), name);
    if (count <= 0) {
      throw new NotModifiedException("Not delete project count");
    }

    return;
  }

  /**
   * 查看项目下的所有数据源
   *
   * @param operator
   * @param projectName
   * @return
   */
  public List<DataSource> query(User operator, String projectName) {
    // 查询项目
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 没有权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), projectName);
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
    // 查询项目信息
    Project project = projectMapper.queryByName(projectName);

    // 不存在的项目名
    if (project == null) {
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 没有权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), projectName);
    }

    return dataSourceMapper.getByName(project.getId(), name);
  }
}
