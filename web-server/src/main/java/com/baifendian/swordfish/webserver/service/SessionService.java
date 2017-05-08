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
import com.baifendian.swordfish.dao.mapper.SessionMapper;
import com.baifendian.swordfish.dao.model.Session;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.UserSessionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Service
public class SessionService {
  private static Logger logger = LoggerFactory.getLogger(SessionService.class.getName());

  private static final String SESSION_ID_NAME = "sessionId";

  @Value("${server.session.timeout}")
  private int sessionEffectiveTime;

  @Autowired
  private SessionMapper sessionMapper;

  /**
   * 从请求中获取用户的 session
   *
   * @param req
   * @return
   * @throws ServletException
   */
  public Session getSessionFromRequest(HttpServletRequest req) throws ServletException {
    // 得到 ip 地址
    String ip = HttpUtil.getClientIpAddress(req);

    String sessionId = req.getHeader(SESSION_ID_NAME);;

    if(sessionId == null) {
      // 得到 cookie 信息
      Cookie cookie = HttpUtil.getCookieByName(req, SESSION_ID_NAME);

      if (cookie != null) {
        sessionId = cookie.getValue();
      }
    }

    if(sessionId == null) {
      return null;
    }

    logger.info("session: {} from ip: {}", sessionId, ip);

    return sessionMapper.queryByIdAndIp(sessionId, ip);
  }

  /**
   * 创建一个 session:
   * 1) 如果已经登陆了, 直接返回已经登陆的 session
   * 2) 如果没有登陆或者过期了, 那么会删除过期的信息, 并分配新的 sessionId
   *
   * @param user
   * @param ip
   * @return
   */
  public UserSessionDto createSession(User user, String ip) {
    // 查询是否登录过
    Session session = sessionMapper.queryByUserIdAndIp(user.getId(), ip);
    Date now = new Date();

    // 如果登录过, 且还是有效期内, 直接返回
    if (session != null) {
      if (now.getTime() - session.getLastLoginTime().getTime() <= sessionEffectiveTime * 1000) {
        // 更新最新登录时间
        sessionMapper.update(session.getId(), now);

        return new UserSessionDto(session.getId(), user);
      } else { // 有效期外, 则删除
        sessionMapper.deleteById(session.getId());
      }
    }

    // 需要新建
    session = new Session();

    session.setId(UUID.randomUUID().toString());
    session.setIp(ip);
    session.setUserId(user.getId());
    session.setLastLoginTime(now);

    sessionMapper.insert(session);

    return new UserSessionDto(session.getId(), user);
  }
}
