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

package com.baifendian.swordfish.execserver.utils;

import com.baifendian.swordfish.dao.model.flow.params.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * command 工具类
 * @author : liujin
 * @date : 2017-03-02 12:25
 */
public class CommandUtil {

    /**
     * 生成 -D 参数
     * <p>
     *
     * @param properties
     * @return -D 参数
     */
    public static List<String> genDArgs(Map<String, String> properties) {
        List<String> dArgs = new ArrayList<>();
        if (properties != null) {
            for (Map.Entry entry : properties.entrySet()) {
                dArgs.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        return dArgs;
    }

    /**
     * 获取所有参数的类型
     * @param args
     * @return
     */
    public static Class<?>[] getTypes(Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < argTypes.length; i++)
            argTypes[i] = args[i].getClass();
        return argTypes;
    }
}
