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
package com.baifendian.swordfish.common.utils.http;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HttpUtil {
  private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

  /**
   * 得到 http 请求中的 ip 地址
   *
   * @param request
   * @return
   */
  public static String getClientIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");

    if (StringUtils.isNotEmpty(ip) && !StringUtils.equalsIgnoreCase("unKnown", ip)) {
      // 多次反向代理后会有多个 ip 值，第一个 ip 才是真实 ip
      int index = ip.indexOf(",");
      if (index != -1) {
        return ip.substring(0, index);
      } else {
        return ip;
      }
    }

    ip = request.getHeader("X-Real-IP");
    if (StringUtils.isNotEmpty(ip) && !StringUtils.equalsIgnoreCase("unKnown", ip)) {
      return ip;
    }

    return request.getRemoteAddr();
  }

  /**
   * 获取 cookie 信息
   *
   * @param request
   * @param name
   * @return
   */
  public static Cookie getCookieByName(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (StringUtils.equalsIgnoreCase(name, cookie.getName())) {
          return cookie;
        }
      }
    }

    return null;
  }

  /**
   * 得到 md5
   *
   * @param raw
   * @return
   */
  public static String getMd5(String raw) {
    return DigestUtils.md5Hex(raw);
  }
}
