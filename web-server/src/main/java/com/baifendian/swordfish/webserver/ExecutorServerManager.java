/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.webserver;


import java.util.*;
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

    /**
     * 获取一个可用的executor server, 选取执行的workflow最少的那个excutorserver
     * @return
     */
    public synchronized ExecutorServerInfo getExecutorServer(){
        Optional<ExecutorServerInfo> server = executorServers.values().stream().filter(p->p.getHeartBeatData()!=null)
                .min(Comparator.comparing(executorServerInfo -> executorServerInfo.getHeartBeatData().getExecIdsSize()));
        if(server.isPresent()){
            return server.get();
        }else{
            return null;
        }
    }

    public synchronized List<ExecutorServerInfo> checkTimeoutServer(long timeoutInterval){
        List<ExecutorServerInfo> faultServers = new ArrayList<>();
        for(Map.Entry<String, ExecutorServerInfo> entry: executorServers.entrySet()){
            long nowTime = System.currentTimeMillis();
            long diff = nowTime - entry.getValue().getHeartBeatData().getReportDate();
            if(diff > timeoutInterval){
                executorServers.remove(entry.getKey());
                faultServers.add(entry.getValue());
            }
        }
        return faultServers;
    }

    public synchronized ExecutorServerInfo removeServer(ExecutorServerInfo executorServerInfo){
        String key = executorServerInfo.getHost() + ":" + executorServerInfo.getPort();
        return executorServers.remove(key);
    }
}
