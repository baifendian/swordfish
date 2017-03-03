/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月25日
 * File Name      : CustomParamManager.java
 */

package com.baifendian.swordfish.execserver.parameter;

import com.baifendian.swordfish.common.utils.TimePlaceholderUtil;
import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.mapper.FlowParamMapper;
import com.baifendian.swordfish.dao.mysql.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.FlowParam;
import com.baifendian.swordfish.dao.mysql.model.Project;
import com.baifendian.swordfish.dao.mysql.model.ProjectFlow;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * 自定义参数管理
 * <p>
 * 
 * @author : liujin
 * @date : 2017年3月1日
 */
public class CustomParamManager {

    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomParamManager.class);

    /** {@link ProjectFlowMapper} */
    private static final ProjectFlowMapper projectFlowMapper= MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ProjectFlowMapper.class);

    /**
     * 构建自定义参数值
     *
     * <p>
     *
     * @param executionFlow
     * @param cycTimeStr
     * @return 自定义参数
     */
    public static Map<String, String> buildCustomParam(ExecutionFlow executionFlow, String cycTimeStr) {
        // 获取工作流的参数
        ProjectFlow projectFlow = projectFlowMapper.findById(executionFlow.getFlowId());
        Map<String, String> valueMap = new HashMap<>();
        Date cycTime = new Date(executionFlow.getStartTime()*1000);

        if (StringUtils.isNotEmpty(cycTimeStr)) {
            try {
                cycTime = DateUtils.parseDate(cycTimeStr, new String[] { SystemParamManager.TIME_FORMAT });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        // 遍历 flow 的参数
        Map<String, String> userDefinedParamMap = projectFlow.getUserDefinedParamMap();
        if(userDefinedParamMap != null) {
            for (Map.Entry<String, String> entry : userDefinedParamMap.entrySet()) {
                String value = entry.getValue();

                // 基于系统参数 ${dw.system.cyctime} 的自定义变量
                if (StringUtils.isNotEmpty(value) && value.startsWith(TimePlaceholderUtil.PLACEHOLDER_PREFIX) && value.endsWith(TimePlaceholderUtil.PLACEHOLDER_SUFFIX)) {
                    value = TimePlaceholderUtil.resolvePlaceholders(value, cycTime, true);
                }

                valueMap.put(entry.getKey(), value);
            }
        }

        return valueMap;
    }

    /**
     * 构建自定义参数值
     * <p>
     *
     * @param executionFlow
     * @param cycTimeStr
     * @param envMap
     * @return 自定义参数
     */
    public static Map<String, String> buildCustomParam(ExecutionFlow executionFlow, String cycTimeStr, Map<String, String> envMap) {
        Map<String, String> valueMap = new HashMap<>();

        Date cycTime = new Date(executionFlow.getStartTime()*1000);
        if (StringUtils.isNotEmpty(cycTimeStr)) {
            try {
                cycTime = DateUtils.parseDate(cycTimeStr, new String[] { SystemParamManager.TIME_FORMAT });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        Set<String> keySet = envMap.keySet();
        for (String key : keySet) {
            String value = envMap.get(key);
            // 基于系统参数 ${dw.system.cyctime} 的自定义变量
            if (StringUtils.isNotEmpty(value) && value.startsWith(TimePlaceholderUtil.PLACEHOLDER_PREFIX) && value.endsWith(TimePlaceholderUtil.PLACEHOLDER_SUFFIX)) {
                value = TimePlaceholderUtil.resolvePlaceholders(value, cycTime, true);
            }

            valueMap.put(key, value);
        }

        return valueMap;
    }

}
