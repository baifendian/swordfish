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
package com.baifendian.swordfish.dao.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Json序列化工具类 <p>
 *
 * @author : dsfan
 * @date : 2016年7月29日
 */
public class JsonUtil {
  /**
   * json mapper(这里的 ObjectMapper 是线程安全的)
   */
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  static {
    // 未匹配的属性不解析
    JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 使用系统默认时区
    JSON_MAPPER.setTimeZone(TimeZone.getDefault());
  }

  /**
   * private constructor
   */
  private JsonUtil() {
  }

  /**
   * 对象装json字符串 <p>
   *
   * @return json string
   */
  public static String toJsonString(Object object) {
    try {
      // JSON_MAPPER.setDateFormat(new
      // SimpleDateFormat(Constants.BASE_DATETIME_FORMAT));
      return JSON_MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json序列化异常", e);
    }
  }

  /**
   * 将string 转化为 map @param <V> @param <T> @param <T> @param object @return @throws
   */
  public static <T, V> Map<T, V> stringToMap(String json, Class<T> classT, Class<V> classV) {
    try {
      // JSON_MAPPER.setDateFormat(new
      // SimpleDateFormat(Constants.BASE_DATETIME_FORMAT));
      return JSON_MAPPER.readValue(json, new TypeReference<Map<T, V>>() {
      });
    } catch (Exception e) {
      throw new RuntimeException("Json反序列化异常", e);
    }
  }

  /**
   * 反序列化一个json字符串 <p>
   *
   * @return instance of T
   */
  public static <T> T parseObject(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try {
      return JSON_MAPPER.readValue(json, clazz);
    } catch (IOException e) {
      System.out.println(e);
      throw new RuntimeException("Json反序列化异常", e);
    }
  }

  public static Map<String, String> parseObjectMap(String json) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try {
      return JSON_MAPPER.readValue(json, new TypeReference<HashMap<String, String>>() {
      });
    } catch (IOException e) {
      System.out.println(e);
      throw new RuntimeException("Json反序列化异常", e);
    }
  }

  /**
   * 反序列化一个json字符串 <p>
   *
   * @return instance of List<T>
   */
  public static <T> List<T> parseObjectList(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try {
      JavaType javaType = JSON_MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
      return JSON_MAPPER.readValue(json, javaType);
    } catch (IOException e) {
      throw new RuntimeException("Json反序列化异常", e);
    }
  }

  /**
   * 获取 Json 树 <p>
   *
   * @return {@link JsonNode}
   */
  public static JsonNode readTree(String json) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try {
      return JSON_MAPPER.readTree(json);
    } catch (IOException e) {
      throw new RuntimeException("Json解析异常", e);
    }
  }

  /**
   * 获取字段的值（支持深度搜索）<br/> 多个相同字段的情况下，获取节点顺序的第一个 <p>
   *
   * @param jsonNode json tree
   * @param field    字段名
   * @return {@link JsonNode}
   */
  public static String findNodeByField(JsonNode jsonNode, String field) {
    JsonNode node = jsonNode.findValue(field);
    if (node == null) {
      return null;
    }
    return node.toString();
  }

}
