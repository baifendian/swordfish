/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月13日
 * File Name      : ParamHelper.java
 */

package com.baifendian.swordfish.execserver.parameter;

import com.baifendian.swordfish.common.utils.PlaceholderUtil;
import com.baifendian.swordfish.common.utils.TimePlaceholderUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 参数解析帮助类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月13日
 */
public class ParamHelper {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamHelper.class);

    /**
     * 替换参数的占位符
     * <p>
     *
     * @param text
     * @param systemParamMap
     * @param customParamMap
     * @return 替换后的文本
     */
    public static String resolvePlaceholders(String text, Map<String, String> systemParamMap, Map<String, String> customParamMap) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        // 获取 dw.system.cyctime
        Date cycTime = null;
        String cycTimeStr = systemParamMap.get(SystemParamManager.CYC_TIME);
        if (StringUtils.isNotEmpty(cycTimeStr)) {
            try {
                cycTime = DateUtils.parseDate(cycTimeStr, new String[] { SystemParamManager.TIME_FORMAT });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            cycTime = new Date();
        }

        text = PlaceholderUtil.resolvePlaceholders(text, systemParamMap, true);
        text = PlaceholderUtil.resolvePlaceholders(text, customParamMap, true);
        if (cycTime != null) {
            text = TimePlaceholderUtil.resolvePlaceholders(text, cycTime, true);
        }
        return text;
    }

    /**
     * 替换参数的占位符
     * <p>
     *
     * @param text
     * @param paramMap
     * @return 替换后的文本
     */
    public static String resolvePlaceholders(String text, Map<String, String> paramMap) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        // 获取 dw.system.cyctime
        Date cycTime = null;
        String cycTimeStr = paramMap.get(SystemParamManager.CYC_TIME);
        if (StringUtils.isNotEmpty(cycTimeStr)) {
            try {
                cycTime = DateUtils.parseDate(cycTimeStr, new String[] { SystemParamManager.TIME_FORMAT });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            cycTime = new Date();
        }

        text = PlaceholderUtil.resolvePlaceholders(text, paramMap, true);
        if (cycTime != null) {
            text = TimePlaceholderUtil.resolvePlaceholders(text, cycTime, true);
        }
        return text;
    }
}
