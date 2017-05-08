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
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.BaseResponse;
import com.baifendian.swordfish.webserver.service.DatasourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/datasources")
public class DatasourceTestController {

  private static Logger logger = LoggerFactory.getLogger(DatasourceTestController.class.getName());

  @Autowired
  private DatasourceService datasourceService;

  /**
   * 测试一个数据源
   *
   * @param operator
   * @param type
   * @param parameter
   * @return
   */
  @GetMapping(value = "/test")
  public BaseResponse testDataSource(@RequestAttribute(value = "session.user") User operator,
                                     @RequestParam(value = "type") DbType type,
                                     @RequestParam(value = "parameter") String parameter) {
    logger.info("Operator user id {}, test datasource, type: {}, parameter: {}",
        operator.getId(), type, parameter);

    return datasourceService.testDataSource(type, parameter);
  }
}
