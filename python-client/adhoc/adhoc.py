#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import time
from login import login

sessionId = login.get_session()

def exec_adhoc(sessionId, data):
    # 发送一个即席的执行请求
    r = requests.post('%s/projects/bdi/adHoc' %(host),
                      headers={'sessionId': sessionId},
                      data={'proxyUser': 'swordfish', 'queue': 'others',
                            'stms': "show databases;use bfd_test; use bfd;select count(*) from bfd_test.test;select * from bfd_test.test;",
                            'udfs': json.dumps([{"func": "md5", "className": "com.baifendian.hive.udf.Md5",
                                                 "libJars": [{"scope": "project", "res": "udf.jar"}]}]),
                            "limit": 2000})

    printf(r)

    execId = r.json().get('execId')

    index = 0

def query_adhoc():
while True:
    # 查询日志
    r = requests.get('http://%s:12345/adHoc/%s/logs?' % (host, execId),
                     headers={'sessionId': sessionId},
                     params={'index': index, 'from': 0, 'size': 1000})

    printf(r)

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

if __name__ == '__main__':
