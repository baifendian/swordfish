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

import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.UserSessionData;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import com.baifendian.swordfish.webserver.exception.UnAuthorizedException;
import com.baifendian.swordfish.webserver.service.SessionService;
import com.baifendian.swordfish.webserver.service.UserService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录入口
 */
@RestController
@RequestMapping("/login")
public class LoginController {

  private static Logger logger = LoggerFactory.getLogger(LoginController.class.getName());

  @Autowired
  private SessionService sessionService;

  @Autowired
  private UserService userService;

  /**
   * @param name     用户名
   * @param email    用户 email
   * @param password 登陆密码
   * @param request  请求信息
   * @param response 返回信息
   * @return
   */
  @RequestMapping(value = "", method = {RequestMethod.POST, RequestMethod.GET})
  public UserSessionData login(@RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "password") String password,
                               HttpServletRequest request,
                               HttpServletResponse response) {
    logger.info("Login, user name: {}, email: {}, password: {}", name, email, "******");

    // 必须存在一个
    if (StringUtils.isEmpty(name) && StringUtils.isEmpty(email)) {
      throw new ParameterException("name or email");
    }

    // 且必须存在一个
    if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(email)) {
      throw new ParameterException("name or email");
    }

    // 得到用户 ip 信息
    String ip = HttpUtil.getClientIpAddress(request);
    if (StringUtils.isEmpty(ip)) {
      throw new ParameterException("ip");
    }

    // 验证用户名和密码是否正确
    User user = userService.queryUser(name, email, password);

    if (user == null) {
      throw new UnAuthorizedException("User password error");
    }

    // 创建 session
    UserSessionData data = sessionService.createSession(user, ip);

    if (data == null) {
      throw new UnAuthorizedException("Create session error");
    }

    response.setStatus(HttpStatus.SC_OK);
    response.addCookie(new Cookie("sessionId", data.getSessionId()));

    return data;
  }
}
