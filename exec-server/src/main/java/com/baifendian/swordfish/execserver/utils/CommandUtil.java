package com.baifendian.swordfish.execserver.utils;

import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;

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
