#!/usr/bin/env python
# coding: utf-8
import os
from processor import process


def d(f, t, *args):
    return (f, t, args)


def register(container):
    def decor(cls):
        container[cls.LABEL] = cls
        return cls
    return decor


common_rules = [
    d('AndroidManifest.xml', 'replace', {
        'VERSION_CODE': '120005',
        'VERSION_NAME': '1.20005',
    }),
]


g_rules = {}


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

        return common_rules + [
            d('AndroidManifest.xml', 'replace', cls.common_replaces()),
            d('src/org/yunyue/GameProxyImpl.java', 'replace', cls.common_replaces()),
        ]


@register(g_rules)
class RuleZC(Rule):
    LABEL = 'zc'
    DIRECTORY = 'zc'
    CH_NAME = '筑巢小包'

    PACKAGE_NAME = 'com.yunyue.ttxm.zclh'
    TD_APPID = 'f7eac3e7b3014a5487f69f85c07069e6'
    TD_CHANNEL = ''


@register(g_rules)
class RuleZCBaidu(RuleZC):
    LABEL = 'zc_baidu'
    CH_NAME = '筑巢百度小包'

    TD_APPID = '2c09c858fb8d4b468fdb26427ec6d18c'


@register(g_rules)
class RuleZCShenma(RuleZC):
    LABEL = 'zc_shenma'
    CH_NAME = '筑巢神马小包'
    TD_APPID = '8f7101f78ab24b34b75c07709bf9cff0'


@register(g_rules)
class RuleZCSougou(RuleZC):
    LABEL = 'zc_sougou'
    CH_NAME = '筑巢搜狗小包'
    TD_APPID = '7ac71c291f484eeba7b9feb801f33457'


def main(desc):
    for label, rule in desc.items():
        os.chdir('sdks/%s' % rule.DIRECTORY)
        os.system('git clean -f -d .')
        process(rule.rules())
        # preview replaces
        os.system('git diff -p --raw .')
        #os.system('ant linkassets release')
        os.system('git clean -f -d .')
        os.system('git checkout -- .')
        # TODO copy package
        os.chdir('../..')

if __name__ == '__main__':
    main(g_rules)
