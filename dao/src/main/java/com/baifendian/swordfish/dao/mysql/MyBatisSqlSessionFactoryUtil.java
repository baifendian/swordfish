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

package com.baifendian.swordfish.dao.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * MyBatis SqlSessionFactory 工具类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月14日
 */
public class MyBatisSqlSessionFactoryUtil {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisSqlSessionFactoryUtil.class);

    /** {@link SqlSessionFactory} */
    private static SqlSessionFactory sqlSessionFactory;

    /** {@link Properties} */
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            File dataSourceFile = ResourceUtils.getFile("classpath:dao/data_source.properties");
            InputStream is = new FileInputStream(dataSourceFile);
            PROPERTIES.load(is);

            File dataSourceFileHive = ResourceUtils.getFile("classpath:common/hive/hive.properties");
            InputStream isHive = new FileInputStream(dataSourceFileHive);
            PROPERTIES.load(isHive);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 获取 SqlSessionFactory
     * <p>
     *
     * @return {@link SqlSessionFactory}
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            synchronized (MyBatisSqlSessionFactoryUtil.class) {
                if (sqlSessionFactory == null) {
                    DataSource dataSource = getDataSource();
                    TransactionFactory transactionFactory = new JdbcTransactionFactory();

                    Environment environment = new Environment("development", transactionFactory, dataSource);

                    Configuration configuration = new Configuration(environment);
                    configuration.setLazyLoadingEnabled(true);
                    configuration.addMappers("com.baifendian.swordfish.dao.mysql.mapper");

                    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
                    sqlSessionFactory = builder.build(configuration);
                }
            }
        }
        return sqlSessionFactory;
    }

    /**
     * 获取 SqlSession
     * <p>
     *
     * @return {@link SqlSession}
     */
    public static SqlSession getSqlSession() {
        return new SqlSessionTemplate(getSqlSessionFactory());
    }

    /**
     * 获取 DataSource
     * <p>
     *
     * @return
     */
    private static DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(PROPERTIES.getProperty("swordfish.jdbc.url"));
        dataSource.setUsername(PROPERTIES.getProperty("swordfish.jdbc.user"));
        dataSource.setPassword(PROPERTIES.getProperty("swordfish.jdbc.pass"));
        dataSource.setInitialSize(Integer.valueOf(PROPERTIES.getProperty("swordfish.jdbc.initialSize")));
        dataSource.setMinIdle(Integer.valueOf(PROPERTIES.getProperty("swordfish.jdbc.minIdle")));
        dataSource.setMaxActive(Integer.valueOf(PROPERTIES.getProperty("swordfish.jdbc.maxActive")));
        dataSource.setMaxWait(Integer.valueOf(PROPERTIES.getProperty("swordfish.jdbc.maxWait")));
        dataSource.setTimeBetweenEvictionRunsMillis(Long.valueOf(PROPERTIES.getProperty("swordfish.jdbc.timeBetweenEvictionRunsMillis")));
        dataSource.setMinEvictableIdleTimeMillis(Long.valueOf((PROPERTIES.getProperty("swordfish.jdbc.minEvictableIdleTimeMillis"))));
        dataSource.setTestWhileIdle(Boolean.valueOf(PROPERTIES.getProperty("swordfish.jdbc.testWhileIdle")));
        dataSource.setTestOnBorrow(Boolean.valueOf(PROPERTIES.getProperty("swordfish.jdbc.testOnBorrow")));
        dataSource.setTestOnReturn(Boolean.valueOf(PROPERTIES.getProperty("swordfish.jdbc.testOnReturn")));
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1800);
        return dataSource;
    }

    public static List<String> getDataSourceMysql() {
        String dirverClass = PROPERTIES.getProperty("jdbc.driverClassName");
        String url = PROPERTIES.getProperty("swordfish.jdbc.url");
        String user = PROPERTIES.getProperty("swordfish.jdbc.user");
        String password = PROPERTIES.getProperty("swordfish.jdbc.pass");
        List<String> list = new ArrayList<String>();
        list.add(dirverClass);
        list.add(url);
        list.add(user);
        list.add(password);
        return list;
    }

    public static List<String> getDataSourceHive() {
        String dirverClass = PROPERTIES.getProperty("hive.driverClass");
        String url = PROPERTIES.getProperty("hive.uris");
        String user = PROPERTIES.getProperty("hive.root.user");
        String password = PROPERTIES.getProperty("hive.root.password");
        List<String> list = new ArrayList<String>();
        list.add(dirverClass);
        list.add(url);
        list.add(user);
        list.add(password);
        return list;
    }
}
