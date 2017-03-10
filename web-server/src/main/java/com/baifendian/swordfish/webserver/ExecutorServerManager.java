/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.webserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : liujin
 * @date : 2017-03-10 18:01
 */
public class ExecutorServerManager {
    private Map<String, ExecutorServerInfo> executorServers = new ConcurrentHashMap<>();


    public boolean serverExists(String key){
        return executorServers.containsKey(key);
    }

    public ExecutorServerInfo addServer(String key, ExecutorServerInfo executorServerInfo){
        return executorServers.put(key, executorServerInfo);
    }
}
