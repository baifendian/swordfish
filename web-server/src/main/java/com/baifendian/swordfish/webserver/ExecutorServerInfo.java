/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.rpc.HeartBeatData;

/**
 * @author : liujin
 * @date : 2017-03-10 17:53
 */
public class ExecutorServerInfo {

    private String host;

    private int port;

    private HeartBeatData heartBeatData;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public HeartBeatData getHeartBeatData() {
        return heartBeatData;
    }

    public void setHeartBeatData(HeartBeatData heartBeatData) {
        this.heartBeatData = heartBeatData;
    }
}
