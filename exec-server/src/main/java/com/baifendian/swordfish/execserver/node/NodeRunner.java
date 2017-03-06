/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月27日
 * File Name      : NodeRunner.java
 */

package com.baifendian.swordfish.execserver.node;

import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ExecutionNode;
import com.baifendian.swordfish.dao.mysql.model.FlowNode;
import com.baifendian.swordfish.execserver.job.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.baifendian.swordfish.common.utils.StructuredArguments.jobValue;

/**
 * 节点执行器
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月27日
 */
public class NodeRunner implements Runnable {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** 超时时间 */
    private final int timeout;

    /** {@link FlowDao} */
    private final FlowDao flowDao;

    /** {@link ExecutionFlow} */
    private final ExecutionFlow executionFlow;

    /** {@link ExecutionNode} */
    private final ExecutionNode executionNode;

    /** {@link FlowNode} */
    private final FlowNode node;

    /** {@link ExecutorService} */
    private final ExecutorService executorService;

    /** 同步对象 */
    private final Object synObject;

    /** 系统参数 */
    private final Map<String, String> systemParamMap;

    /** 自定义参数 */
    private final Map<String, String> customParamMap;

    /** jobId 字符串 */
    private final String jobIdLog;

    /**
     * @param executionFlow
     * @param executionNode
     * @param node
     * @param executorService
     * @param synObject
     * @param timeout
     * @param customParamMap
     * @param systemParamMap
     */
    public NodeRunner(ExecutionFlow executionFlow, ExecutionNode executionNode, FlowNode node, ExecutorService executorService, Object synObject, int timeout,
                      Map<String, String> systemParamMap, Map<String, String> customParamMap) {
        this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
        this.executionFlow = executionFlow;
        this.executionNode = executionNode;
        this.node = node;
        this.executorService = executorService;
        this.synObject = synObject;
        this.timeout = timeout;
        this.systemParamMap = systemParamMap;
        this.customParamMap = customParamMap;
        this.jobIdLog = jobValue(executionNode.getJobId());
    }

    @Override
    public void run() {
        FlowStatus status = null;
        try {
            // 生成具体 handler
            JobHandler jobHandler = new JobHandler(flowDao, executionFlow, executionNode, node, executorService, timeout, systemParamMap, customParamMap);

            // 具体执行
            status = jobHandler.handle();

            // 更新 executionNode 信息
            updateExecutionNode(status);

        } catch (Exception e) {
            LOGGER.error("{}", jobIdLog + e.getMessage(), e);
        } finally {
            if (status == null) {
                updateExecutionNode(FlowStatus.FAILED);
            }
            // 唤醒 flow runner 线程
            notifyFlowRunner();
        }
    }

    /**
     * 更新数据库中的 ExecutionNode 信息
     * <p>
     *
     * @param flowStatus
     */
    private void updateExecutionNode(FlowStatus flowStatus) {
        executionNode.setStatus(flowStatus);
        executionNode.setEndTime(BFDDateUtils.getSecs());
        flowDao.updateExecutionNode(executionNode);
    }

    /**
     * 唤醒 flow runner 线程
     * <p>
     */
    private void notifyFlowRunner() {
        synchronized (synObject) {
            synObject.notifyAll();
        }
    }

}
