#!/usr/bin/env bash

# version
version="1.0-SNAPSHOT"

# host address
hostname="172.24.8.98"

# zk address
zkAddress="172.24.8.95:2181,172.24.8.97:2181,172.24.8.98:2181"

# ES address
esAddress="172.24.8.96:9300,172.24.8.97:9300,172.24.8.98:9300"

# es cluster name
esClusterName="udpdev"

# mysql address
mysqlAddress="172.24.8.94:3306"

# mysql db name
mysqlDb="swordfish"

# mysql user name
mysqlUser="udp"

# mysql password
mysqlPassword="Udp-2017"

# hive server address
hiveAddress="172.24.8.95"

# hadoop address
hadoopNamenodeAddress="172.24.8.94"

# hadoop yarn address
hadoopYarnAddress="172.24.8.95"

# env config file
envFile="/opt/udp/.sf_env.sh"

# datax home path
dataxHome="/opt/udp/datax"

# storm path
stormRestAddr="bgs-8p95-zhanglifeng.bfdabc.com:8744"

# develop mode
developMode=true

# home of swordfish
SWORDFISH_HOME="/opt/udp/swordfish-all-${version}/"

# 使用示例
function usage() {
    echo "Usage: $0 -r <true|false> [-m <all|web-server|master-server|exec-server>" 1>&2;
    exit 1;
}

while getopts ":r:m:" o; do
    case "${o}" in
        r)
            r=${OPTARG}
            [[ "$r" = "true" || "$r" = "false" ]] || usage
            ;;
        m)
            m=${OPTARG}
            [[ "$m" = "all" || "$m" = "web-server" || "$m" = "master-server" || "$m" = "exec-server" ]] || usage
            ;;
        *)
            usage
            ;;
    esac
done

if [ -z "${r}" ]; then
    usage
fi

if [ -z "${m}" ]; then
    m="all"
fi

echo "replace: $r, module: $m"

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
    sed -i "s#es.cluster.name.*#es.cluster.name = ${esClusterName}#g" conf/common/search.properties

    sed -i "s#sf.env.file.*#sf.env.file = ${envFile}#g" conf/common/base_config.properties
    sed -i "s#develop.mode.*#develop.mode = ${developMode}#g" conf/common/base_config.properties

    sed -i "s#fs.defaultFS.*#fs.defaultFS = hdfs://${hadoopNamenodeAddress}:8020#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.resourcemanager.address.*#yarn.resourcemanager.address = ${hadoopYarnAddress}:8032#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.resourcemanager.scheduler.address.*#yarn.resourcemanager.scheduler.address = ${hadoopYarnAddress}:8030#g" conf/common/hadoop/hadoop.properties
    sed -i "s#mapreduce.jobhistory.address.*#mapreduce.jobhistory.address = ${hadoopYarnAddress}:10020#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.resourcemanager.webapp.address.*#yarn.resourcemanager.webapp.address = http://${hadoopYarnAddress}:8088/cluster/app/%s#g" conf/common/hadoop/hadoop.properties
    sed -i "s#yarn.application.status.address.*#yarn.application.status.address = http://${hadoopYarnAddress}:8088/ws/v1/cluster/apps/%s#g" conf/common/hadoop/hadoop.properties

    sed -i "s#storm.rest.url.*#storm.rest.url = http://${stormRestAddr}#g" conf/common/storm.properties

    # 4. quartz
    sed -i "s#org.quartz.dataSource.myDS.URL.*#org.quartz.dataSource.myDS.URL= jdbc:mysql://${mysqlAddress}/${mysqlDb}?autoReconnect=true#g" conf/quartz.properties
    sed -i "s#org.quartz.dataSource.myDS.user.*#org.quartz.dataSource.myDS.user = ${mysqlUser}#g" conf/quartz.properties
    sed -i "s#org.quartz.dataSource.myDS.password.*#org.quartz.dataSource.myDS.password = ${mysqlPassword}#g" conf/quartz.properties

    # 5. common.hive 组件下的文件替换
    sed -i "s#hive.metastore.uris.*#hive.metastore.uris = thrift://${hiveAddress}:9083#g" conf/common/hive/hive.properties
    sed -i "s#hive.thrift.uris.*#hive.thrift.uris = jdbc:hive2://${hiveAddress}:10000#g" conf/common/hive/hive.properties
    sed -i "s#hive.uris.*#hive.uris = jdbc:hive2://${hiveAddress}:10000#g" conf/common/hive/hive.properties

    # 6. worker.properties
    sed -i "s#executor.datax.home.*#executor.datax.home = ${dataxHome}#g" conf/worker.properties
}

function process_check()
{
    sleep 3s

    pid=`cat /tmp/*$1.pid`
    ps -fe|grep ${pid}|grep -v grep

    if [ $? -ne 0 ]
    then
        echo "[pid $pid not exist, service is '$1']"
        exit 1
    else
        echo "[pid $pid start success, service is '$1']"
    fi
}

# get script path
CUR_DIR=`dirname $0`

if [ "$r" = "true" ]; then
    echo "exec file replace"

    cd $SWORDFISH_HOME

    file_replace || { echo "conf file replace failed."; exit 1; }
else
    echo "do not exec file replace"
fi

# start all service
cd $SWORDFISH_HOME

if [ "$m" = "all" ] || [ "$m" = "web-server" ]; then
  sh bin/swordfish-daemon.sh stop web-server
  sh bin/swordfish-daemon.sh start web-server

  process_check web-server
fi

if [ "$m" = "all" ] || [ "$m" = "master-server" ]; then
  sh bin/swordfish-daemon.sh stop master-server
  sh bin/swordfish-daemon.sh start master-server

  process_check master-server
fi

if [ "$m" = "all" ] || [ "$m" = "exec-server" ]; then
  sh bin/swordfish-daemon.sh stop exec-server
  sh bin/swordfish-daemon.sh start exec-server

  process_check exec-server
fi

# 查看进程是否存在
sleep 1s
echo "process information:"
jps -lm