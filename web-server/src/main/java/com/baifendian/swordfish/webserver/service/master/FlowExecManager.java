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

package com.baifendian.swordfish.webserver.service.master;

import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mysql.enums.FlowRunType;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ProjectFlow;
import com.baifendian.swordfish.rpc.WorkerService;
import com.baifendian.swordfish.webserver.ExecutorClient;
import com.baifendian.swordfish.webserver.ExecutorServerInfo;
import com.baifendian.swordfish.webserver.ExecutorServerManager;
import com.baifendian.swordfish.webserver.config.MasterConfig;
import com.baifendian.swordfish.dao.mail.EmailManager;
import com.bfd.harpc.RpcException;
import com.bfd.harpc.monitor.NamedThreadFactory;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * workflow 的执行管理
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月6日
 */
public class FlowExecManager {
    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** {@link ExecutorService} */
    private final ExecutorService appendFlowExecutorService;

    /** execution flow queue **/
    private final BlockingQueue<ExecutionFlow> executionFlowBlockingQueue;

    /** {@link FlowDao} */
    private final FlowDao flowDao;

    /** 检测依赖的等待时间，默认 30 s */
    private static long checkInterval = 30 * 1000;

    /**
     * @param executionFlowBlockingQueue
     * @param flowDao
     */
    public FlowExecManager(BlockingQueue<ExecutionFlow> executionFlowBlockingQueue, FlowDao flowDao) {
        this.executionFlowBlockingQueue = executionFlowBlockingQueue;
        this.flowDao = flowDao;

        NamedThreadFactory flowThreadFactory = new NamedThreadFactory("Scheduler-Master-AddData");
        appendFlowExecutorService = Executors.newCachedThreadPool(flowThreadFactory);
    }

    /**
     * 提交补数据任务
     * <p>
     *
     * @param flow
     * @param cron
     * @param startDateTime
     * @param endDateTime
     */
    public void submitAddData(ProjectFlow flow, CronExpression cron, Date startDateTime, Date endDateTime) {
        // 提交任务去执行
        appendFlowExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Date scheduleDate = cron.getTimeAfter(DateUtils.addSeconds(startDateTime, -1));
                try {
                    boolean isFailed = false; // 是否已经失败
                    List<Map.Entry<Date, Boolean>> resultList = new ArrayList<>();
                    while (scheduleDate.before(endDateTime) || scheduleDate.equals(endDateTime)) {
                        Boolean execStatus = null;
                        if (!isFailed) {
                            // 插入 ExecutionFlow
                            int scheduleUnixTimestamp = BFDDateUtils.getSecs(scheduleDate);
                            ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(flow.getProjectId(), flow.getId(), flow.getOwnerId(), scheduleUnixTimestamp, FlowRunType.ADD_DATA);
                            executionFlow.setProjectId(flow.getProjectId());
                            executionFlow.setFlowType(flow.getType());

                            // 发送请求到 executor server 中执行
                            executionFlowBlockingQueue.add(executionFlow);

                            // 如果当前任务补数据任务失败，后续任务不再执行
                            execStatus = checkExecStatus(executionFlow.getId());
                            if (!execStatus) {
                                isFailed = true;
                            }
                        }
                        resultList.add(new AbstractMap.SimpleImmutableEntry<Date, Boolean>(new Date(scheduleDate.getTime()), execStatus));
                        scheduleDate = cron.getTimeAfter(scheduleDate);
                    }
                    // 发送邮件
                    EmailManager.sendAddDataEmail(flow, !isFailed, resultList);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }

            }
        });
    }

    /**
     * 检测 workflow 的执行状态
     * <p>
     *
     * @param execId
     * @return 是否成功
     */
    private boolean checkExecStatus(Long execId) {
        while (true) {
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                return false;
            }

            ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
            if (executionFlow.getStatus().typeIsSuccess()) {
                return true;
            } else if (executionFlow.getStatus().typeIsFinished()) {
                return false;
            }
        }
    }

    /**
     * 销毁资源
     * <p>
     */
    public void destroy() {
        if (!appendFlowExecutorService.isShutdown()) {
            try {
                appendFlowExecutorService.shutdownNow();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
