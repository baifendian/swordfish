package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.common.job.config.BaseConfig;
import com.baifendian.swordfish.dao.mysql.enums.FlowRunType;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.execserver.job.JobTypeManager;
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
        ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(1,1,1,11111111, FlowRunType.DISPATCH);
        System.out.println(executionFlow.getId());
    }

    public static void testJob() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JobProps props = new JobProps();
        props.setJobParams("{\"script\":\"ls -l\"}");
        Logger logger = LoggerFactory.getLogger(Init.class);
        Job job = JobTypeManager.newJob("NODE_1", "VIRTUAL", props, logger);
    }
    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        Init.initFlow();
        //Init.testJob();
        //FileUtils.writeStringToFile(new File("/home/swordfish/log/job_log/2017-03-03/NODE_1_20170303133421.log"), "haha");
        System.out.println(BaseConfig.getSystemEnvPath());
        System.out.println(new Date(1488607000));
    }
}
