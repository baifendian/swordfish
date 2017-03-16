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

package com.baifendian.swordfish.execserver.utils.hadoop;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置实例获取工具
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月3日
 */
public class ConfigurationUtil {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationUtil.class);

    /** {@link Configuration} */
    private static volatile Configuration configuration;

    /** {@link Properties} */
    private static final Properties PROPERTIES = new Properties();

    static {
        InputStream is = null;
        try {
            File dataSourceFile = ResourceUtils.getFile("classpath:common/hadoop/hadoop.properties");
            is = new FileInputStream(dataSourceFile);
            PROPERTIES.load(is);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 获取 Configuration
     * <p>
     *
     * @return {@link Configuration}
     */
    public static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (ConfigurationUtil.class) {
                if (configuration == null) {
                    configuration = new Configuration();
                    initConfiguration();
                }
            }
        }
        return configuration;
    }

    /**
     * 初始化配置
     * <p>
     */
    private static void initConfiguration() {
        configuration.setBoolean("mapreduce.app-submission.cross-platform", Boolean.parseBoolean(PROPERTIES.getProperty("mapreduce.app-submission.cross-platform")));
        configuration.set("fs.defaultFS", PROPERTIES.getProperty("fs.defaultFS"));
        configuration.set("mapreduce.framework.name", PROPERTIES.getProperty("mapreduce.framework.name"));
        configuration.set("yarn.resourcemanager.address", PROPERTIES.getProperty("yarn.resourcemanager.address"));
        configuration.set("yarn.resourcemanager.scheduler.address", PROPERTIES.getProperty("yarn.resourcemanager.scheduler.address"));
        configuration.set("mapreduce.jobhistory.address", PROPERTIES.getProperty("mapreduce.jobhistory.address"));

    }
}
