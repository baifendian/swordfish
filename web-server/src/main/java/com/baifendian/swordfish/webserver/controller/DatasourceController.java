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
package com.baifendian.swordfish.webserver.controller;

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.DatasourceDto;
import com.baifendian.swordfish.webserver.service.DatasourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源的服务入口
 */
@RestController
@RequestMapping("/projects/{projectName}/datasources")
public class DatasourceController {

  private static Logger logger = LoggerFactory.getLogger(DatasourceController.class.getName());

  @Autowired
  private DatasourceService datasourceService;

  /**
   * 创建一个数据源
   *
   * @param operator
   * @param name
   * @param projectName
   * @param desc
   * @param type
   * @param parameter
   * @return
   */
  @PostMapping(value = "/{name}")
  public DatasourceDto createDataSource(@RequestAttribute(value = "session.user") User operator,
                                        @PathVariable("projectName") String projectName,
                                        @PathVariable("name") String name,
                                        @RequestParam(value = "desc", required = false) String desc,
                                        @RequestParam(value = "type") DbType type,
                                        @RequestParam(value = "parameter") String parameter) {
    logger.info("Operator user {}, create datasource, project name: {}, data source name: {}, desc: {}, type: {}, parameter: {}",
            operator.getName(), projectName, name, desc, type, parameter);

    return new DatasourceDto(datasourceService.createDataSource(operator, projectName, name, desc, type, parameter));
  }

  /**
   * 修改并增加数据源
   *
   * @param operator
   * @param name
   * @param projectName
   * @param desc
   * @param type
   * @param parameter
   * @return
   */
  @PutMapping(value = "/{name}")
  public DatasourceDto putDataSource(@RequestAttribute(value = "session.user") User operator,
                                     @PathVariable("projectName") String projectName,
                                     @PathVariable("name") String name,
                                     @RequestParam(value = "desc", required = false) String desc,
                                     @RequestParam(value = "type") DbType type,
                                     @RequestParam(value = "parameter") String parameter) {
    logger.info("Operator user {}, put datasource, project name: {}, data source name: {}, desc: {}, type: {}, parameter: {}",
            operator.getName(), projectName, name, desc, type, parameter);

    return new DatasourceDto(datasourceService.putDataSource(operator, projectName, name, desc, type, parameter));
  }

  /**
   * 修改一个数据源
   *
   * @param operator
   * @param name
   * @param projectName
   * @param desc
   * @param parameter
   * @return
   */
  @PatchMapping(value = "/{name}")
  public DatasourceDto modifyDataSource(@RequestAttribute(value = "session.user") User operator,
                                        @PathVariable("projectName") String projectName,
                                        @PathVariable("name") String name,
                                        @RequestParam(value = "desc", required = false) String desc,
                                        @RequestParam(value = "parameter") String parameter) {
    logger.info("Operator user {}, modify datasource, project name: {}, data source name: {}, desc: {}, type: {}, parameter: {}",
            operator.getName(), projectName, name, desc, parameter);

    return new DatasourceDto(datasourceService.modifyDataSource(operator, projectName, name, desc, parameter));
  }

  /**
   * 删除一个数据源
   *
   * @param operator
   * @param projectName
   * @param name
   */
  @DeleteMapping(value = "/{name}")
  public void deleteDataSource(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable("projectName") String projectName,
                               @PathVariable("name") String name) {
    logger.info("Operator user {}, delete datasource, project name: {}, data source name: {}",
            operator.getName(), projectName, name);

    datasourceService.deleteDataSource(operator, projectName, name);
  }

  /**
   * 查看一个项目下的所有数据源
   *
   * @param operator
   * @param projectName
   * @return
   */
  @GetMapping(value = "")
  public List<DatasourceDto> query(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable("projectName") String projectName) {
    logger.info("Operator user {}, query datasource of project, project name: {}",
            operator.getName(), projectName);

    List<DataSource> dataSourceList = datasourceService.query(operator, projectName);
    List<DatasourceDto> datasourceDtoList = new ArrayList<>();

    for (DataSource dataSource : dataSourceList) {
      datasourceDtoList.add(new DatasourceDto(dataSource));
    }

    return datasourceDtoList;
  }

  /**
   * 查询某个具体的数据源
   *
   * @param operator
   * @param projectName
   * @param name
   * @return
   */
  @GetMapping(value = "/{name}")
  public DatasourceDto queryByName(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable("projectName") String projectName,
                                   @PathVariable("name") String name) {
    logger.info("Operator user {}, query datasource, project name: {}, data source name: {}",
            operator.getName(), projectName, name);

    return new DatasourceDto(datasourceService.queryByName(operator, projectName, name));
  }
}
