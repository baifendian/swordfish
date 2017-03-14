/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.webserver;


import com.baifendian.swordfish.webserver.exception.MasterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : liujin
 * @date : 2017-03-10 18:01
 */
public class ExecutorServerManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, ExecutorServerInfo> executorServers = new ConcurrentHashMap<>();

    public synchronized ExecutorServerInfo addServer(String key, ExecutorServerInfo executorServerInfo) throws MasterException {
        if(executorServers.containsKey(key)) {
            throw new MasterException("executor is register");
        }
        return executorServers.put(key, executorServerInfo);
    }

    public synchronized ExecutorServerInfo updateServer(String key, ExecutorServerInfo executorServerInfo) throws MasterException {
        if(!executorServers.containsKey(key)) {
            throw new MasterException("executor is not register");
        }
        return executorServers.put(key, executorServerInfo);
    }

    /**
     * 获取一个可用的executor server, 选取执行的workflow最少的那个excutorserver
     * @return
     */
    public synchronized ExecutorServerInfo getExecutorServer(){
        logger.debug("executor servers:{}", executorServers.toString());
        ExecutorServerInfo result = null;
        for(ExecutorServerInfo executorServerInfo: executorServers.values()){
            if(executorServerInfo.getHeartBeatData() == null){
                continue;
            }
            if(result == null){
                result = executorServerInfo;
            } else {
                if(result.getHeartBeatData().getExecIdsSize() > executorServerInfo.getHeartBeatData().getExecIdsSize()){
                    result = executorServerInfo;
                }
            }
        }
        return result;
    }

    public synchronized List<ExecutorServerInfo> checkTimeoutServer(long timeoutInterval){
        List<ExecutorServerInfo> faultServers = new ArrayList<>();
        for(Map.Entry<String, ExecutorServerInfo> entry: executorServers.entrySet()){
            long nowTime = System.currentTimeMillis();
            long diff = nowTime - entry.getValue().getHeartBeatData().getReportDate();
            if(diff > timeoutInterval){
                logger.warn("executor server time out {}", entry.getKey());
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

    public void initServers(Map<String, ExecutorServerInfo> executorServerInfoMap){
        for(Map.Entry<String, ExecutorServerInfo> entry:executorServerInfoMap.entrySet()){
            executorServers.put(entry.getKey(), entry.getValue());
        }
    }

}
