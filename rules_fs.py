# coding: utf-8
import os
from processor import register, Rule


class RuleBase(Rule):
    VERSION_CODE = '100000'
    VERSION_NAME = '1.00000'
    APPNAME = '封神小鲜肉'
    APPLABEL = 'fs'


@register
class RuleEmpty(RuleBase):
    LABEL = 'empty'
    DIRECTORY = 'empty'
    CH_NAME = '白包小包'
    SDKTYPE = '0'
    PACKAGE_NAME = 'com.yunyue.fs.yy'


@register
class RuleUC(RuleBase):
    LABEL = 'uc'
    DIRECTORY = 'uc'
    CH_NAME = 'UC小包'
    SDKTYPE = '7'
    PACKAGE_NAME = 'com.yunyue.fs.uc'

    GAMEID = '560262'

    @classmethod
    def rules(cls):
        return super(RuleUC, cls).rules() + [
            ('src/com/ninegame/ucgamesdk/UCSdkConfig.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleC360(RuleBase):
    LABEL = 'c360'
    DIRECTORY = 'c360'
    CH_NAME = '360小包'
    SDKTYPE = '19'
    PACKAGE_NAME = 'com.yunyue.fs.c360'

    QHOPENSDK_APPKEY = '354483f127d6e773c14c21f6b3ef557a'
    QHOPENSDK_PRIVATEKEY = ''
    QHOPENSDK_APPID = '202593516'


@register
class RuleBaidu(RuleBase):
    LABEL = 'baidu'
    DIRECTORY = 'baidu'
    CH_NAME = '百度小包'
    SDKTYPE = '17'
    PACKAGE_NAME = 'com.yunyue.fs.baidu'

    APPID = '6315322'
    APPKEY = 'wLezE8VtTkO6rVkapuWCyxyP'


@register
class RuleXiaomi(RuleBase):
    LABEL = 'xiaomi'
    DIRECTORY = 'xiaomi'
    CH_NAME = '小米小包'
    SDKTYPE = '18'
    PACKAGE_NAME = 'com.yunyue.fs.xiaomi'

    APPID = '2882303761517353553'
    APPKEY = '5501735324553'

    @classmethod
    def rules(cls):
        return super(RuleXiaomi, cls).rules() + [
            ('src/org/yunyue/MiAppApplication.java', 'replace', cls.common_replaces()),
        ]

