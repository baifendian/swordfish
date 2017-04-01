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
package com.baifendian.swordfish.webserver.api.controller;

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.dto.BaseResponse;
import com.baifendian.swordfish.webserver.api.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据源的服务入口
 */
@RestController
@RequestMapping("/projects/{projectName}/datasources")
public class DatasourceController {

  @Autowired
  private DatasourceService datasourceService;

  /**
   * 创建一个数据源
   * @param operator
   * @param name
   * @param projectName
   * @param desc
   * @param type
   * @param parameter
   * @param response
   * @return
   */
  @PostMapping(value="/{name}")
  public DataSource createDataSource(@RequestAttribute(value = "session.user") User operator,
                                     @PathVariable("name") String name,
                                     @PathVariable("projectName") String projectName,
                                     @RequestParam(value = "desc", required = false) String desc,
                                     @RequestParam(value = "type", required = true) DbType type,
                                     @RequestParam(value = "parameter", required = true) String parameter,
                                     HttpServletResponse response){
    return datasourceService.createDataSource(operator,projectName,name,desc,type,parameter,response);
  }



  /**
   * 修改一个数据源
   * @param operator
   * @param name
   * @param projectName
   * @param desc
   * @param type
   * @param parameter
   * @param response
   * @return
   */
  @PatchMapping(value="/{name}")
  public DataSource modifyDataSource(@RequestAttribute(value = "session.user") User operator,
                                     @PathVariable("name") String name,
                                     @PathVariable("projectName") String projectName,
                                     @RequestParam(value = "desc", required = false) String desc,
                                     @RequestParam(value = "type", required = true) DbType type,
                                     @RequestParam(value = "parameter", required = true) String parameter,
                                     HttpServletResponse response){
    return datasourceService.modifyDataSource(operator,projectName,name,desc,type,parameter,response);
  }

  /**
   * 删除一个数据源
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  @DeleteMapping(value = "/{name}")
  public void deleteDataSource(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable("projectName") String projectName,
                               @PathVariable("name") String name,
                               HttpServletResponse response
                               ){
    datasourceService.deleteDataSource(operator,projectName,name,response);
  }

  /**
   * 查看一个项目下的所有数据源
   * @param operator
   * @param projectName
   * @param response
   * @return
   */
  @GetMapping(value = "")
  public List<DataSource> query(@RequestAttribute(value = "session.user") User operator,
                                @PathVariable("projectName") String projectName,
                                HttpServletResponse response){
    return datasourceService.query(operator,projectName,response);
  }

  /**
   * 查询某个具体的数据源
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  @GetMapping(value = "/{name}")
  public DataSource queryByName(@RequestAttribute(value = "session.user") User operator,
                                @PathVariable("projectName") String projectName,
                                @PathVariable("name") String name,
                                HttpServletResponse response){
    return datasourceService.queryByName(operator,projectName,name,response);
  }
}
