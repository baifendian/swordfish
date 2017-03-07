/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月25日
 * File Name      : DwSchedulerWorker.java
 */

package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.dao.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.execserver.service.ExecServiceImpl;
import com.baifendian.swordfish.execserver.utils.OsUtil;
import com.bfd.harpc.common.configure.PropertiesConfiguration;
import com.bfd.harpc.main.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker 服务
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月25日
 */
public class ExecServer {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecServer.class);

    /** 默认配置文件 */
    private static final String DEFAULT_CONFIG = "classpath:worker.properties";

    /** yarn 作业配置文件 */
    private static final String JOB_YARN_CONFIG = "classpath:job/yarn.properties";

    /** hive 作业配置文件 */
    private static final String JOB_HIVE_CONFIG = "classpath:job/hive.properties";

    /** java 作业配置文件 */
    private static final String JOB_JAVA_CONFIG = "classpath:job/java.properties";

    /** server配置文件 */
    private static final String SERVER_FILE_PATH = "classpath:worker-server.properties";

    /** 是否保持启动 */
    private static boolean running = true;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("start exec server");
        try {
            // 下面语句用于 windows 调试
            if (OsUtil.isWindows()) {
                System.setProperty("hadoop.home.dir", "d:\\hadoop\\");
            }

            // 加载配置文件
            PropertiesConfiguration.load(new String[] { DEFAULT_CONFIG, JOB_YARN_CONFIG, JOB_HIVE_CONFIG, JOB_JAVA_CONFIG });

            // 初始化 hdfs client
            HdfsClient.init(ConfigurationUtil.getConfiguration());

            ExecServiceImpl impl = new ExecServiceImpl();

            // 下面是测试
            /*
             Integer projectId = 5794;
             Integer flowId = 999;
             FlowDao flowDao = DaoFactory.getDaoInstance(FlowDao.class);
             ExecutionFlow executionFlow =
             flowDao.runFlowToExecution(projectId, flowId);
             impl.execFlow(projectId, executionFlow.getId(),
             FlowType.SHORT.toString());
             */
            impl.scheduleExecFlow(1, 4, "etl", System.currentTimeMillis());
/*
            final Server server = new Server(new String[] { SERVER_FILE_PATH }, impl);
            server.start();

            // 添加ShutdownHook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    server.close(); // 关闭服务
                    impl.destory(); // 销毁资源
                }
            }));


            synchronized (ExecServer.class) {
                while (running) {
                    try {
                        ExecServer.class.wait();
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
*/
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
