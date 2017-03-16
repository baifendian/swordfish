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

package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.common.job.config.BaseConfig;
import com.baifendian.swordfish.dao.mysql.enums.FlowRunType;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.execserver.job.JobTypeManager;
import com.baifendian.swordfish.execserver.service.ExecServiceImpl;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author : liujin
 * @date : 2017-03-03 12:17
 */
public class Init {
    public static void initFlow(){
        FlowDao flowDao = DaoFactory.getDaoInstance(FlowDao.class);
        ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(1,1,1,new Date(), FlowRunType.DISPATCH);
        System.out.println(executionFlow.getId());
    }

    public static void testJob() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JobProps props = new JobProps();
        props.setJobParams("{\"script\":\"ls -l\"}");
        Logger logger = LoggerFactory.getLogger(Init.class);
        Job job = JobTypeManager.newJob("NODE_1", "VIRTUAL", props, logger);
    }

    public static void runFlow() throws TException {
        ExecServiceImpl impl = new ExecServiceImpl("127.0.0.1", 7777);
        impl.scheduleExecFlow(1, 3275, "etl", System.currentTimeMillis());

    }
    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, TException {
        Init.initFlow();
        //Init.testJob();
        //Init.runFlow();
        System.out.println(BaseConfig.getSystemEnvPath());
        System.out.println(new Date(1488607000));
    }
}
