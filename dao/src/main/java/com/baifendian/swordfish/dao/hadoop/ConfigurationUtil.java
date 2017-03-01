/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月3日
 * File Name      : ConfigurationUtil.java
 */

package com.baifendian.swordfish.dao.hadoop;

import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisSqlSessionFactoryUtil.class);

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
