#!/usr/bin/env python
# -*- coding: utf-8 -*-

import requests
import settings


def get_session(user, password):
    r = requests.post('%s/login' % (settings.g_url), data={'name': user, 'password': password})
    sessionId = r.json().get('sessionId')

    return sessionId