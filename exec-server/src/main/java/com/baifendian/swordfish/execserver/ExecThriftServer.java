/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.MasterDao;
import com.baifendian.swordfish.dao.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.dao.mysql.model.MasterServer;
import com.baifendian.swordfish.execserver.service.ExecServiceImpl;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.WorkerService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;
import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.baifendian.swordfish.execserver.utils.ThriftUtil.getTThreadPoolServer;

/**
 * @author : liujin
 * @date : 2017-03-10 14:57
 */
public class ExecThriftServer {
    private static Logger logger = LoggerFactory.getLogger(ExecThriftServer.class);

    private TServer server;

    private static Configuration conf;

    private MasterDao masterDao;

    private final MasterServer masterServer;

    private String hostName;

    private final int port;

    private InetSocketAddress inetSocketAddress;

    private ScheduledExecutorService executorService;

    private MasterClient masterClient;

    private AtomicBoolean running = new AtomicBoolean(true);

    private Object syncObject = new Object();

    private ExecServiceImpl workerService;

    private final int THRIFT_RPC_RETRIES = 3;

    /** 心跳时间间隔，单位秒 */
    private int heartBeatInterval;

    static{
        try {
            conf = new PropertiesConfiguration("worker.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ExecThriftServer() throws TTransportException, UnknownHostException {
        masterDao = DaoFactory.getDaoInstance(MasterDao.class);
        masterServer = masterDao.getMasterServer();
        if(masterServer == null){
            throw new ExecException("can't found master server");
        }

        port = conf.getInt("executor.port", 10000);
    }

    public void run() throws UnknownHostException, TTransportException {
        HdfsClient.init(ConfigurationUtil.getConfiguration());

        masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort(), THRIFT_RPC_RETRIES);
        hostName = InetAddress.getLocalHost().getHostName();

        logger.info("register to master {}:{}", masterServer.getHost(), masterServer.getPort());
        /** 注册到master */
        boolean ret = masterClient.registerExecutor(hostName, port, System.currentTimeMillis());
        if(!ret){
            throw new ExecException("register executor error");
        }
        heartBeatInterval = conf.getInt("executor.heartbeat.interval", 60);

        executorService = Executors.newScheduledThreadPool(5);
        Runnable heartBeatThread = getHeartBeatThread();
        executorService.scheduleAtFixedRate(heartBeatThread, 10, heartBeatInterval, TimeUnit.SECONDS);

        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory tTransportFactory = new TTransportFactory();
        workerService = new ExecServiceImpl();
        TProcessor tProcessor = new WorkerService.Processor(workerService);
        inetSocketAddress = new InetSocketAddress(hostName, port);
        server = getTThreadPoolServer(protocolFactory, tProcessor, tTransportFactory, inetSocketAddress, 50, 200);
        logger.info("start thrift server on port:{}", port);
        Thread serverThread = new TServerThread(server);
        serverThread.setDaemon(true);
        serverThread.start();

        synchronized (syncObject){
            while(running.get()){
                try {
                    logger.info("wait....................");
                    syncObject.wait();
                } catch (InterruptedException e) {
                    logger.error("error", e);
                }
            }
            executorService.shutdown();
            server.stop();
            logger.info("exec server stop");
        }

    }

    public Runnable getHeartBeatThread(){
        Runnable heartBeatThread = new Runnable() {
            @Override
            public void run() {
                if(running.get()) {
                    MasterServer masterServer = masterDao.getMasterServer();
                    HeartBeatData heartBeatData = new HeartBeatData();
                    heartBeatData.setReportDate(System.currentTimeMillis());
                    MasterClient client = new MasterClient(masterServer.getHost(), masterServer.getPort(), THRIFT_RPC_RETRIES);
                    logger.debug("executor report heartbeat:{}", heartBeatData);
                    boolean result = client.executorReport(hostName, port, heartBeatData);
                    if (!result) {
                        logger.warn("heart beat time out");
                        running.compareAndSet(true, false);
                        synchronized (syncObject) {
                            syncObject.notify();
                        }
                    }
                }
            }
        };
        return heartBeatThread;
    }

    public class TServerThread extends Thread{
        private TServer server;
        public TServerThread(TServer server){
            this.server = server;
        }

        @Override
        public void run(){
            server.serve();
        }
    }

    public static void main(String[] args) throws TTransportException, UnknownHostException {
        ExecThriftServer execThriftServer = new ExecThriftServer();
        execThriftServer.run();
    }
}
