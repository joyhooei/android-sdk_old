# coding: utf-8
import os
from processor import register, Rule


class RuleBase(Rule):
    VERSION_CODE = '120005'
    VERSION_NAME = '1.20005'


@register
class RuleZC(RuleBase):
    LABEL = 'zc'
    DIRECTORY = 'zc'
    CH_NAME = '筑巢小包'
    SDKTYPE = '9'
    PACKAGE_NAME = 'com.yunyue.ttxm.zclh'
    CHANNEL = ''

    TD_APPID = 'f7eac3e7b3014a5487f69f85c07069e6'
    TD_CHANNEL = ''

    ZC_APPID = '547347373077037056'
    ZC_RSA = 'MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOUust2OfurxzdifqELDYBBM44tUmqa/Sfqn90MkrjpORe78BuiUU25+JWzxizrgLgSicjH/RHZvWZs2CcZ7v2Q+cjlbMeqth/rDk5p2HW0jpEohkguwKt+2RenPTHzvRhLQ0bdYpYVQN6g8qPOVkkd1rsSVaz69PF5ENljJyw3JAgMBAAECgYEAvTRtdF4Ex8Ay+ejtR5j2gN6JaGjDeHAqCiaLCsKImBgwwhkNNwvlSS4ZhbRwBn43X5og/sfIZKKO7oWRUmytVuubR9eXsKTZGnrN8HgxEvGhlDoEBFu1GoEMmrD+XMAMsC2kKBgxjy315Ildz9LXB8tBMh1FjlQWJIXWUOMiSXUCQQD9uY34hHDFn5Fu1cLTRn8xk0vYjQzYp7FUty5k5CUMGhuSG4jXUJBHDoeXla51vyF74Qq1DOSQW6hVDMeSSa7XAkEA5zzOGfOLYbQhkD5SP6UdfBjeBJ6Em8i0lyuM/xTxEHgs9x8x2UdVUy0BSaVkrjxC4SInaM3ZIQN5o+rdvhy0XwJBANkT03KnpXB/eEdSnjBy5Un+EutAqpgGyUKIwynQxB2ZjLMx2Z8WL4qL1NiNWMkm8LfzL1z9neQgd2Hk4C652dsCQCAkramh1yAvv/KjFx/NvfmAI2yU9G4LSj8xSJo0uQXHDskTRwSjC9NSEDnCiepGai2NZ9kDtEkIiKImhchliRUCQHRacVqhfPqVjmx9HugQt1FvDnq1T+nKsYImZSKYi8oGGItXOX+alRIcflOWZk3zUpNO2Sbdx9LaugznUlYzib8='


@register
class RuleZCBaidu(RuleZC):
    LABEL = 'zc_baidu'
    CH_NAME = '筑巢百度小包'

    TD_APPID = '2c09c858fb8d4b468fdb26427ec6d18c'


@register
class RuleZCShenma(RuleZC):
    LABEL = 'zc_shenma'
    CH_NAME = '筑巢神马小包'
    TD_APPID = '8f7101f78ab24b34b75c07709bf9cff0'


@register
class RuleZCSougou(RuleZC):
    LABEL = 'zc_sougou'
    CH_NAME = '筑巢搜狗小包'
    TD_APPID = '7ac71c291f484eeba7b9feb801f33457'


ZC_CHANNELS = [
    ('qixiazi', '七匣子'),
    ('youyi', '优艺市场'),
    ('anjingling', '安精灵'),
    ('jufeng', '聚丰网络'),
    ('kaopu', '靠谱助手'),
    ('shouyou', '手游之家'),
    ('anfeng', '安锋网'),
]
for label, name in ZC_CHANNELS:
    register(type('RuleZC%s' % label, (RuleZC,), dict(
        LABEL='zc_%s' % label,
        CH_NAME='筑巢%s小包' % name,
        CHANNEL=label,
    )))


