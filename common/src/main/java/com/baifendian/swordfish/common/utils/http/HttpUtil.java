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

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by caojingwei on 16/8/22.
 */
public class HttpUtil {

    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 判断邮箱正则表达式
     **/
    public static final Pattern PATTERN_MATCHES_EMAIL = Pattern.compile(Constants.REGEX_MATCHES_EMAIL);

    /**
     * 判断用户名正则表达式
     **/
    public static final Pattern PATTERN_MATCHES_USER_NAME = Pattern.compile(Constants.REGEX_MATCHES_USER_NAME);

    /**
     * 判断手机号码
     **/
    public static final Pattern PATTERN_MATCHES_MOBILE = Pattern.compile(Constants.REGEX_MATCHES_MOBILE);

    /**
     * 判断密码是否规范
     **/
    public static final Pattern PATTERN_MATCHES_PWD = Pattern.compile(Constants.REGEX_MATCHES_PWD);

    /**
     * 判断组织 code 是否合法
     **/
    public static final Pattern PATTERN_MATCHES_ORG_NAME = Pattern.compile(Constants.REGEX_MATCHES_ORG_NAME);
    public static final Pattern PATTERN_MATCHES_MAIL_GROUPS = Pattern.compile(Constants.REGEX_MATCHES_MAIL_GROUPS);

    /**
     * 常用的名称的正则表达式
     **/
    public static final Pattern PATTERN_MATCHES_COMMON_NAME = Pattern.compile(Constants.REGEX_MATCHES_COMMON_NAME);

    /**
     * 常用名称（逻辑实体名称，物理实体名称，节点名称）的正则表达式
     **/
    public static final Pattern PATTERN_MATCHES_NODE_NAME = Pattern.compile(Constants.REGEX_MATCHES_NODE_NAME);

    /**
     * 资源名称的正则表达式
     **/
    public static final Pattern PATTERN_MATCHES_RESOURCE_NAME = Pattern.compile(Constants.REGEX_MATCHES_RESOURCE_NAME);

    /**
     * 资源名称抽取的正则表达式
     */
    public static Pattern PATTERN_RESOURCE_RULE_MATCHES = Pattern.compile(Constants.RESOURCE_RULE_MATCHES);

    /**
     * 从请求中获取 cookie 信息
     *
     * @param request
     * @param name
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static boolean hasParam(HttpServletRequest request, String param) {
        return request.getParameter(param) != null;
    }

    public static String requestGetParameterString(HttpServletRequest request, String param) {
        return request.getParameter(param);
    }

    public static Integer requestGetParameterInteger(HttpServletRequest request, String param) {
        String rst = request.getParameter(param);
        if (rst == null) {
            return null;
        } else {
            return Integer.valueOf(rst);
        }
    }

    public static Long requestGetParameterLong(HttpServletRequest request, String param) {
        String rst = request.getParameter(param);
        if (rst == null) {
            return null;
        } else {
            return Long.valueOf(rst);
        }
    }

    public static Date requestGetParameterDate(HttpServletRequest request, String param) {
        String rst = request.getParameter(param);
        if (!StringUtils.isNotEmpty(rst)) {
            return null;
        } else {
            return BFDDateUtils.parse(rst);
        }
    }

    public static Date requestGetParameterDate(HttpServletRequest request, String param, String dateFormat) {
        String rst = request.getParameter(param);
        if (!StringUtils.isNotEmpty(rst)) {
            return null;
        } else {
            return BFDDateUtils.parse(rst, dateFormat);
        }
    }

    public static Boolean requestGetParameterBoolean(HttpServletRequest request, String param) {
        String rst = request.getParameter(param);
        if (rst == null) {
            return null;
        } else {
            return Boolean.valueOf(rst);
        }
    }

    public static Double requestGetParameterDouble(HttpServletRequest request, String param) {
        String rst = request.getParameter(param);
        if (rst == null) {
            return null;
        } else {
            return Double.valueOf(rst);
        }
    }

    public static Map requestGetParameterMap(HttpServletRequest request, String param) throws RuntimeException {
        String rst = request.getParameter(param);
        if (rst == null) {
            return null;
        } else {
            return JsonUtil.parseObject(rst, HashMap.class);
        }
    }

    /**
     * 获取一个枚举参数
     * <p>
     *
     * @param request
     * @param param
     * @param enumClass
     * @return 枚举
     * @throws RuntimeException
     */
    public static <E extends Enum<E>> E requestGetParameterEnum(HttpServletRequest request, String param, Class<E> enumClass) throws RuntimeException {
        String rst = request.getParameter(param);
        if (StringUtils.isEmpty(rst)) {
            return null;
        } else {
            return Enum.valueOf(enumClass, rst);
        }
    }

