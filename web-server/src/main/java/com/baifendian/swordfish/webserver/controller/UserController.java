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

import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.UserDto;
import com.baifendian.swordfish.webserver.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
   * @return
   */
  @PostMapping(value = "/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto createUser(@RequestAttribute(value = "session.user") User operator,
                            @PathVariable String name,
                            @RequestParam(value = "email") String email,
                            @RequestParam(value = "desc", required = false) String desc,
                            @RequestParam(value = "password") String password,
                            @RequestParam(value = "phone", required = false) String phone,
                            @RequestParam(value = "proxyUsers") String proxyUsers) {
    logger.info("Operator user {}, create user, name: {}, email: {}, desc: {}, password: {}, phone: {}, proxyUsers: {}",
        operator.getName(), name, email, desc, "******", phone, proxyUsers);

    return new UserDto(userService.createUser(operator, name, email, desc, password, phone, proxyUsers));
  }

  /**
   * 修改用户信息, "系统管理员和用户自己" 操作
   *
   * @param operator
   * @param name
   * @param email
   * @param desc
   * @param password
   * @param phone
   * @param proxyUsers: 代理用户信息, 普通用户无权限修改自身代理用户信息
   * @return
   */
  @PatchMapping(value = "/{name}")
  public UserDto modifyUser(@RequestAttribute(value = "session.user") User operator,
                            @PathVariable String name,
                            @RequestParam(value = "email", required = false) String email,
                            @RequestParam(value = "desc", required = false) String desc,
                            @RequestParam(value = "password", required = false) String password,
                            @RequestParam(value = "phone", required = false) String phone,
                            @RequestParam(value = "proxyUsers", required = false) String proxyUsers) {
    logger.info("Operator user {}, modify user, name: {}, email: {}, desc: {}, password: {}, phone: {}, proxyUsers: {}",
        operator.getName(), name, email, desc, "******", phone, proxyUsers);

    return new UserDto(userService.modifyUser(operator, name, email, desc, password, phone, proxyUsers));
  }

  /**
   * 删除用户, "系统管理员" 操作
   *
   * @param operator
   * @param name
   * @return
   */
  @DeleteMapping(value = "/{name}")
  public void deleteUser(@RequestAttribute(value = "session.user") User operator,
                         @PathVariable String name) {
    logger.info("Operator user {}, delete user, name: {}",
        operator.getName(), name);

    userService.deleteUser(operator, name);
  }

  /**
   * 查询用户
   *
   * @param operator
   * @param allUser
   * @return
   */
  @GetMapping(value = "")
  public List<UserDto> queryUsers(@RequestAttribute(value = "session.user") User operator,
                                  @RequestParam(value = "allUser", required = false, defaultValue = "false") boolean allUser) {
    logger.info("Operator user {}, query user, allUser: {}",
        operator.getName(), allUser);

    List<User> userList = userService.queryUser(operator, allUser);
    List<UserDto> userDtoList = new ArrayList<>();

    for (User user : userList) {
      userDtoList.add(new UserDto(user));
    }

    return userDtoList;
  }
}
