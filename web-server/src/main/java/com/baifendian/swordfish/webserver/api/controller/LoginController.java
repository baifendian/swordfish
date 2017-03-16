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
import com.baifendian.swordfish.webserver.api.dto.BaseData;
import com.baifendian.swordfish.webserver.api.dto.ErrorData;
import com.baifendian.swordfish.webserver.api.dto.UserSessionData;
import com.baifendian.swordfish.webserver.api.service.SessionService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
public class LoginController {
  @Autowired
  private SessionService sessionService;

  /**
   * @param name:     用户名
   * @param password: 登陆密码
   * @param response
   * @return
   */
  @RequestMapping(value = "/", method = {RequestMethod.POST})
  public BaseData login(@RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "email", required = false) String email,
                        @RequestParam(value = "password") String password,
                        HttpServletRequest request,
                        HttpServletResponse response) {
    // 必须存在一个
    if (StringUtils.isEmpty(name) && StringUtils.isEmpty(password)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return new ErrorData(ErrorData.Code.PARAM_NOT_VALID, "用户名和邮箱必须存在一个");
    }

    // 且必须存在一个
    if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(password)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return new ErrorData(ErrorData.Code.PARAM_NOT_VALID, "用户名和邮箱不能同时存在");
    }

    // 得到用户 ip 信息
    String ip = HttpUtil.getClientIpAddress(request);
    if (StringUtils.isEmpty(ip)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return new ErrorData(ErrorData.Code.PARAM_NOT_VALID, "无法获取用户 ip 信息");
    }

    // 创建 session
    UserSessionData data = sessionService.createSession(name, email, ip);

    if (data == null) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return new ErrorData(ErrorData.Code.USER_NOT_EXIST, "登陆失败, 请检查账号/密码是否正确");
    }

    response.addCookie(new Cookie("sessionId", data.getSessionId()));
    return data;
  }
}
