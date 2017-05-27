#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import settings
from login import login


def create_streaming(sessionId, projectName, name, desc, type, parameter, userDefParams):
    '''创建流任务'''
    r = requests.post('%s//projects/%s/streaming/%s' % (settings.g_url, projectName, name),
                      headers={'sessionId': sessionId},
                      data={'desc': desc, 'type': type, 'parameter': parameter, 'userDefParams': userDefParams})

    return (r.status_code, r.json())


def modify_and_create_streaming(sessionId, projectName, name, desc, type, parameter, userDefParams):
    '''修改流任务, 没有则创建'''
    r = requests.put('%s//projects/%s/streaming/%s' % (settings.g_url, projectName, name),
                     headers={'sessionId': sessionId},
                     data={'desc': desc, 'type': type, 'parameter': parameter, 'userDefParams': userDefParams})

    return (r.status_code, r.json())


def modify_streaming(sessionId, projectName, name, desc, parameter, userDefParams):
    '''修改流任务'''
    r = requests.patch('%s//projects/%s/streaming/%s' % (settings.g_url, projectName, name),
                       headers={'sessionId': sessionId},
                       data={'desc': desc, 'parameter': parameter, 'userDefParams': userDefParams})

    return (r.status_code, r.json())


def delete_streaming(sessionId, projectName, name):
    '''修改流任务'''
    r = requests.delete('%s//projects/%s/streaming/%s' % (settings.g_url, projectName, name),
                        headers={'sessionId': sessionId})

    return r.status_code


def query_project_streamings(sessionId, projectName):
    '''修改流任务'''
    r = requests.get('%s//projects/%s/streamings' % (settings.g_url, projectName),
                     headers={'sessionId': sessionId})

    return (r.status_code, r.json())


def query_project_streaming(sessionId, projectName, name):
    '''修改流任务'''
    r = requests.get('%s//projects/%s/streaming/%s' % (settings.g_url, projectName, name),
                     headers={'sessionId': sessionId})

    return (r.status_code, r.json())


if __name__ == '__main__':
    sessionId = login.get_session(settings.g_user, settings.g_password)

    print sessionId

    print 'create streaming...'
    (status, data) = create_streaming(sessionId,
                                      settings.g_project,
                                      "streaming_01",
                                      "test streaming task",
                                      "SPARK_STREAMING",
                                      json.dumps({
                                          "mainClass": "com.baifendian.spark.WordCount",
                                          "mainJar": {
                                              "scope": "PROJECT",
                                              "res": "spark-wc-examples.jar"
                                          }
                                      }),
                                      None)

    print status, json.dumps(data, indent=4)

    print 'modify and create streaming...'
    (status, data) = modify_and_create_streaming(sessionId,
                                                 settings.g_project,
                                                 "streaming_02",
                                                 "test streaming task",
                                                 "SPARK_STREAMING",
                                                 json.dumps({
                                                     "mainClass": "com.baifendian.spark.WordCount",
                                                     "mainJar": {
                                                         "scope": "PROJECT",
                                                         "res": "spark-wc-examples.jar"
                                                     }
                                                 }),
                                                 json.dumps([{"key1": "value1"}]))

    print status, json.dumps(data, indent=4)

    print 'modify streaming...'
    (status, data) = modify_streaming(sessionId,
                                      settings.g_project,
                                      "streaming_01",
                                      "test streaming task 哈哈哈哈哈哈哈",
                                      json.dumps({
                                          "mainClass": "com.baifendian.spark.WordCount",
                                          "mainJar": {
                                              "scope": "PROJECT",
                                              "res": "spark-wc-examples.jar"
                                          }
                                      }),
                                      None)

    print status, json.dumps(data, indent=4)

    print 'delete...'
    status = delete_streaming(sessionId, settings.g_project, "streaming_01")

    print status

    print 'query project streamings...'
    (status, data) = query_project_streamings(sessionId, settings.g_project)

    print status, json.dumps(data, indent=4)

    print 'query project streaming...'
    (status, data) = query_(sessionId, settings.g_project, "streaming_02")

    print status, json.dumps(data, indent=4)
