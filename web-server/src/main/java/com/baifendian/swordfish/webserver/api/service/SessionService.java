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

import com.baifendian.swordfish.dao.mapper.SessionMapper;
import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.Session;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.dto.UserSessionData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class SessionService {
  private static Logger logger = LoggerFactory.getLogger(SessionService.class.getName());

  @Value("${server.session.timeout}")
  private int sessionEffectiveTime;

  @Autowired
  private SessionMapper sessionMapper;

  @Autowired
  private UserMapper userMapper;

  /**
   * 查询 session 信息
   *
   * @param sessionId
   * @param remoteIp
   * @return
   */
//  private Session querySession(String sessionId, String remoteIp) {
//    if (sessionId == null) {
//      return null;
//    }
//
//    // 查询 session 对象
//    Session session = sessionMapper.findById(sessionId);
//    if (session == null) {
//      return null;
//    }
//
//    if (!remoteIp.equals(session.getIp()) && !isDebug) {
//      return null;
//    }
//
//    return session;
//  }
//
//  /**
//   * 从请求中获取用户的 session
//   *
//   * @param req
//   * @return
//   * @throws ServletException
//   */
//  public Session getSessionFromRequest(HttpServletRequest req) throws ServletException {
//    String remoteIp = HttpUtil.getClientIpAddress(req);
//    Cookie cookie = HttpUtil.getCookieByName(req, Constants.SESSION_ID_NAME);
//    String sessionId = null;
//
//    if (cookie != null) {
//      sessionId = cookie.getValue();
//    }
//    if (sessionId == null && HttpUtil.hasParam(req, "sessionId")) {
//      sessionId = req.getParameter("sessionId");
//    }
//
//    LOGGER.info("session:{} from ip:{}", sessionId, remoteIp);
//    boolean isDebug = req.getParameter("isDebug") != null;
//    return querySession(sessionId, remoteIp, isDebug);
//  }

  /**
   * 创建一个 session:
   * 1) 如果已经登陆了, 直接返回已经登陆的 session
   * 2) 如果没有登陆或者过期了, 那么会删除过期的信息, 并分配新的 sessionId
   *
   * @param name
   * @param email
   * @param ip
   * @return
   */
  public UserSessionData createSession(String name, String email, String ip) {
    UserSessionData userSessionData = new UserSessionData();
    User user;

    // 用户名存在
    if (StringUtils.isNotEmpty(name)) {
      user = userMapper.queryByName(name);
    } else { // 邮箱存在
      user = userMapper.queryByEmail(email);
    }

    // 查看是否已经存在
    if (user == null) {
      return null;
    }

    Session session = sessionMapper.queryByUserIdAndIp(user.getId(), ip);
    boolean needCreate = true;

    if (session != null) {
      Date lastLoginTime = session.getLastLoginTime();
      Date now = new Date();

      // session 未过期
      if (now.getTime() - lastLoginTime.getTime() < sessionEffectiveTime * 1000) {
        needCreate = false;
      } else { // 过期删除
        sessionMapper.deleteById(session.getId());
      }
    }

    // 需要新建
    if (needCreate) {
      session = new Session();

      session.setId(UUID.randomUUID().toString());
      session.setIp(ip);
      session.setUserId(user.getId());
      session.setLastLoginTime(new Date());

      sessionMapper.insert(session);
    }

    return new UserSessionData(session.getId(), user);
  }
}
