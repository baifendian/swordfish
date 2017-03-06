package com.baifendian.swordfish.common.hive;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.hive.jdbc.HiveConnection;

import java.sql.SQLException;

/**
 * Created by wenting on 9/8/16.
 */
public class HiveConnectionPoolFactory extends BaseKeyedPoolableObjectFactory{
    /**
     * 生成对象
     */
    @Override
    public HiveConnection makeObject(Object object) throws Exception {
        // 生成client对象
        if (object != null) {
            ConnectionInfo connectionInfo = (ConnectionInfo)object;
            java.util.Properties info = new java.util.Properties();
            if (connectionInfo.getUser() != null) {
                info.put("user", connectionInfo.getUser());
            }
            if (connectionInfo.getPassword() != null) {
                info.put("password", connectionInfo.getPassword());
            }
            try {
                HiveConnection connection = new HiveConnection(connectionInfo.getUri(), info);
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(Object key, Object obj) throws Exception {
        HiveConnection hiveConnection = (HiveConnection)obj;
        ((HiveConnection) obj).close();
    }

    @Override
    public boolean validateObject(Object key, Object obj) {
        HiveConnection hiveConnection = (HiveConnection)obj;
        try {
            boolean isClosed = hiveConnection.isClosed();
            return  !isClosed;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