@register
class RuleXiaomi(RuleBase):
    LABEL = 'xiaomi'
    DIRECTORY = 'xiaomi'
    CH_NAME = '小米小包'
    SDKTYPE = '18'
    PACKAGE_NAME = 'com.yunyue.nzgl.xiaomi'

    APPID = '2882303761517320056'
    APPKEY = '5871732088056'

    @classmethod
    def rules(cls):
        return super(RuleXiaomi, cls).rules() + [
            ('src/org/yunyue/MiAppApplication.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleUC(RuleBase):
    LABEL = 'uc'
    DIRECTORY = 'uc'
    CH_NAME = 'UC小包'
    SDKTYPE = '7'
    PACKAGE_NAME = 'com.yunyue.nzgl.uc'

    GAMEID = '551539'

    @classmethod
    def rules(cls):
        return super(RuleUC, cls).rules() + [
            ('src/com/ninegame/ucgamesdk/UCSdkConfig.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleBaidu(RuleBase):
    LABEL = 'baidu'
    DIRECTORY = 'baidu'
    CH_NAME = '百度小包'
    SDKTYPE = '17'
    PACKAGE_NAME = 'com.yunyue.nzgl.baidu'

    APPID = '5584487'
    APPKEY = 'fuZz2kKfl7hdzilGuaxcK4BY'


@register
class RuleAnzhi(RuleBase):
    LABEL = 'anzhi'
    DIRECTORY = 'anzhi'
    CH_NAME = '安智小包'
    SDKTYPE = '21'
    PACKAGE_NAME = 'com.yunyue.nzgl.anzhi'

    APPKEY = '14280503379sMl6lAYp04RUefHl2mq'
    APPSECRET = '4acD86kGk52n0KI3wk8dD6pY'

    @classmethod
    def rules(cls):
        return super(RuleAnzhi, cls).rules() + [
            ('src/org/yunyue/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleC360(RuleBase):
    LABEL = 'c360'
    DIRECTORY = 'c360'
    CH_NAME = '360小包'
    SDKTYPE = '19'
    PACKAGE_NAME = 'com.yunyue.nzgl.c360'

    QHOPENSDK_APPKEY = 'af4b8465b38059cb1f416c6cbcd21003'
    QHOPENSDK_PRIVATEKEY = '218db57f401f45ff10f94a2add643e73'
    QHOPENSDK_APPID = '202359551'


@register
class RuleOppo(RuleBase):
    LABEL = 'oppo'
    DIRECTORY = 'oppo'
    CH_NAME = 'OPPO小包'
    SDKTYPE = '20'
    PACKAGE_NAME = 'com.yunyue.nzgl.nearme.gamecenter'
    YY_PACKAGE_NAME = 'com.yunyue.nzgl.oppo'

    APPID = '\\ 3370'
    APPKEY = '4uy6F890d9Q8ooSKkGkg4gw8s'
    APPSECRET = '40a84eAC43C5b7347bAE858514053C28'


@register
class RuleYYB(RuleBase):
    LABEL = 'yyb'
    DIRECTORY = 'yyb'
    CH_NAME = '应用宝小包'
    SDKTYPE = '24'
    PACKAGE_NAME = 'com.tencent.tmgp.NZGLDH'
    YY_PACKAGE_NAME = 'com.yunyue.nzgl.yyb'
    APP_NAME = '哪吒归来HD'

    CREATE_ORDER_URL = 'http://yyb.nataku.yunyuegame.com/sdk/android/sdk/yyb/create_order'
    QUERY_BALANCE_URL = 'http://yyb.nataku.yunyuegame.com/sdk/android/sdk/yyb/query_balance'

    QQ_APPID = '1104480701'
    QQ_APPKEY = 'R8U6PCBOw3sX64H0'
    WX_APPID = 'wx2ab37fb74e206f3d'
    WX_APPKEY = 'c032d3e153a0e477fae08328153e35cf'

    @classmethod
    def rules(cls):
        return super(RuleYYB, cls).rules() + [
            ('res/values/strings.xml', 'replace', cls.common_replaces()),
        ]


@register
class RuleWDJ(RuleBase):
    LABEL = 'wdj'
    DIRECTORY = 'wdj'
    CH_NAME = '豌豆荚小包'
    SDKTYPE = '22'
    PACKAGE_NAME = 'com.yunyue.nzgl.wdj'

    APPKEY = '100024673'
    SECURITY_KEY = '20734a7a12cecdd660aba9665a083cb6'

    @classmethod
    def rules(cls):
        return super(RuleWDJ, cls).rules() + [
            ('src/org/yunyue/sdk_app.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleWanka(RuleBase):
    LABEL = 'wanka'
    DIRECTORY = 'wanka'
    CH_NAME = '硬核小包'
    SDKTYPE = '23'
    PACKAGE_NAME = 'com.yunyue.nzgl.wanka'
    YY_PACKAGE_NAME = 'com.yunyue.nzgl.wanka'
    CREATE_ORDER_URL = 'http://sdk.nataku.yunyuegame.com/sdk/android/sdk/wanka/create_order'


@register
class RuleEmpty(RuleBase):
    LABEL = 'empty'
    DIRECTORY = 'empty'
    CH_NAME = '白包小包'
    SDKTYPE = '0'
    PACKAGE_NAME = 'com.yunyue.nzgl.yy'


@register
class RuleGfan(RuleBase):
    LABEL = 'gfan'
    DIRECTORY = 'gfan'
    CH_NAME = '机锋小包'
    SDKTYPE = '28'
    PACKAGE_NAME = 'com.yunyue.nzgl.gfan'


@register
class RuleMM(RuleBase):
    LABEL = 'mm'
    DIRECTORY = 'mm'
    CH_NAME = '移动MM小包'
    SDKTYPE = '29'
    PACKAGE_NAME = 'com.yunyue.nzgl.mm'

    APPID = ''
    APPKEY = ''


@register
class RuleM4399(RuleBase):
    LABEL = 'm4399'
    DIRECTORY = 'm4399'
    CH_NAME = '4399小包'
    SDKTYPE = '30'
    PACKAGE_NAME = 'com.yunyue.nzgl.m4399'

    APPKEY = '103797'
