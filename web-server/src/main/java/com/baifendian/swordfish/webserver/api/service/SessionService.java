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
import com.baifendian.swordfish.dao.model.Session;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * author: smile8
 * date:   2017/3/15
 * desc:
 */
@Service
public class SessionService {
  private static Logger logger = LoggerFactory.getLogger(SessionService.class.getName());

  @Autowired
  private SessionMapper sessionMapper;

  @Value("${server.session.timeout}")
  private int sessionEffectiveTime;

  /**
   * 查询 session 信息
   *
   * @param sessionId
   * @param remoteIp
   * @return
   */
  private Session querySession(String sessionId, String remoteIp) {
    if (sessionId == null) {
      return null;
    }

    // 查询 session 对象
    Session session = sessionMapper.findById(sessionId);
    if (session == null) {
      return null;
    }

    if (!remoteIp.equals(session.getIp()) && !isDebug) {
      return null;
    }

    return session;
  }

  /**
   * 从请求中获取用户的 session
   *
   * @param req
   * @return
   * @throws ServletException
   */
  public Session getSessionFromRequest(HttpServletRequest req) throws ServletException {
    String remoteIp = HttpUtil.getClientIpAddress(req);
    Cookie cookie = HttpUtil.getCookieByName(req, Constants.SESSION_ID_NAME);
    String sessionId = null;

    if (cookie != null) {
      sessionId = cookie.getValue();
    }
    if (sessionId == null && HttpUtil.hasParam(req, "sessionId")) {
      sessionId = req.getParameter("sessionId");
    }

    LOGGER.info("session:{} from ip:{}", sessionId, remoteIp);
    boolean isDebug = req.getParameter("isDebug") != null;
    return querySession(sessionId, remoteIp, isDebug);
  }

  /**
   * 创建一个 session
   *
   * @param user
   * @param remoteIp
   * @return
   */
  public Session createSession(User user, String remoteIp) {
    String sessionId = UUID.randomUUID().toString();
    Date startTime = new Date();
    Date endTime = CommonUtil.addHour(startTime, sessionEffect);

    Session session = new Session(sessionId, remoteIp, startTime, endTime, user);
    sessionMapper.insert(session);
    return session;
  }
}
