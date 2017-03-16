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

import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.mysql.mapper.UserMapper;
import com.baifendian.swordfish.webserver.api.dto.BaseData;
import com.baifendian.swordfish.webserver.api.dto.ErrorInfo;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: smile8
 * date:   2017/3/16
 * desc:
 */
@RestController
@RequestMapping("/login")
public class LoginController {
  @Autowired
  private UserMapper userMapper;

  /**
   * @param name:     用户名
   * @param password: 登陆密码
   * @param response
   * @return
   */
  @RequestMapping(value = "/}", method = {RequestMethod.POST})
  public BaseData login(@RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "password", required = false) String password,
                        HttpServletRequest request,
                        HttpServletResponse response) {
    // 必须存在一个
    if (StringUtils.isEmpty(name) && StringUtils.isEmpty(password)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return new ErrorInfo(1, "用户名和邮箱必须存在一个");
    }

    // 且必须存在一个
    if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(password)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return new ErrorInfo(1, "用户名和邮箱不能同时存在");
    }

    // 得到用户 ip 信息
    String ip = HttpUtil.getClientIpAddress(request);
    if (StringUtils.isEmpty(ip)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return new ErrorInfo(1, "无法获取用户 ip 信息");
    }

    // 下面的逻辑是这样的:
    // 1. 如果已经登陆了, 直接返回已经登陆的 session
    // 2. 如果没有登陆或者过期了, 那么会删除过期的信息, 并分配新的 sessionId

    // 用户名存在
    if (StringUtils.isNotEmpty(name)) {
      // 得到用户 id

      // 查看是否已经存在

    } else { // 邮箱存在

    }



  }

}
