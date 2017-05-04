#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import time
from login import login
import settings

def exec_adhoc(sessionId, projectName, stms, limit, proxyUser, queue, udfs, timeout):
    # 发送一个即席的执行请求
    r = requests.post('%s/projects/%s/adHoc' %(settings.g_url, projectName),
                      headers={'sessionId': sessionId},
                      data={'stms': stms,
                            'limit': limit,
                            'proxyUser': proxyUser,
                            'queue': queue,
                            'udfs': json.dumps(udfs),
                            'timeout': timeout})

    return r.json().get('execId')

def query_logs(sessionId, execId):
    '''查询日志'''
    while True:
        # 查询日志
        r = requests.get('http://%s:12345/adHoc/%s/logs?' % (host, execId),
                         headers={'sessionId': sessionId},
                         params={'index': index, 'from': 0, 'size': 1000})


        # 如果有结果则查询结果
        if r.json().get('hasResult') == True:
            r2 = requests.get('http://%s:12345/adHoc/%s/result?' % (host, execId),
                              headers={'sessionId': sessionId},
                              params={'index': index})

            printf(r2)

            index = index + 1

            if r.json().get('lastSql') == True:
                break

        time.sleep(2)

def query_result(sessionId, execId, index):
    '''查询结果'''

if __name__ == '__main__':
