package com.baifendian.swordfish.server.sparksql;

import com.baifendian.swordfish.rpc.SparkSqlService;
import com.baifendian.swordfish.server.sparksql.service.SparkSqlServiceImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkThriftServer {

  private static Logger logger = LoggerFactory.getLogger(SparkThriftServer.class);

  private TServer server;

  /**
   * 当前 exec 的 host 信息
   */
  private String host;

  /**
   * 当前 exec 的 port 信息
   */
  private final int port;

  public class TServerThread extends Thread {

    private TServer server;

    public TServerThread(TServer server) {
      this.server = server;
    }

    @Override
    public void run() {
      server.serve();
    }
  }

  SparkThriftServer() throws TTransportException, UnknownHostException {
    port = 20017;
    // executor 的地址, 端口信息
    host = InetAddress.getLocalHost().getHostAddress();

    // 启动 worker service
    server = getTThreadPoolServer();

    Thread serverThread = new TServerThread(server);
    serverThread.setDaemon(true);
    serverThread.start();

    logger.info("start thrift server on port:{}", port);
  }

  public TServer getTThreadPoolServer(  ) throws TTransportException {
    TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

    TServerTransport serverTransport = new TServerSocket(new InetSocketAddress(host, port));
    TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
    serverArgs.minWorkerThreads(50);
    serverArgs.maxWorkerThreads(200);
    serverArgs.processor(new SparkSqlService.Processor(new SparkSqlServiceImpl()));
    serverArgs.transportFactory( new TTransportFactory());
    serverArgs.protocolFactory(protocolFactory);
    return new TThreadPoolServer(serverArgs);
  }

  public static void main(String[] args)
      throws UnknownHostException, TTransportException, InterruptedException {
    new SparkThriftServer();

    while (true){
      Thread.sleep(10000);
    }
  }
}
