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

    ZC_APPID = '547347373077037056'
    ZC_RSA = 'MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOUust2OfurxzdifqELDYBBM44tUmqa/Sfqn90MkrjpORe78BuiUU25+JWzxizrgLgSicjH/RHZvWZs2CcZ7v2Q+cjlbMeqth/rDk5p2HW0jpEohkguwKt+2RenPTHzvRhLQ0bdYpYVQN6g8qPOVkkd1rsSVaz69PF5ENljJyw3JAgMBAAECgYEAvTRtdF4Ex8Ay+ejtR5j2gN6JaGjDeHAqCiaLCsKImBgwwhkNNwvlSS4ZhbRwBn43X5og/sfIZKKO7oWRUmytVuubR9eXsKTZGnrN8HgxEvGhlDoEBFu1GoEMmrD+XMAMsC2kKBgxjy315Ildz9LXB8tBMh1FjlQWJIXWUOMiSXUCQQD9uY34hHDFn5Fu1cLTRn8xk0vYjQzYp7FUty5k5CUMGhuSG4jXUJBHDoeXla51vyF74Qq1DOSQW6hVDMeSSa7XAkEA5zzOGfOLYbQhkD5SP6UdfBjeBJ6Em8i0lyuM/xTxEHgs9x8x2UdVUy0BSaVkrjxC4SInaM3ZIQN5o+rdvhy0XwJBANkT03KnpXB/eEdSnjBy5Un+EutAqpgGyUKIwynQxB2ZjLMx2Z8WL4qL1NiNWMkm8LfzL1z9neQgd2Hk4C652dsCQCAkramh1yAvv/KjFx/NvfmAI2yU9G4LSj8xSJo0uQXHDskTRwSjC9NSEDnCiepGai2NZ9kDtEkIiKImhchliRUCQHRacVqhfPqVjmx9HugQt1FvDnq1T+nKsYImZSKYi8oGGItXOX+alRIcflOWZk3zUpNO2Sbdx9LaugznUlYzib8='


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
