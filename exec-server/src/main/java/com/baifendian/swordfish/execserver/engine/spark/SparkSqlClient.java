package com.baifendian.swordfish.execserver.engine.spark;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.rpc.AdhocResultInfo;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.SparkSqlService.Client;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkSqlClient {
  private static Logger LOGGER = LoggerFactory.getLogger(SparkSqlClient.class.getName());

  private SynchronousQueue<Client> clientQueue = new SynchronousQueue<>();

  private Map<String, Client> jobInfo = new ConcurrentHashMap<>();

  static public void init(String hosts, int port) {
    instance = new SparkSqlClient(hosts, port);
  }
  static public SparkSqlClient getInstance(){
    return instance;
  }

  static private SparkSqlClient instance;

  public SparkSqlClient(String hosts, int port){
    String[] hostArray = hosts.split(",");

    for (String host: hostArray){
      TTransport tTransport = new TSocket(host, port, 4000);

      try {
        TProtocol protocol = new TBinaryProtocol(tTransport);

        Client client = new Client(protocol);
        tTransport.open();
        clientQueue.offer(client);
      } catch (TTransportException e) {
        LOGGER.error("Connection server exception", e);
      }
    }
  }

  public boolean execute(String jobId, List<UdfInfo> udfs, List<String> sql, int remainTime,
      Logger logger) {
    Client client;
    try {
      client = clientQueue.poll(remainTime, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.info("queue interrupted", e);
      return false;
    }

    if (client == null) {
      logger.info("job time out");
      return false;
    }

    RetInfo retInfo;
    try {
      jobInfo.put(jobId, client);
      retInfo = client.execEtl(jobId, udfs, sql, remainTime);
    } catch (TException e) {
      logger.info("TException", e);
      return false;
    }

    jobInfo.remove(jobId);

    return retInfo.getStatus() == 0;
  }

  public boolean executeAdhoc(String jobId, List<UdfInfo> udfs, List<String> sql,
      ResultCallback resultCallback, int queryLimit, int remainTime, Logger logger) {
    Client client;
    try {
      client = clientQueue.poll(remainTime, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.info("queue interrupted", e);
      return false;
    }

    if (client == null) {
      logger.info("job time out");
      return false;
    }

    Date startTime = new Date();
    RetInfo retInfo;
    try {
      retInfo = client.execAdhoc(jobId, udfs, sql, queryLimit, remainTime);
    } catch (TException e) {
      logger.info("TException", e);
      return false;
    }
    jobInfo.put(jobId, client);
    for (int i=0;i<sql.size(); ++i){
      ExecResult execResult = new ExecResult();
      execResult.setIndex(i);
      execResult.setStm(sql.get(i));
      AdhocResultInfo adhocResultInfo;
      try {
        adhocResultInfo = client.getAdhocResult(jobId);
      } catch (TException e) {
        logger.info("TException", e);
        execResult.setStatus(FlowStatus.FAILED);
        resultCallback.handleResult(execResult, startTime, new Date());
        continue;
      }

      execResult.setStatus(FlowStatus.valueOfType(adhocResultInfo.getStatus()));
      execResult.setTitles(adhocResultInfo.getTitles());
      execResult.setValues(adhocResultInfo.getValues());
      resultCallback.handleResult(execResult, startTime, new Date());
    }

    jobInfo.remove(jobId);

    return retInfo.getStatus() == 0;
  }

  public boolean cancel(String jobId, Logger logger){
    Client client = jobInfo.get(jobId);
    if (client == null){
      return false;
    }

    try {
      client.cancelExecFlow(jobId);
    } catch (TException e) {
      logger.info("TException", e);
      return false;
    }

    return true;
  }
}
