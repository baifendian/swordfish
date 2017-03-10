/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.MasterService;
import com.baifendian.swordfish.rpc.RetInfo;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : liujin
 * @date : 2017-03-10 16:49
 */
public class MasterClient {

    private static Logger logger = LoggerFactory.getLogger(MasterClient.class);

    private String host;

    private int port;

    private int timeout = 3000;

    private TTransport tTransport;

    private MasterService.Client client;

    private int retries;

    public MasterClient(String host, int port, int retries){
        this.host = host;
        this.port = port;
        this.retries = retries;
    }

    private void connect(){
        tTransport = new TSocket(host, port, timeout);
        try {
            TProtocol protocol = new TBinaryProtocol(tTransport);
            client = new MasterService.Client(protocol);
            tTransport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    private void close(){
        if(tTransport != null){
            tTransport.close();
        }
    }

    public boolean executorReport(String clientHost, int clientPort, HeartBeatData heartBeatData) {
        connect();
        try{
            client.executorReport(clientHost, clientPort, heartBeatData);
        }catch (TException e) {
            logger.error("report info error", e);
            return false;
        }finally {
            close();
        }
        return true;
    }

    public boolean registerExecutor(String clientHost, int clientPort) {
        connect();
        try{
            RetInfo ret = client.registerExecutor(clientHost, clientPort);
            if(ret.getStatus() != 0) {
                logger.error("register executor error:{}", ret.getMsg());
                return false;
            }
        }catch (TException e) {
            logger.error("report info error", e);
            return false;
        }finally {
            close();
        }
        return true;
    }

    public boolean registerExecutorWithRetry(String clientHost, int clientPort) {
        boolean flag = false;
        for(int i=0; i<retries; i++){
            if(registerExecutor(clientHost, clientPort)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
