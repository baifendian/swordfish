#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import time
from login import login
import settings


def exec_adhoc(sessionId, projectName, stms, limit, proxyUser, queue, udfs, timeout):
    # 发送一个即席的执行请求
    r = requests.post('%s/projects/%s/adHoc' % (settings.g_url, projectName),
                      headers={'sessionId': sessionId},
                      data={'stms': stms,
                            'limit': limit,
                            'proxyUser': proxyUser,
                            'queue': queue,
                            'udfs': json.dumps(udfs),
                            'timeout': timeout})

    return (r.status_code, r.json())


def query_logs(sessionId, execId, index, _from, size):
    '''查询日志'''
    r = requests.get('%s/adHoc/%s/logs?' % (settings.g_url, execId),
                     headers={'sessionId': sessionId},
                     params={'index': index,
                             'from': _from,
                             'size': size})

    return (r.status_code, r.json())


def query_result(sessionId, execId, index):
    '''查询结果'''
    r = requests.get('%s/adHoc/%s/result?' % (settings.g_url, execId),
                     headers={'sessionId': sessionId},
                     params={'index': index})

    return (r.status_code, r.json())


if __name__ == '__main__':

    sessionId = login.get_session(settings.g_user, settings.g_password)

    print sessionId

    (status, result) = exec_adhoc(sessionId,
                                  settings.g_project,
                                  "--select current_timestamp() ,current_timestamp() from shuanghu_db.test_01 limit 1;\n\nselect * from shuanghu_db.test_01 limit 1;",
                                  1000,
                                  "shuanghu",
                                  "others",
                                  None,
                                  None)

    execId = result.get('execId') if status < 300 and status >= 200 else None

    print execId, result

    index = 0
    _from = 0

    time.sleep(2)

    (status, result) = query_logs(sessionId, 205, index, _from, 1000)

    print 'Get log %s, %s' % (status, result)