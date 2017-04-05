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

import com.baifendian.swordfish.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class StatService {

  private static Logger logger = LoggerFactory.getLogger(StatService.class.getName());

  /**
   * 查询状态信息
   *
   * @param operator
   * @param projectName
   * @param startTime
   * @param endTime
   * @param response
   */
  public void queryStates(User operator, String projectName, long startTime, long endTime, HttpServletResponse response) {

  }

  /**
   * 返回查询排行
   *
   * @param operator
   * @param projectName
   * @param date
   * @param num
   * @param response
   */
  public void queryConsumes(User operator, String projectName, long date, int num, HttpServletResponse response) {

  }

  /**
   * 返回错误的排行信息
   *
   * @param operator
   * @param projectName
   * @param date
   * @param num
   * @param response
   */
  public void queryErrors(User operator, String projectName, long date, int num, HttpServletResponse response) {

  }
}
