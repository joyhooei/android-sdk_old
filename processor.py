'''
rules = [file_pattern, command, args]
'''
import os
import glob
from collections import OrderedDict


def command_replace(path, trans):
    s = open(path, 'rb').read()
    for key, value in trans.items():
        s = s.replace('${%s}' % key, value)
    open(path, 'wb').write(s)


def command_copy(path_to, path_from):
    os.system('cp %s %s' % (path_from, path_to))


def process(rules):
    for pattern, command, args in rules:
        for path in glob.glob(pattern):
            globals()['command_'+command](path, args)


class Rule(object):
    @classmethod
    def common_replaces(cls):
        r = {}
        for k in dir(cls):
            v = getattr(cls, k)
            if isinstance(v, basestring) and not k.startswith('__'):
                r[k] = v
        return r

    @classmethod
    def rules(cls):
        return [
            ('AndroidManifest.xml', 'replace', cls.common_replaces()),
            ('src/org/weilan/GameProxyImpl.java', 'replace', cls.common_replaces()),
            ('res/values/strings.xml', 'replace', cls.common_replaces()),
        ]

all_rules = OrderedDict()


def register(cls):
    all_rules[cls.LABEL] = cls
    return cls
