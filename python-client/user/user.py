#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys

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


if __name__ == '__main__':
    sessionId = login.get_session(settings.g_admin_user, settings.g_admin_password)

    print sessionId

    (status, data) = add_user(sessionId, settings.g_user, settings.g_email, None, settings.g_password, None, ["*"])

    print status, json.dumps(data, indent=4)