/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.utils;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import java.net.InetSocketAddress;

/**
 * @author : liujin
 * @date : 2017-03-10 17:40
 */
public class ThriftUtil {

    public static TServer getTThreadPoolServer(TProtocolFactory protocolFactory, TProcessor processor,
                                                TTransportFactory transportFactory, InetSocketAddress inetSocketAddress, int minWorkerThreads, int maxWorkerThreads) throws TTransportException {
        TServerTransport serverTransport = new TServerSocket(inetSocketAddress);
        TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
        serverArgs.minWorkerThreads(minWorkerThreads);
        serverArgs.maxWorkerThreads(maxWorkerThreads);
        serverArgs.processor(processor);
        serverArgs.transportFactory(transportFactory);
        serverArgs.protocolFactory(protocolFactory);
        return new TThreadPoolServer(serverArgs);
    }
}
