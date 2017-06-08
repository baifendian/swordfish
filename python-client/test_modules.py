#!/usr/bin/env python
# -*- coding: utf-8 -*-

import unittest
from adhoc.test_adhoc import TestAdhoc
from login.test_login import TestLogin
from project.test_project import TestProject
from streaming.test_streaming import TestStreaming
from streaming.test_streaming_exec import TestStreamingExec
from user.test_user import TestUser

suite = unittest.TestSuite()

suite.addTest(unittest.makeSuite(TestLogin))
suite.addTest(unittest.makeSuite(TestUser))
suite.addTest(unittest.makeSuite(TestProject))
suite.addTest(unittest.makeSuite(TestAdhoc))
suite.addTest(unittest.makeSuite(TestStreaming))
suite.addTest(unittest.makeSuite(TestStreamingExec))

unittest.TextTestRunner(verbosity=2).run(suite)
