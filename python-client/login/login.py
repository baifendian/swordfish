#!/usr/bin/env python
# -*- coding: utf-8 -*-

import requests
import settings


def get_session(user, password):
    '''登陆, 获取 session 信息'''
    r = requests.post('%s/login' % (settings.g_url), data={'name': user, 'password': password})

    return r.json().get('sessionId')