    public static boolean regexMatches(String str, String regex) {
        return regexMatches(str, Pattern.compile(regex));
    }

    public static boolean regexMatches(String str, Pattern pattern) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        return pattern.matcher(str).matches();
    }

    public static boolean matcheEmail(String str) {
        return regexMatches(str, PATTERN_MATCHES_EMAIL);
    }

    public static boolean matcheUserName(String str) {
        return regexMatches(str, PATTERN_MATCHES_USER_NAME);
    }

    public static boolean matchTenantName(String name){
        return regexMatches(name, PATTERN_MATCHES_USER_NAME);
    }

    public static boolean matchePWD(String str) {
        return regexMatches(str, PATTERN_MATCHES_PWD);
    }

    public static boolean matchePhone(String str) {
        return regexMatches(str, PATTERN_MATCHES_MOBILE);
    }

    public static boolean matcheOrgName(String str) {
        return regexMatches(str, PATTERN_MATCHES_ORG_NAME);
    }

    public static boolean matchMailGroup(String str){
        return regexMatches(str, PATTERN_MATCHES_MAIL_GROUPS);
    }

    /**
     * 匹配字段名称, 函数名称等使用
     *
     * @param str
     * @return
     */
    public static boolean matcheCommonName(String str) {
        return regexMatches(str, PATTERN_MATCHES_COMMON_NAME);
    }

    /**
     * 匹配逻辑实体名称, 物理实体名称, 节点名称等使用
     *
     * @param str
     * @return
     */
    public static boolean matcheNodeName(String str) {
        return regexMatches(str, PATTERN_MATCHES_NODE_NAME);
    }

    public static boolean matcheResourceName(String str) {
        return regexMatches(str, PATTERN_MATCHES_RESOURCE_NAME);
    }

    public static byte[] get(String url) {
        return get(url, 0);
    }

    public static byte[] get(String url, int timeout) {
        HttpClient hc = new HttpClient();
        hc.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        hc.getHttpConnectionManager().getParams().setSoTimeout(timeout);
        GetMethod method = null;
        InputStream in = null;
        try {
            method = new GetMethod(url);
            method.setFollowRedirects(false);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, "utf-8");
            int code = hc.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                in = method.getResponseBodyAsStream();
                return IOUtils.toByteArray(in);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (method != null) {
                method.releaseConnection();
                method = null;
            }
            IOUtils.closeQuietly(in);
        }
        return new byte[] {};
    }

    public static byte[] post(String url, Map<String, String> params) {
        return post(url, params, 0);
    }

    public static byte[] post(String url, Map<String, String> params, int timeout) {
        HttpClient hc = new HttpClient();
        hc.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        hc.getHttpConnectionManager().getParams().setSoTimeout(timeout);
        PostMethod method = null;
        InputStream in = null;
        try {
            method = new PostMethod(url);
            method.setFollowRedirects(false);
            List<NameValuePair> _params = new ArrayList<NameValuePair>();
            if (params != null) {
                for (String key : params.keySet()) {
                    _params.add(new NameValuePair(key, params.get(key)));
                }
            }
            method.setRequestBody(_params.toArray(new NameValuePair[] {}));
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, "utf-8");
            int code = hc.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                in = method.getResponseBodyAsStream();
                return IOUtils.toByteArray(in);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (method != null) {
                method.releaseConnection();
                method = null;
            }
            IOUtils.closeQuietly(in);
        }
        return new byte[] {};
    }

}
