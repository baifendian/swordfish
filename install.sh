#!/usr/bin/env bash

# version
version="1.0-SNAPSHOT"

# host address
hostname="172.24.5.149"

# zk address
zkAddress="172.24.5.149:2181,172.24.5.150:2181,172.24.5.151:2181"

# ES address
esAddress="172.24.5.149:9300,172.24.5.150:9300,172.24.5.151:9300"

# mysql address
mysqlAddress="172.24.5.149:3306"

# mysql db name
mysqlDb="swordfish"

# mysql user name
mysqlUser="swordfish"

# mysql password
mysqlPassword="swordfish"

# hive server address
hiveAddress="172.24.5.149"

# hadoop address
hadoopNamenodeAddress="172.24.5.149"

# hadoop yarn address
hadoopYarnAddress="172.24.5.149"

# env config file
envFile="~/.sf_env.sh"

# 文件替换
function file_replace()
{
    # 1. data source
    sed -i "s#spring.datasource.url.*#spring.datasource.url = jdbc:mysql://${mysqlAddress}/${mysqlDb}?autoReconnect=true#g" conf/dao/data_source.properties
    sed -i "s#spring.datasource.username.*#spring.datasource.username = ${mysqlUser}#g" conf/dao/data_source.properties
    sed -i "s#spring.datasource.password.*#spring.datasource.password = ${mysqlPassword}#g" conf/dao/data_source.properties

    # 2. application

    # 3. common 组件下的文件替换
    sed -i "s#es.address.*#es.address = ${esAddress}#g" conf/common/search.properties

    sed -i "s#sf.env.file.*#sf.env.file = ${envFile}#g" conf/common/base_config.properties

    sed -i "s#fs.defaultFS.*#fs.defaultFS = hdfs://${hadoopNamenodeAddress}:8020#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.resourcemanager.address.*#yarn.resourcemanager.address = ${hadoopYarnAddress}:8032#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.resourcemanager.scheduler.address.*#yarn.resourcemanager.scheduler.address = ${hadoopYarnAddress}:8030#g" conf/common/hadoop/hadoop.properties
    sed -i "s#mapreduce.jobhistory.address.*#mapreduce.jobhistory.address = ${hadoopYarnAddress}:10020#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.resourcemanager.webapp.address.*#yarn.resourcemanager.webapp.address = ${hadoopYarnAddress}:8088#g" conf/common/hadoop/hadoop.properties

    # 4. quartz
    if [ "$1" = "master-server" ]; then
        sed -i "s#org.quartz.dataSource.myDS.URL.*#org.quartz.dataSource.myDS.URL= jdbc:mysql://${mysqlAddress}/${mysqlDb}?autoReconnect=true#g" conf/quartz.properties
        sed -i "s#org.quartz.dataSource.myDS.user.*#org.quartz.dataSource.myDS.user = ${mysqlUser}#g" conf/quartz.properties
        sed -i "s#org.quartz.dataSource.myDS.password.*#org.quartz.dataSource.myDS.password = ${mysqlPassword}#g" conf/quartz.properties
    fi

    # 5. common.hive 组件下的文件替换
    if [ "$1" = "exec-server" ]; then
        sed -i "s#hive.metastore.uris.*#hive.metastore.uris = thrift://${hiveAddress}:9083#g" conf/common/hive/hive.properties
        sed -i "s#hive.thrift.uris.*#hive.thrift.uris = jdbc:hive2://${hiveAddress}:10000/%s#g" conf/common/hive/hive.properties
        sed -i "s#hive.uris.*#hive.uris = jdbc:hive2://${hiveAddress}:10000#g" conf/common/hive/hive.properties
    fi
}

# stop all service
sh bin/swordfish-daemon.sh stop web-server
sh bin/swordfish-daemon.sh stop master-server
sh bin/swordfish-daemon.sh stop exec-server

# compile project
mvn -U clean package assembly:assembly -Dmaven.test.skip=true || { echo "maven failed."; exit 1; }

# get script path
CUR_DIR=`dirname $0`
SWORDFISH_HOME=`cd "$CUR_DIR"; pwd`

# web-server
cd $SWORDFISH_HOME/target/swordfish-all-${version}/swordfish-web-server-${version}/

file_replace web-server || { echo "Web server conf replace failed."; exit 1; }

# master-server
cd $SWORDFISH_HOME/target/swordfish-all-${version}/swordfish-master-server-${version}/

file_replace master-server || { echo "Master server conf replace failed."; exit 1; }

# exec-server
cd $SWORDFISH_HOME/target/swordfish-all-${version}/swordfish-exec-server-${version}/

file_replace exec-server || { echo "Exec server conf replace failed."; exit 1; }

# start all service
sh bin/swordfish-daemon.sh start web-server
sh bin/swordfish-daemon.sh start master-server
sh bin/swordfish-daemon.sh start exec-server
