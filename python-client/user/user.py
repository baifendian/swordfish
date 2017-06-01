#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import settings
from login import login


def add_user(sessionId, name, email, desc, password, phone, proxyUsers):
    '''添加一个用户'''
    r = requests.post('%s/users/%s' % (settings.g_url, name),
                      headers={'sessionId': sessionId},
                      data={'email': email,
                            'desc': desc,
                            'password': password,
                            'phone': phone,
                            'proxyUsers': json.dumps(proxyUsers)})

    return (r.status_code, r.json())


def modify_user(sessionId, name, email, desc, password, phone, proxyUsers):
    '''修改一个用户'''
    r = requests.patch('%s/users/%s' % (settings.g_url, name),
                       headers={'sessionId': sessionId},
                       data={'email': email,
                             'desc': desc,
                             'password': password,
                             'phone': phone,
                             'proxyUsers': json.dumps(proxyUsers)})

    return (r.status_code, r.json())


def delete_user(sessionId, name):
    '''删除用户'''
    r = requests.delete('%s/users/%s' % (settings.g_url, name),
                        headers={'sessionId': sessionId})

    return r.status_code


def query_user(sessionId, allUser):
    '''查询用户'''
    r = requests.get('%s/users' % (settings.g_url),
                     headers={'sessionId': sessionId},
                     params={'allUser': allUser})

    return (r.status_code, r.json())


if __name__ == '__main__':
    sessionId = login.get_session(settings.g_admin_user, settings.g_admin_password)

    print sessionId

    for i in xrange(1, 10):
        user = "%s_%d" %(settings.g_user, 102)
        email = "%s_%d" %(settings.g_email, 102)

        (status, data) = add_user(sessionId, user, email, None, settings.g_password, None, ["*"])

        print status, json.dumps(data, indent=4)

    # (status, data) = query_user(sessionId, True)
    #
    # print status, json.dumps(data, indent=4)
