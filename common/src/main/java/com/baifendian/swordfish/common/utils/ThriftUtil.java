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
package com.baifendian.swordfish.common.utils;

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
