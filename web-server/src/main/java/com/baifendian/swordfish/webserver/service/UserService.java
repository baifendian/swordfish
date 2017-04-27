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

import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.UserDto;
import com.baifendian.swordfish.webserver.exception.NotFoundException;
import com.baifendian.swordfish.webserver.exception.NotModifiedException;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import com.baifendian.swordfish.webserver.exception.PermissionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
  private static Logger logger = LoggerFactory.getLogger(UserService.class.getName());

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private ProjectMapper projectMapper;

  /**
   * 创建用户, 只有系统管理员有权限增加用户
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
  public User createUser(User operator,
                            String name,
                            String email,
                            String desc,
                            String password,
                            String phone,
                            String proxyUsers) {
    // 如果不是管理员, 返回错误
    if (operator.getRole() != UserRoleType.ADMIN_USER) {
      throw new PermissionException("admin",operator.getName());
    }

    // 校验代理用户格式是否正确以及是否包含正常代理的内容
    // TODO::

    User user = new User();
    Date now = new Date();

    user.setName(name);
    user.setEmail(email);
    user.setDesc(desc);
    user.setPhone(phone);
    user.setPassword(HttpUtil.getMd5(password));
    user.setRole(UserRoleType.GENERAL_USER); // 创建的用户都是普通用户, 管理员用户当前是内置的
    user.setProxyUsers(proxyUsers);
    user.setCreateTime(now);
    user.setModifyTime(now);

    // 插入一条用户信息
    try {
      userMapper.insert(user);
    } catch (DuplicateKeyException e) {
      logger.error("User has exist, can't create again.", e);
      throw new NotModifiedException("User has exist, can't create again.");
    }

    return user;
  }

  /**
   * 修改用户信息
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
  public User modifyUser(User operator,
                         String name,
                         String email,
                         String desc,
                         String password,
                         String phone,
                         String proxyUsers) {
    if (operator.getRole() != UserRoleType.ADMIN_USER) {
      // 非管理员, 只能修改自身信息
      if (!StringUtils.equals(operator.getName(), name)) {
        throw new PermissionException("admin",operator.getName());
      }

      // 普通用户不能进行用户代理设置
      if (StringUtils.isNotEmpty(proxyUsers)) {
        throw new PermissionException("admin",operator.getName());
      }
    }

    User user = userMapper.queryByName(name);

    if (user == null) {
      throw new NotFoundException("user",name);
    }

    Date now = new Date();

    if (email != null) {
      user.setEmail(email);
    }

    if (desc != null) {
      user.setDesc(desc);
    }

    if (phone != null) {
      user.setPhone(phone);
    }

    if (password != null) {
      if (StringUtils.isNotEmpty(password)) {
        user.setPassword(HttpUtil.getMd5(password));
      }
    }

    if (proxyUsers != null) {
      // 校验代理用户格式是否正确以及是否包含正常代理的内容
      // TODO::

      user.setProxyUsers(proxyUsers);
    }

    user.setModifyTime(now);

    int count = userMapper.update(user);

    if (count <= 0) {
      throw new NotModifiedException("Not update count");
    }

    return user;
  }

  /**
   * 删除用户信息, 只有管理员能够操作
   *
   * @param operator
   * @param name
   * @return
   */
  public void deleteUser(User operator,
                         String name) {
    if (operator.getRole() != UserRoleType.ADMIN_USER) {
      throw new PermissionException("admin",operator.getName());
    }

    // 删除, 是不能删除自己的
    if (StringUtils.equals(operator.getName(), name)) {
      logger.error("Can't delete user self");
      throw new ParameterException("name");
    }

    // 删除用户的时候, 必须保证 "项目" 的信息不为空
    List<Project> projects = projectMapper.queryProjectByUser(operator.getId());

    if (CollectionUtils.isNotEmpty(projects)) {
      logger.error("Can't delete a account which has projects");
      throw new NotModifiedException("Can't delete a account which has projects");
    }

    int count = userMapper.delete(name);

    if (count <= 0) {
      throw new NotModifiedException("Not delete count");
    }

    return;
  }

  /**
   * 查询用户信息
   *
   * @param operator
   * @param allUser
   * @return
   */
  public List<User> queryUser(User operator,
                              boolean allUser) {
    List<User> userList = new ArrayList<>();

    // 只查询自己
    if ((operator.getRole() != UserRoleType.ADMIN_USER) || !allUser) {
      userList.add(operator);

      return userList;
    }

    return userMapper.queryAllUsers();
  }

  /**
   * 查询用户信息, 校验账号和密码
   *
   * @param name
   * @param email
   * @param password
   * @return
   */
  public User queryUser(String name, String email, String password) {
    String md5 = HttpUtil.getMd5(password);

    return userMapper.queryForCheck(name, email, md5);
  }
}
