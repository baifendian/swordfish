package com.baifendian.swordfish.execserver.engine.spark;

import com.baifendian.swordfish.rpc.AdhocResultRet;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.SparkSqlService;
import com.baifendian.swordfish.rpc.UdfInfo;
import com.baifendian.swordfish.rpc.SparkSqlService.Client;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkSqlClientDecorator implements SparkSqlService.Iface{

  private static Logger logger = LoggerFactory.getLogger(SparkSqlClientDecorator.class);

  TTransport tTransport;

  private Client client;
  private String host;
  private int port;

  public SparkSqlClientDecorator(String host, int port) {
    this.host = host;
    this.port = port;

    connect();
  }

  @Override
  public RetInfo execEtl(String jobId, List<UdfInfo> udfs, List<String> sql, int remainTime)
      throws TException {
    return clientExec(() -> client.execEtl(jobId, udfs, sql, remainTime));
  }

  @Override
  public RetInfo execAdhoc(String jobId, List<UdfInfo> udfs, List<String> sql, int queryLimit,
      int remainTime) throws TException {
    return clientExec(()->client.execAdhoc(jobId, udfs, sql, queryLimit, remainTime));
  }

  @Override
  public RetInfo cancelExecFlow(String jobId) throws TException {
    return clientExec(()->client.cancelExecFlow(jobId));
  }

  @Override
  public AdhocResultRet getAdhocResult(String jobId, int index) throws TException {
    return clientExec(()->client.getAdhocResult(jobId, index));
  }

  private void connect() {
    if (tTransport != null){
      tTransport.close();
    }

    tTransport = new TSocket(host, port, CONNECTION_TIMEOUT);

    try {
      TProtocol protocol = new TBinaryProtocol(tTransport);

      client = new Client(protocol);
      tTransport.open();
    } catch (TTransportException e) {
      logger.error("Connection server exception", e);

      throw new RuntimeException("Connect spark sql server error.", e);
    }
  }

  /**
   * 执行thrift请求，如果发生错误，则进行重连
   */
  private <T> T clientExec(Callable<T> call) {
    try {
      return call.call();
    } catch (Exception e) {
      logger.error("Connection server exception", e);
    }

    connect();

    try {
      return call.call();
    } catch (Exception e) {
      logger.error("Connection server exception", e);
      throw new RuntimeException("Spark sql execute error.", e);
    }
  }

  /**
   * 超时时间, 客户端连接到 master 的超时时间
   */
  private static final int CONNECTION_TIMEOUT = 4000;
}
