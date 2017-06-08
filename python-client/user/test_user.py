#!/usr/bin/env python
# -*- coding: utf-8 -*-

import unittest


class TestUser(unittest.TestCase):
    def setUp(self):
        print 'setUp...'

    def tearDown(self):
        print 'tearDown...'

    def test1(self):
        self.assertEqual(2, 2)
