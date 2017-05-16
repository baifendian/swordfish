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
package com.baifendian.swordfish.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DaoFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DaoFactory.class);

  private static Map<String, BaseDao> daoMap = new ConcurrentHashMap<>();

  /**
   * 获取 Dao 实例 （单例） <p>
   *
   * @param clazz
   * @return Dao实例
   */
  @SuppressWarnings("unchecked")
  public static <T extends BaseDao> T getDaoInstance(Class<T> clazz) {
    String className = clazz.getName();
    synchronized (daoMap) {
      if (!daoMap.containsKey(className)) {
        try {
          T t = clazz.getConstructor().newInstance();
          // 实例初始化
          t.init();
          daoMap.put(className, t);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }

    return (T) daoMap.get(className);
  }
}
