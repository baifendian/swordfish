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

package com.baifendian.swordfish.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 占位符工具类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月11日
 */
public class PlaceholderUtil {

    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderUtil.class);

    /** 待替换位置的前缀 : "${" */
    public static final String PLACEHOLDER_PREFIX = "${";

    /** 待替换位置的后缀 :"}" */
    public static final String PLACEHOLDER_SUFFIX = "}";

    /** 键与默认值的分割符（null表示不支持） */
    public static final String VALUE_SEPARATOR = null;

    /** 严格的替换工具实现，待替换的位置没有获取到对应值时，则抛出异常 */
    private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

    /** 非严格的替换工具实现，待替换的位置没有获取到对应值时，则忽略当前位置，继续替换下一个位置 */
    private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);

    /**
     * 替换文本的占位符
     * <p>
     *
     * @param text
     *            待替换文本
     * @param valueMap
     *            占位符的数据字典
     * @return 替换后的字符串
     */
    public static String resolvePlaceholders(String text, Map<String, String> valueMap) {
        return resolvePlaceholders(text, valueMap, false);
    }

    /**
     * 替换文本的占位符
     * <p>
     *
     * @param text
     *            待替换文本
     * @param valueMap
     *            占位符的数据字典
     * @param ignoreUnresolvablePlaceholders
     *            是否忽略没有匹配到值的占位符
     * @return 替换后的字符串
     */
    public static String resolvePlaceholders(String text, Map<String, String> valueMap, boolean ignoreUnresolvablePlaceholders) {
        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
        return helper.replacePlaceholders(text, new PropertyPlaceholderResolver(text, valueMap));
    }

    /**
     * 替换文本的占位符（空替换）
     * <p>
     *
     * @param text
     * @param constValue
     * @return 替换后的字符串
     */
    public static String resolvePlaceholdersConst(String text, String constValue) {
        return nonStrictHelper.replacePlaceholders(text, new ConstPlaceholderResolver(constValue));
    }

    /**
     * 占位符替换的处理
     * <p>
     * 
     * @author : dsfan
     * @date : 2016年10月11日
     */
    private static class PropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final String text;

        private final Map<String, String> valueMap;

        public PropertyPlaceholderResolver(String text, Map<String, String> valueMap) {
            this.text = text;
            this.valueMap = valueMap;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            try {
                String propVal = valueMap.get(placeholderName);

                return propVal;
            } catch (Throwable ex) {
                LOGGER.error("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "]", ex);
                return null;
            }
        }
    }

    /**
     * 占位符替换的处理（空字符串替换占位符）
     * <p>
     * 
     * @author : dsfan
     * @date : 2016年11月29日
     */
    private static class ConstPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final String constValue;

        public ConstPlaceholderResolver(String constValue) {
            this.constValue = constValue;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            return constValue;
        }
    }

    public static void main(String[] args) {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("test1", "1234");
        valueMap.put("parm", "hahah");
        String test1 = PlaceholderUtil.resolvePlaceholders("${test1}test1${parm1:***}${test1}", valueMap, true);
        System.out.println(test1);
        String test2 = PlaceholderUtil.resolvePlaceholdersConst("${test1}test1${parm1:***}${test1}", "NULL");
        System.out.println(test2);

    }
}
