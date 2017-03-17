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
package com.baifendian.swordfish.webserver.api.interceptor;

import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.Session;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录的拦截器, 在执行除 "/login" 的操作之前, 都会进行拦截
 */
public class LoginInterceptor implements HandlerInterceptor {
  private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class.getName());

  @Autowired
  private SessionService sessionService;

  @Autowired
  private UserMapper userMapper;

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
    Session session = sessionService.getSessionFromRequest(httpServletRequest);

    // session 如果没有获取到, 返回 false
    if (session == null) {
      return false;
    }

    // 获取到了, 则设置一下 session
    User user = userMapper.queryById(session.getUserId());

    if (user == null) {
      return false;
    }

    httpServletRequest.setAttribute("session.user", user);

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    logger.info(httpServletResponse.toString());
  }

  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
  }
}
