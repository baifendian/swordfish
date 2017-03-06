package com.baifendian.swordfish.common.hive;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenting on 9/8/16.
 */
public class HiveMetaPoolFactory extends BasePoolableObjectFactory{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String metastoreUris;

    private String jdoUrl;

    private String jdoUser;

    private String jdoPassword;

    private String jdoDriverName;

    HiveConf hConf;

    public HiveMetaPoolFactory(String metastoreUris, String jdoUrl, String jdoUser,
                               String jdoPassword, String jdoDriverName) {
        this.metastoreUris = metastoreUris;
        this.jdoUrl = jdoUrl;
        this.jdoUser = jdoUser;
        this.jdoPassword = jdoPassword;
        this.jdoDriverName = jdoDriverName;

        Configuration conf = new Configuration();
        if(StringUtils.isNotEmpty(metastoreUris)) {
            conf.set("hive.metastore.uris", metastoreUris);
        } else {
            conf.set("javax.jdo.option.ConnectionURL", jdoUrl);
            conf.set("javax.jdo.option.ConnectionUserName", jdoUser);
            conf.set("javax.jdo.option.ConnectionPassword", jdoPassword);
            conf.set("javax.jdo.option.ConnectionDriverName", jdoDriverName);
        }
        hConf = new HiveConf(conf, HiveConf.class);
    }

    @Override
    public Object makeObject() throws Exception {
        HiveMetaStoreClient hmsc = new HiveMetaStoreClient(hConf);
        return hmsc;
    }

    @Override
    public void destroyObject(Object client) throws Exception {
        HiveMetaStoreClient hmsc =(HiveMetaStoreClient)client;
        hmsc.close();
    }

    @Override
    public boolean validateObject(Object client) {
        HiveMetaStoreClient hmsc =(HiveMetaStoreClient)client;
        try {
            Database database = hmsc.getDatabase("defalut");
            if(database != null) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn("HiveMetaPoolFactory validateObject", e);
        }
        return false;
    }

}
