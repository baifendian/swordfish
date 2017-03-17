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

import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.BaseData;
import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserMapper userMapper;

  /**
   * 创建用户
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
  public BaseData createUser(User operator,
                             String name,
                             String email,
                             String desc,
                             String password,
                             String phone,
                             String proxyUsers) {
    User user = new User();

    user.setName(name);
    user.setEmail(email);
    user.setDesc(desc);
    user.setPassword(HttpUtil.getMd5(password));
    user.setPhone(phone);
    user.setProxyUsers(proxyUsers);

    return user;
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
