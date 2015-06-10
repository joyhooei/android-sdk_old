# coding: utf-8
import os
from processor import register, Rule


class RuleBase(Rule):
    VERSION_CODE = '120005'
    VERSION_NAME = '1.20005'
    APPNAME = '哪吒归来'


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
    ('hulizhushou', '狐狸助手'),
    ('fengyou', '疯游网'),
    ('zhidian', '指点传媒'),
    ('wx', 'WX'),
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
    PACKAGE_NAME = 'com.tencent.tmgp.NZGLHD'
    YY_PACKAGE_NAME = 'com.yunyue.nzgl.yyb'
    APP_NAME = '哪吒归来HD'

    CREATE_ORDER_URL = 'http://yyb.nataku.yunyuegame.com/sdk/android/sdk/yyb/create_order'
    QUERY_BALANCE_URL = 'http://yyb.nataku.yunyuegame.com/sdk/android/sdk/yyb/query_balance'

    QQ_APPID = '1104618329'
    QQ_APPKEY = 'DXJkUjT2XV5CBCiR'
    WX_APPID = 'wxc307ae35469f7c29'
    WX_APPKEY = 'c1ccf8682a1e4628a1aea47062b7c1d3'

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

    APPKEY = '653373561'


@register
class RuleMM(RuleBase):
    LABEL = 'mm'
    DIRECTORY = 'mm'
    CH_NAME = '移动MM小包'
    SDKTYPE = '29'
    PACKAGE_NAME = 'com.yunyue.nzgl.mm'

    APPID = '300009025525'
    APPKEY = '44023CCE65DBC4F88D7A9888F65F8ADD'
    PARTNER = '2088711460351330'
    SELLER = '2064427543@qq.com'
    RSA_PRIVATE = ''


@register
class RuleM4399(RuleBase):
    LABEL = 'm4399'
    DIRECTORY = 'm4399'
    CH_NAME = '4399小包'
    SDKTYPE = '30'
    PACKAGE_NAME = 'com.yunyue.nzgl.m4399'

    APPKEY = '103797'

    @classmethod
    def rules(cls):
        return super(RuleM4399, cls).rules() + [
            ('src/org/yunyue/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleHWD(RuleBase):
    LABEL = 'hwd'
    DIRECTORY = 'hwd'
    CH_NAME = '好玩点小包'
    SDKTYPE = '27'
    PACKAGE_NAME = 'com.yunyue.nzgl.hwd'

    APPID = '10148'
    QT_CHANNEL = 'A1239'


@register
class RulePaojiao(RuleBase):
    LABEL = 'paojiao'
    DIRECTORY = 'paojiao'
    CH_NAME = '泡椒小包'
    SDKTYPE = '31'
    PACKAGE_NAME = 'com.yunyue.nzgl.paojiao'

    APPID = '1223'
    APPKEY = 'LJICymT8uw5gvZBOTw2IPN6V4Ii8QXUe'
    JPUSH_APPKEY = '00b109b1c087e8fd15d06af9'


@register
class RulePipaw(RuleBase):
    LABEL = 'pipaw'
    DIRECTORY = 'pipaw'
    CH_NAME = '琵琶网小包'
    SDKTYPE = '32'
    PACKAGE_NAME = 'com.yunyue.nzgl.ppw'
    YY_PACKAGE_NAME = 'com.yunyue.nzgl.pipaw'

    APPID = '12381432793094'
    MERCHANT_ID = "1238"
    MERCHANT_APPID = "1287"
    PRIVATE_KEY = "d9dfe49650a6d27b2deb7461d486b38b"

    @classmethod
    def rules(cls):
        return super(RulePipaw, cls).rules() + [
            ('src/org/yunyue/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleYouku(RuleBase):
    LABEL = 'youku'
    DIRECTORY = 'youku'
    CH_NAME = '优酷网小包'
    SDKTYPE = '33'
    PACKAGE_NAME = 'com.yunyue.nzgl.youku'

    APPID = '1599'
    APPKEY = '004f0cc91d53602f'
    APPSECRET = '2bd018855bb3d5811a501582b242f278'

    @classmethod
    def common_replaces(cls):
        d = super(RuleYouku, cls).common_replaces()
        v = d['VERSION_CODE']
        d['YOUKU_VERSION_CODE'] = v[:2] + v[-2:]
        return d


@register
class RuleYYH(RuleBase):
    LABEL = 'yyh'
    DIRECTORY = 'yyh'
    CH_NAME = '应用汇小包'
    SDKTYPE = '34'
    PACKAGE_NAME = 'com.yunyue.nzgl.yyh'

    APPID = '10845'
    APPKEY = 'Q7Y6NcWnX1VsIj2o'
    PAYID = '5000302248'
    PAYKEY = 'M0ZGNjM5RjZDOEEyM0E0NjNERkMzOUZDMEQ5QzA0NTZDMTA5MThENk9UYzRPRGswTWpFMU5UYzVPRFUxTVRBM09Tc3lNalE1TURJNU5Ua3dNekExTnpNME16WXdNakV6T0RBME9EQXlNRGszTWpNM01qRXlNams9'


@register
class RuleZY(RuleBase):
    LABEL = 'zy'
    DIRECTORY = 'zy'
    CH_NAME = '卓易小包'
    SDKTYPE = '35'
    PACKAGE_NAME = 'com.yunyue.nzgl.zy'

    APPID = '901'
    APPKEY = '6d92f234296a73f28ca825434d0aea04'
    ZY_CHANNEL = '01'


@register
class RuleKugou(RuleBase):
    LABEL = 'kugou'
    DIRECTORY = 'kugou'
    CH_NAME = '酷狗小包'
    SDKTYPE = '36'
    PACKAGE_NAME = 'com.yunyue.nzgl.kugou'

    MERCHANTID = '244'
    APPID = '1444'
    APPKEY = 'DzHGRthxIBn6zQIP8FBhRCKoinkHoxAA'
    GAMEID = '10676'
    CODE = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGf5QaYh/jxpN+Sye2FQGbdMfgwYRF9n7ulkrM2afz+4Cqz/dMmwYyq1dSMZbv2gzI6JDxNQmFsc5tUdqpKTSzUrmRnWBtHLXzKhXTMxqutm1FedHoE51e5v2hYZ46ab4lBIg1yQVkHKmZC2pIWk4qBuvNUTbz1YKedr3mi99AbQIDAQAB'

    @classmethod
    def rules(cls):
        return super(RuleKugou, cls).rules() + [
            ('src/org/yunyue/sdk_app.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleEGame(RuleBase):
    LABEL = 'egame'
    DIRECTORY = 'egame'
    CH_NAME = '电信爱游戏小包'
    SDKTYPE = '37'
    PACKAGE_NAME = 'com.yunyue.nzgl.egame'

    APPID = '5043229'
    APPKEY = 'fd23b160d8b2dd0a82e6492817c3b5fb'
    CLIENTID = '7581912'
    CLIENTSECRET = '0f4c59c6b56646a2a5dd93e015eb0b6b'

    EGAME_CHANNEL = '10000000'
