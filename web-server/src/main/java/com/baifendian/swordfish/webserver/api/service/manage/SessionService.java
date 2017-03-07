package com.baifendian.swordfish.webserver.api.service.manage;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.common.utils.http.HttpUtil;
import com.baifendian.swordfish.dao.mysql.enums.UserStatusType;
import com.baifendian.swordfish.dao.mysql.mapper.SessionMapper;
import com.baifendian.swordfish.dao.mysql.mapper.UserMapper;
import com.baifendian.swordfish.dao.mysql.model.Session;
import com.baifendian.swordfish.dao.mysql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by caojingwei on 16/8/22.
 */
@Service
public class SessionService {
    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private UserMapper userMapper;

    @Value("${dw.session.effect}")
    private int sessionEffect;

    public Session querySession(String sessionId,String remoteIp){
        if (sessionId == null) {
            return null;
        }
        Session session = sessionMapper.findById(sessionId);
        if (session == null || !remoteIp.equals(session.getIp())) {
            return null;
        }

        User queryArg = new User();
        queryArg.setId(session.getUserId());
        queryArg.setStatus(UserStatusType.NORMAL);
        List<User> userList = userMapper.query(queryArg,true);
        if (userList.isEmpty()){
            return null;
        }else {
            session.setUser(userList.get(0));
        }

        if (!session.isRemember()){
            Date startTime = new Date();
            Date endTime = CommonUtil.addHour(startTime, sessionEffect);
            sessionMapper.update(sessionId,endTime,startTime);
        }
        return session;
    }

    public Session getSessionFromRequest(HttpServletRequest req) throws ServletException {
        String remoteIp = req.getRemoteAddr();
        Cookie cookie = HttpUtil.getCookieByName(req, Constants.SESSION_ID_NAME);
        String sessionId = null;

        if (cookie != null) {
            sessionId = cookie.getValue();
        }
        if (sessionId == null && HttpUtil.hasParam(req, "sessionId")) {
            sessionId = req.getParameter("sessionId");
        }
        return querySession(sessionId, remoteIp);
    }

    public Session createSession(User user, String remoteIp, boolean isRemember) {
        String sessionId = UUID.randomUUID().toString();
        Date startTime = new Date();
        Date endTime;

        if (isRemember) {
            endTime = CommonUtil.addDay(startTime, 7);
        } else {
            endTime = CommonUtil.addHour(startTime, sessionEffect);
        }

        Session session = new Session(sessionId, remoteIp, startTime, endTime, isRemember, user);
        sessionMapper.insert(session);
        return session;
    }


}
