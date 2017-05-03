#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
import settings
from login import login


def create_project(sessionId, name, desc):
    '''创建项目'''
    r = requests.post('%s/projects/%s' % (settings.g_url, name),
                      headers={'sessionId': sessionId},
                      data={'desc': desc})

    return (r.status_code, r.json())


def modify_project(sessionId, name, desc):
    '''修改项目'''
    r = requests.patch('%s/projects/%s' % (settings.g_url, name),
                       headers={'sessionId': sessionId},
                       data={'desc': desc})

    return (r.status_code, r.json())


def delete_project(sessionId, name):
    '''删除项目'''
    r = requests.delete('%s/projects/%s' % (settings.g_url, name),
                        headers={'sessionId': sessionId}
                        )

    return r.status_code


def query_project_list(sessionId):
    '''查询项目列表'''
    r = requests.get('%s/projects' % (settings.g_url),
                        headers={'sessionId': sessionId}
                        )

    return (r.status_code, r.json())


if __name__ == '__main__':
    sessionId = login.get_session(settings.g_user, settings.g_password)

    print sessionId

    status = delete_project(sessionId, settings.g_project)

    print status

    (status, data) = create_project(sessionId, settings.g_project, "project dec")

    print status, json.dumps(data, indent=4)

    (status, data) = query_project_list(sessionId)

    print status, json.dumps(data, indent=4)
