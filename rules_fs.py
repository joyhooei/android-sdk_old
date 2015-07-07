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
    QHOPENSDK_PRIVATEKEY = 'd184708aafa976a80b3411678c9a071b'
    QHOPENSDK_APPID = '202593516'

    PAY_URL = 'http://sdk.fengshen.yunyuegame.com/sdk/android/sdk/c360/pay_callback'


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


@register
class RuleWDJ(RuleBase):
    LABEL = 'wdj'
    DIRECTORY = 'wdj'
    CH_NAME = '豌豆荚小包'
    SDKTYPE = '22'
    PACKAGE_NAME = 'com.yunyue.fs.wdj'

    APPKEY = '100028689'
    SECURITY_KEY = 'c5e52cb17c35df8d4b9fb83e9d909c0e'

    @classmethod
    def rules(cls):
        return super(RuleWDJ, cls).rules() + [
            ('src/org/yunyue/sdk_app.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleAnzhi(RuleBase):
    LABEL = 'anzhi'
    DIRECTORY = 'anzhi'
    CH_NAME = '安智小包'
    SDKTYPE = '21'
    PACKAGE_NAME = 'com.yunyue.fs.anzhi'

    APPKEY = '14356441495136mQAPB5s3XSafyoK9'
    APPSECRET = '3G91iXHaLTF852fxdJ3X8AlL'

    @classmethod
    def rules(cls):
        return super(RuleAnzhi, cls).rules() + [
            ('src/org/yunyue/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleOppo(RuleBase):
    LABEL = 'oppo'
    DIRECTORY = 'oppo'
    CH_NAME = 'OPPO小包'
    SDKTYPE = '20'
    PACKAGE_NAME = 'com.yunyue.fs.nearme.gamecenter'
    YY_PACKAGE_NAME = 'com.yunyue.fs.oppo'

    APPID = '\\ 3867'
    APPKEY = 'bgWqwsx1v00sGK4w84OwoosGc'
    APPSECRET = 'D566f23f01AA489fF84409d6a5280769'

    PAY_URL = 'http://sdk.fengshen.yunyuegame.com/sdk/android/sdk/oppo/pay_callback'


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
class RuleGfan(RuleBase):
    LABEL = 'gfan'
    DIRECTORY = 'gfan'
    CH_NAME = '机锋小包'
    SDKTYPE = '28'
    PACKAGE_NAME = 'com.yunyue.fs.gfan'

    APPKEY = '164591850'


@register
class RuleWanka(RuleBase):
    LABEL = 'wanka'
    DIRECTORY = 'wanka'
    CH_NAME = '硬核小包'
    SDKTYPE = '23'
    PACKAGE_NAME = 'com.yunyue.fs.wanka'
    YY_PACKAGE_NAME = 'com.yunyue.fs.wanka'
    CREATE_ORDER_URL = 'http://sdk.fengshen.yunyuegame.com/sdk/android/sdk/wanka/create_order'


@register
class RuleDownjoy(RuleBase):
    LABEL = 'downjoy'
    DIRECTORY = 'downjoy'
    CH_NAME = '当乐小包'
    SDKTYPE = '38'
    PACKAGE_NAME = 'com.yunyue.fs.downjoy'

    MERCHANT_ID = '101'
    APPID = '195'
    APPKEY = 'j5VEvxhc'


@register
class RuleMZW(RuleBase):
    LABEL = 'mzw'
    DIRECTORY = 'mzw'
    CH_NAME = '拇指玩小包'
    SDKTYPE = '41'
    PACKAGE_NAME = 'com.yunyue.fs.mzw'
