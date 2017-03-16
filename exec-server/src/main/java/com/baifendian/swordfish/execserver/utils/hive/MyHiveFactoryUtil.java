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

package com.baifendian.swordfish.execserver.utils.hive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 初始化hiveConf
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年10月26日
 */

public class MyHiveFactoryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyHiveFactoryUtil.class);
    private static final Properties PROPERTIES = new Properties();
    private static HiveConfig hiveConfig;

    static {
        try {
            File dataSourceFile = ResourceUtils.getFile("classpath:common/hive/hive.properties");
            InputStream is = new FileInputStream(dataSourceFile);
            PROPERTIES.load(is);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void buildHiveConfig(HiveConfig hiveConfig) {
        hiveConfig.setMetastoreUris(PROPERTIES.getProperty("hive.metastore.uris"));
        hiveConfig.setThriftUris(PROPERTIES.getProperty("hive.thrift.uris"));
        hiveConfig.setRootUser(PROPERTIES.getProperty("hive.root.user"));
        hiveConfig.setPassword(PROPERTIES.getProperty("hive.root.password"));
        hiveConfig.setJdoUrl(PROPERTIES.getProperty("javax.jdo.option.ConnectionURL"));
        hiveConfig.setJdoDriverName(PROPERTIES.getProperty("javax.jdo.option.ConnectionDriverName"));
        hiveConfig.setJdoUser(PROPERTIES.getProperty("javax.jdo.option.ConnectionUserName"));
        hiveConfig.setJdoPassword(PROPERTIES.getProperty("javax.jdo.option.ConnectionPassword"));
    }

    public static HiveConfig getInstance() {
        if (hiveConfig == null) {
            synchronized (MyHiveFactoryUtil.class) {
                if (hiveConfig == null) {
                    HiveConfig hiveConfigTemp = new HiveConfig();
                    buildHiveConfig(hiveConfigTemp);
                    hiveConfig = hiveConfigTemp;
                }
            }
        }
        return hiveConfig;
    }

}
