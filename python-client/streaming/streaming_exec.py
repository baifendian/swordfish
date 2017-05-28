#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import settings
from login import login
import streaming
import sys


def exec_streaming(sessionId, projectName, name, proxyUser, queue):
    '''执行流任务'''
    r = requests.post('%s//executors/streaming' % (settings.g_url),
                      headers={'sessionId': sessionId},
                      data={'projectName': projectName,
                            'name': name,
                            'proxyUser': proxyUser,
                            'queue': queue})

    return (r.status_code, r.json())


def kill_exec(sessionId, execId):
    '''杀死流任务'''
    r = requests.post('%s//executors/streaming/%s/kill' % (settings.g_url, execId),
                      headers={'sessionId': sessionId})

    return r.status_code


def get_streaming_execs(sessionId, startDate, endDate, projectName, name, status, _from, size):
    '''得到流任务执行情况'''
    r = requests.get('%s//executors/streamings' % (settings.g_url),
                     headers={'sessionId': sessionId},
                     params={'startDate': startDate,
                             'endDate': endDate,
                             'projectName': projectName,
                             'name': name,
                             'status': status,
                             'from': _from,
                             'size': size})

    return (r.status_code, r.json())


def get_streaming_latest_exec(sessionId, projectName, names):
    r = requests.get('%s//executors/streaming/latest' % (settings.g_url),
                     headers={'sessionId': sessionId},
                     params={'projectName': projectName,
                             'names': names})

    return (r.status_code, r.json())


def get_streaming_exec_detail(sessionId, execId):
    r = requests.get('%s//executors/streaming/%s' % (settings.g_url, execId),
                     headers={'sessionId': sessionId})

    return (r.status_code, r.json())


def get_logs(sessionId, execId, _from, size):
    r = requests.get('%s//executors/streaming/%s/logs' % (settings.g_url, execId),
                     headers={'sessionId': sessionId},
                     params={'from': _from,
                             'size': size})

    return (r.status_code, r.json())


if __name__ == '__main__':
    sessionId = login.get_session(settings.g_user, settings.g_password)

    print sessionId

    streamingName = sys.argv[1]

    print 'streaming name: %s' % (streamingName)

    print 'create streaming...'
    (status, data) = streaming.create_streaming(sessionId,
                                                settings.g_project,
                                                streamingName,
                                                "test streaming task",
                                                "SPARK_STREAMING",
                                                json.dumps({
                                                    "mainClass": "org.apache.spark.examples.streaming.DirectKafkaWordCount",
                                                    "mainJar": {
                                                        "scope": "PROJECT",
                                                        "res": "spark-examples-1.0-SNAPSHOT-hadoop2.6.0.jar"
                                                    },
                                                    "args": "172.18.1.22:9092,172.18.1.23:9092,172.18.1.24:9092 test_01",
                                                    "driverCores": 1,
                                                    "driverMemory": "1024M",
                                                    "numExecutors": 1,
                                                    "executorMemory": "1024M",
                                                    "executorCores": 1
                                                }),
                                                None)

    print status, json.dumps(data, indent=4)

    print 'exec streaming...'
    (status, data) = exec_streaming(sessionId,
                                    settings.g_project,
                                    streamingName,
                                    "qifeng.dai",
                                    'alg')

    print status, json.dumps(data, indent=4)

    execId = data.get("execId")

    print 'kill exec of %s...' % (execId)
    status = kill_exec(sessionId, execId)

    print status

    print 'get streaming execs...'
    (status, data) = get_streaming_execs(sessionId, 0, 100000000000000,
                                         settings.g_project, None, 0, 0, 100)

    print status, json.dumps(data, indent=4)

    print 'get steraming latest exec...'
    (status, data) = get_streaming_latest_exec(sessionId, settings.g_project, json.dumps(['abc', streamingName]))

    print status, json.dumps(data, indent=4)

    print 'get streaming detail of %s...' % (execId)
    (status, data) = get_streaming_exec_detail(sessionId, execId)

    print status, json.dumps(data, indent=4)

    print 'get log of %s...' % (execId)
    (status, data) = get_logs(sessionId, execId, 0, 100)

    print status, json.dumps(data, indent=4)