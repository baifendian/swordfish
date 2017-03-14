/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.MasterDao;
import com.baifendian.swordfish.dao.mysql.model.MasterServer;
import com.baifendian.swordfish.execserver.service.ExecServiceImpl;
import com.baifendian.swordfish.rpc.MasterService;
import com.baifendian.swordfish.rpc.WorkerService;
import com.baifendian.swordfish.webserver.exception.MasterException;
import com.baifendian.swordfish.webserver.quartz.QuartzManager;
import com.baifendian.swordfish.webserver.service.master.MasterServiceImpl;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.tools.ant.taskdefs.Exec;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.baifendian.swordfish.execserver.utils.ThriftUtil.getTThreadPoolServer;

/**
 * @author : liujin
 * @date : 2017-03-10 17:42
 */
public class WebThriftServer {

    private static final Logger logger = LoggerFactory.getLogger(WebThriftServer.class);

    private static Configuration conf;

    private TServer server;

    private final String host;

    private final int port;

    private final String MASTER_PORT = "master.port";

    private final String MASTER_MIN_THREADS = "master.min.threads";
    private final String MASTER_MAX_THREADS = "master.max.threads";

    private MasterDao masterDao;

    private ExecutorServerManager executorServerManager;

    private MasterServiceImpl masterService;

    static{
        try {
            conf = new PropertiesConfiguration("master.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public WebThriftServer() throws UnknownHostException {
        host = InetAddress.getLocalHost().getHostName();
        port = conf.getInt(MASTER_PORT, 9999);
        masterDao = DaoFactory.getDaoInstance(MasterDao.class);
        executorServerManager = new ExecutorServerManager();
    }

    public void run() throws SchedulerException, TTransportException, MasterException {
        // 启动调度
        QuartzManager.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.stop(); // 关闭 server
                try {
                    // 关闭调度
                    QuartzManager.shutdown();
                    // 关闭资源
                } catch (SchedulerException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }));

        try {
            registerMaster();
            init();
            server.serve();
        }catch (Exception e){
            QuartzManager.shutdown();
            throw e;
        }


    }

    private void init() throws MasterException, TTransportException {
        masterService = new MasterServiceImpl(executorServerManager, conf);
        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory tTransportFactory = new TTransportFactory();
        TProcessor tProcessor = new MasterService.Processor(masterService);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        int minThreads = conf.getInt(MASTER_MIN_THREADS, 50);
        int maxThreads = conf.getInt(MASTER_MAX_THREADS, 200);
        server = getTThreadPoolServer(protocolFactory, tProcessor, tTransportFactory, inetSocketAddress, minThreads, maxThreads);
        logger.info("start thrift server on port:{}", port);

    }

    public void registerMaster() throws MasterException {
        MasterServer masterServer = masterDao.getMasterServer();
        if(masterServer != null && !(masterServer.getHost().equals(host) && masterServer.getPort() == port)){
            logger.error(String.format("can't register more then one master, exist master:%s:%d",
                    masterServer.getHost(), masterServer.getPort()));
            throw new MasterException(String.format("can't register more then one master, exist master:%s:%d",
                    masterServer.getHost(), masterServer.getPort()));
        } else {
            if(masterServer == null)
                masterDao.registerMasterServer(host, port);
            else
                masterDao.updateMasterServer(host, port);
        }
    }

    public static void main(String[] args) throws Exception {
        WebThriftServer webThriftServer = new WebThriftServer();
        webThriftServer.run();
    }
}
