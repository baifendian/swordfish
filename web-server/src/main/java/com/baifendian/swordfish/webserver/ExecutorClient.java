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

package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.MasterService;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.WorkerService;
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
 * @date : 2017-03-13 8:43
 */
public class ExecutorClient {

    private static Logger logger = LoggerFactory.getLogger(ExecutorClient.class);

    private String host;

    private int port;

    private int timeout = 3000;

    private final int RPC_RETRIES = 3;

    private TTransport tTransport;

    private WorkerService.Client client;

    private int retries;

    public ExecutorClient(String host, int port, int retries){
        this.host = host;
        this.port = port;
        this.retries = retries;
    }

    public ExecutorClient(ExecutorServerInfo executorServerInfo){
        this.host = executorServerInfo.getHost();
        this.port = executorServerInfo.getPort();
        this.retries = RPC_RETRIES;
    }

    private void connect(){
        tTransport = new TSocket(host, port, timeout);
        try {
            TProtocol protocol = new TBinaryProtocol(tTransport);
            client = new WorkerService.Client(protocol);
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

    public boolean scheduleExecFlow(int projectId, long execId, String flowType, long scheduleDate) {
        connect();
        try{
            client.scheduleExecFlow(projectId, execId, flowType, scheduleDate);
        }catch (TException e) {
            logger.error("report info error", e);
            return false;
        }finally {
            close();
        }
        return true;
    }

    public boolean execFlow(int projectId, long execId, String flowType) throws TException {
        connect();
        try{
            client.execFlow(projectId, execId, flowType);
        }catch (TException e) {
            logger.error("exec flow error", e);
            throw e;
        }finally {
            close();
        }
        return true;
    }

}
