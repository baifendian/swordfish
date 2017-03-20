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

import com.baifendian.swordfish.dao.BaseData;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 用户管理的服务入口
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private static Logger logger = LoggerFactory.getLogger(UserController.class.getName());

  @Autowired
  private UserService userService;

  /**
   * 添加用户, "系统管理员" 操作
   *
   * @param operator
   * @param name
   * @param email
   * @param desc
   * @param password
   * @param phone
   * @param proxyUsers
   * @param response
   * @return
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.POST})
  public BaseData createUser(@ModelAttribute(value = "session.user") User operator,
                             @PathVariable String name,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "desc", required = false) String desc,
                             @RequestParam(value = "password") String password,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "proxyUsers") String proxyUsers,
                             HttpServletResponse response) {
    logger.info("Operator user id {}, create user, name: {}, email: {}, desc: {}, password: {}, phone: {}, proxyUsers: {}", operator.getId(),
        name, email, desc, password, phone, proxyUsers);

    BaseData data = userService.createUser(operator, name, email, desc, password, phone, proxyUsers);

    response.setStatus(data.getHttpStatus());

    return data;
  }

  /**
   * 修改用户信息, "系统管理员和用户自己" 操作
   *
   * @param name
   * @param email
   * @param desc
   * @param password
   * @param phone
   * @param proxyUsers: 代理用户信息, 普通用户无权限修改自身代理用户信息
   * @param response
   * @return
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.PATCH})
  public BaseData modifyUser(@PathVariable String name,
                             @RequestParam(value = "session.userId") int userId,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "desc", required = false) String desc,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "proxyUsers", required = false) String proxyUsers,
                             HttpServletResponse response) {
    return null;
  }

  /**
   * 删除用户, "系统管理员" 操作
   *
   * @param name
   * @param response
   * @return
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.DELETE})
  public BaseData deleteUser(@PathVariable String name,
                             @RequestParam(value = "session.userId") int userId,
                             HttpServletResponse response) {
    return null;
  }


  /**
   * 删除用户, "系统管理员" 操作
   *
   * @param name
   * @param response
   * @return
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.GET})
  public BaseData queryUsers(@PathVariable String name,
                             @RequestParam(value = "session.userId") int userId,
                             @RequestParam(value = "allUser", required = false) boolean allUser,
                             HttpServletResponse response) {
    return null;
  }


  @RequestMapping(value = "/{name}/proxyUsers/{proxyUser}", method = {RequestMethod.GET})
  public BaseData modifyProxyUserPass(@PathVariable String name,
                                      @RequestParam(value = "session.userId") int userId,
                                      @RequestParam(value = "allUser", required = false) boolean allUser,
                                      HttpServletResponse response) {
    return null;
  }
}
