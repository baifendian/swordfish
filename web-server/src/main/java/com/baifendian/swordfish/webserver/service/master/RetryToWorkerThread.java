/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月28日
 * File Name      : RetryToWorkerThread.java
 */

package com.baifendian.swordfish.webserver.service.master;

import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.rpc.WorkerService;
import com.baifendian.swordfish.webserver.config.MasterConfig;
import com.bfd.harpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * 重试请求 Worker 的线程
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月28日
 */
public class RetryToWorkerThread extends Thread {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** worker rpc client */
    private final WorkerService.Iface worker;

    /** {@link FlowDao} */
    private final FlowDao flowDao;

    /** workflow 执行队列 */
    private final BlockingQueue<ExecutionFlow> executionFlowQueue;

    /**
     * @param worker
     * @param flowDao
     * @param executionFlowQueue
     */
    public RetryToWorkerThread(WorkerService.Iface worker, FlowDao flowDao, BlockingQueue<ExecutionFlow> executionFlowQueue) {
        this.worker = worker;
        this.flowDao = flowDao;
        this.executionFlowQueue = executionFlowQueue;

        this.setName("Master-RetryToWorker");
    }

    @Override
    public void run() {
        while (true) {
            ExecutionFlow executionFlow;
            try {
                executionFlow = executionFlowQueue.take();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                break; // 中断则退出
            }

            long execId = executionFlow.getId();
            boolean isSucess = false; // 是否请求成功
            for (int i = 0; i < MasterConfig.failRetryCount; i++) {
                try {
                    worker.execFlow(executionFlow.getProjectId(), execId, executionFlow.getFlowType().name());
                    isSucess = true;
                    break; // 请求成功，结束重试请求
                } catch (RpcException e) {
                    ExecutionFlow temp = flowDao.queryExecutionFlow(execId);
                    // 如果执行被取消或者状态已经更新，结束重试请求
                    if (temp == null || temp.getStatus() != FlowStatus.INIT) {
                        break;
                    }
                } catch (Exception e) { // 内部错误
                    LOGGER.error(e.getMessage(), e);
                }
            }

            // 多次重试后仍然失败
            if (!isSucess) {
                flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);
            }
        }
    }

}
