package com.baifendian.swordfish.common.hive;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenting on 9/8/16.
 */


public class HiveMetaPoolClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(HiveMetaPoolClient.class);

    private GenericObjectPool pool;

    /** 超时时间，单位为ms，默认为3s */
    private int timeout = 3000;

    /** 最大活跃连接数 */
    private int maxActive = 1024;

    /** 链接池中最大空闲的连接数,默认为100 */
    private int maxIdle = 100;

    /** 连接池中最少空闲的连接数,默认为0 */
    private int minIdle = 0;

    /** 当连接池资源耗尽时，调用者最大阻塞的时间 */
    private int maxWait = 2000;

    /** 空闲链接”检测线程，检测的周期，毫秒数，默认位3min，-1表示关闭空闲检测 */
    private int timeBetweenEvictionRunsMillis = 180000;

    /** 空闲时是否进行连接有效性验证，如果验证失败则移除，默认为false */
    private boolean testWhileIdle = false;

    private static HiveMetaPoolClient hiveMetaPoolClient;

    private HiveMetaPoolClient(String metastoreUris, String jdoUrl, String jdoUser,
                              String jdoPassword, String jdoDriverName) {
        try {
            pool = bulidClientPool(metastoreUris, jdoUrl,
                    jdoUser, jdoPassword, jdoDriverName);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public static void init(String metastoreUris, String jdoUrl, String jdoUser,
                       String jdoPassword, String jdoDriverName) {
        if(hiveMetaPoolClient == null) {
            synchronized (HiveMetaPoolClient.class) {
                hiveMetaPoolClient = new HiveMetaPoolClient(metastoreUris, jdoUrl,
                        jdoUser, jdoPassword, jdoDriverName);
            }
        }
    }

    public static HiveMetaPoolClient getInstance() {
        if(hiveMetaPoolClient == null) {
            LOGGER.error("获取 HiveMetaPoolClient 实例失败，请先调用 init 方法初始化");
        }
        return hiveMetaPoolClient;
    }

    protected GenericObjectPool bulidClientPool(String metastoreUris, String jdoUrl, String jdoUser,
                                                String jdoPassword, String jdoDriverName) {
        // 设置poolConfig
        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
        poolConfig.maxActive = maxActive;
        poolConfig.maxIdle = maxIdle;
        poolConfig.minIdle = minIdle;
        poolConfig.maxWait = maxWait;
        poolConfig.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        poolConfig.testWhileIdle = testWhileIdle;
        HiveMetaPoolFactory clientFactory = new HiveMetaPoolFactory(metastoreUris, jdoUrl,
                jdoUser, jdoPassword, jdoDriverName);
        return new GenericObjectPool(clientFactory, poolConfig);
    }

    public HiveMetaStoreClient borrowClient() throws  Exception{
        return (HiveMetaStoreClient)pool.borrowObject();
    }

    public void clear() {
        pool.clear();
    }

    public void returnClient(HiveMetaStoreClient client){
        if(client != null) {
            try {
                pool.returnObject(client);
            } catch (Exception e) {
                LOGGER.error("HiveMetaPoolClient returnObject error:" , e);
            }
        }
    }

    public void invalidateObject(HiveMetaStoreClient client){
        try {
            pool.invalidateObject(client);
        } catch (Exception e) {
            LOGGER.error("HiveMetaPoolClient invalidateObject error:" , e);
        }
    }
}
