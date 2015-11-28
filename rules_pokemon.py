# coding: utf-8
import os
from processor import register, Rule


class RuleBase(Rule):
    VERSION_CODE = '100000'
    VERSION_NAME = '1.00000'
    APPNAME = '神奇小精灵'
    APPLABEL = 'pokemon'


@register
class RuleEmpty(RuleBase):
    LABEL = 'empty'
    DIRECTORY = 'empty'
    CH_NAME = '神奇小精灵'
    SDKTYPE = '0'
    PACKAGE_NAME = 'com.winnergame.pokemon.empty'


@register
class RuleUC(RuleBase):
    LABEL = 'uc'
    DIRECTORY = 'uc'
    CH_NAME = 'UC小包'
    SDKTYPE = '7'
    PACKAGE_NAME = 'com.winnergame.pokemon.uc'

    GAMEID = '579041'

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
    PACKAGE_NAME = 'com.winnergame.pokemon.qh360'

    QHOPENSDK_APPKEY = '7861bd2c6c6af2840cc201d24f277b4c'
    QHOPENSDK_PRIVATEKEY = 'b9ce75b9c21477feb06ff8e61e581905'
    QHOPENSDK_APPID = '202820771'

    PAY_URL = 'http://pokemon.sdk.dnastdio.com:8888/sdk/c360/pay_callback'


@register
class RuleBaidu(RuleBase):
    LABEL = 'baidu'
    DIRECTORY = 'baidu'
    CH_NAME = '百度小包'
    SDKTYPE = '17'
    PACKAGE_NAME = 'com.winnergame.pokemon.baidu'

    APPID = '7187701'
    APPKEY = 'cmDV5PRTiFk4lrlydnsw3Dew'


@register
class RuleXiaomi(RuleBase):
    LABEL = 'xiaomi'
    DIRECTORY = 'xiaomi'
    CH_NAME = '小米小包'
    SDKTYPE = '18'
    PACKAGE_NAME = 'com.winnergame.pokemon.mi'
    YY_PACKAGE_NAME = 'com.winnergame.pokemon.mi'

    APPID = '2882303761517409326'
    APPKEY = '5391740911326'

    @classmethod
    def rules(cls):
        return super(RuleXiaomi, cls).rules() + [
            ('src/org/weilan/MiAppApplication.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleWDJ(RuleBase):
    LABEL = 'wdj'
    DIRECTORY = 'wdj'
    CH_NAME = '豌豆荚小包'
    SDKTYPE = '22'
    PACKAGE_NAME = 'com.winnergame.pokemon.wdj'

    APPKEY = '100034487'
    SECURITY_KEY = 'e41c62ee82d9777bff71cabe0b1eb7a7'

    @classmethod
    def rules(cls):
        return super(RuleWDJ, cls).rules() + [
            ('src/org/weilan/sdk_app.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleAnzhi(RuleBase):
    LABEL = 'anzhi'
    DIRECTORY = 'anzhi'
    CH_NAME = '安智小包'
    SDKTYPE = '21'
    PACKAGE_NAME = 'com.winnergame.pokemon.anzhi'

    APPKEY = '14482459076Q40C2DSvGMJ2Y69551d'
    APPSECRET = 'F4TWgtKHesIKN5v3eEsSQM42'

    @classmethod
    def rules(cls):
        return super(RuleAnzhi, cls).rules() + [
            ('src/org/weilan/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleOppo(RuleBase):
    LABEL = 'oppo'
    DIRECTORY = 'oppo'
    CH_NAME = 'OPPO小包'
    SDKTYPE = '20'
    PACKAGE_NAME = 'com.winnergame.pokemon.nearme.gamecenter'
    YY_PACKAGE_NAME = 'com.winnergame.pokemon.oppo'

    APPID = '4671'
    APPKEY = 'bjiQLpsqyu8Gg4kgK8goCKko4'
    APPSECRET = '8d68A258e9D93812b86B532567635352'

    PAY_URL = 'http://pokemon.sdk.dnastdio.com:8888/sdk/oppo/pay_callback'


@register
class RuleYYH(RuleBase):
    LABEL = 'yyh'
    DIRECTORY = 'yyh'
    CH_NAME = '应用汇小包'
    SDKTYPE = '34'
    PACKAGE_NAME = 'com.winnergame.pokemon.yyh'

    APPID = '10932'
    APPKEY = 'xBtsAo9c4F1CNnW0'
    PAYID = '5000372262'
    PAYKEY = 'NTI0QzA3RURDNzVGM0JGOThGNUU5N0ZDNzQ1RDdDQzM5MEI5OTVBOU1USXlOek15TkRreE16WTNOakUxTmpRd01qTXJNak15TXpNeE5EYzJPVFEyTXpBNE5qUXpNalkyT1RjM05Ua3dOREE0TmpJek1USTVOek01'


@register
class RuleGfan(RuleBase):
    LABEL = 'gfan'
    DIRECTORY = 'gfan'
    CH_NAME = '机锋小包'
    SDKTYPE = '28'
    PACKAGE_NAME = 'com.winnergame.pokemon.gfan'

    APPKEY = '1677378043'


@register
class RuleWanka(RuleBase):
    LABEL = 'wanka'
    DIRECTORY = 'wanka'
    CH_NAME = '硬核小包'
    SDKTYPE = '23'
    PACKAGE_NAME = 'com.winnergame.pokemon.wanka'
    YY_PACKAGE_NAME = 'com.winnergame.pokemon.wanka'
    CREATE_ORDER_URL = 'http://sdk.fengshen.winnergame.com/sdk/android/sdk/wanka/create_order'


@register
class RuleDownjoy(RuleBase):
    LABEL = 'downjoy'
    DIRECTORY = 'downjoy'
    CH_NAME = '当乐小包'
    SDKTYPE = '38'
    PACKAGE_NAME = 'com.winnergame.pokemon.downjoy'

    MERCHANT_ID = '1368'
    APPID = '3749'
    APPKEY = 'dNQsiZIW'

    @classmethod
    def rules(cls):
        return super(RuleDownjoy, cls).rules() + [
            ('src/org/weilan/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleMZW(RuleBase):
    LABEL = 'mzw'
    DIRECTORY = 'mzw'
    CH_NAME = '拇指玩小包'
    SDKTYPE = '41'
    PACKAGE_NAME = 'com.winnergame.pokemon.mzw'

    APPKEY = 'f3244edd2c89a457b9708eb4e503b37d'

    @classmethod
    def rules(cls):
        return super(RuleMZW, cls).rules() + [
            ('src/org/weilan/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RulePPS(RuleBase):
    LABEL = 'pps'
    DIRECTORY = 'pps'
    CH_NAME = 'PPS小包'
    SDKTYPE = '39'
    PACKAGE_NAME = 'com.winnergame.pokemon.pps'

    GAMEID = '3639'


@register
class RulePPTV(RuleBase):
    LABEL = 'pptv'
    DIRECTORY = 'pptv'
    CH_NAME = 'PPTV小包'
    SDKTYPE = '40'
    PACKAGE_NAME = 'com.winnergame.pokemon.pptv'

    APPID = 'pokemonxxr_m'
    PPTV_CID = '269'
    PPTV_CCID = ''
    UMENG_APPKEY = '5596584e67e58edf5f001607'
    UMENG_CHANNEL = '269'


@register
class RulePipaw(RuleBase):
    LABEL = 'pipaw'
    DIRECTORY = 'pipaw'
    CH_NAME = '琵琶网小包'
    SDKTYPE = '32'
    PACKAGE_NAME = 'com.winnergame.pokemon.ppw'
    YY_PACKAGE_NAME = 'com.winnergame.pokemon.pipaw'

    APPID = '12381435890541'
    MERCHANT_ID = "1238"
    MERCHANT_APPID = "1301"
    PRIVATE_KEY = "f57d74d191b5fe31143ff28254d47941"

    @classmethod
    def rules(cls):
        return super(RulePipaw, cls).rules() + [
            ('src/org/weilan/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleEGame(RuleBase):
    LABEL = 'egame'
    DIRECTORY = 'egame'
    CH_NAME = '电信爱游戏小包'
    SDKTYPE = '37'
    PACKAGE_NAME = 'com.winnergame.pokemon.egame'

    APPID = '5053651'
    APPKEY = 'ac1dd0b898e076b2ed49624c16253924'
    CLIENTID = '16578107'
    CLIENTSECRET = '562b4a48f9584bc69c4a3291dfda1d93'

    EGAME_CHANNEL = '10000000'

    @classmethod
    def rules(cls):
        return super(RuleEGame, cls).rules() + [
            ('extra_assets/feeInfo.dat', 'copy', 'feeInfo_pokemon.dat'),
        ]


@register
class RuleVivo(RuleBase):
    LABEL = 'vivo'
    DIRECTORY = 'vivo'
    CH_NAME = 'VIVO小包'
    SDKTYPE = '42'
    PACKAGE_NAME = 'com.winnergame.pokemon.vivo'

    APPID = "829b98597d987dd831b7f36d92d8fd04"
    CREATE_ORDER_URL = 'http://pokemon.sdk.dnastdio.com:8888/sdk/android/sdk/vivo/create_order'

    @classmethod
    def rules(cls):
        return super(RuleVivo, cls).rules() + [
            ('src/org/weilan/wxapi/WXPayEntryActivity.java', 'replace', cls.common_replaces()),
        ]



@register
class RulePaojiao(RuleBase):
    LABEL = 'paojiao'
    DIRECTORY = 'paojiao'
    CH_NAME = '泡椒小包'
    SDKTYPE = '31'
    PACKAGE_NAME = 'com.winnergame.pokemon.paojiao'

    APPID = '1274'
    APPKEY = 'LJICymT8uw5gvZBOTw2IPN6V4Ii8QXUe'
    JPUSH_APPKEY = '8172ea4cb6d808cb9fd7ea59'


@register
class RuleZC(RuleBase):
    LABEL = 'zc'
    DIRECTORY = 'zc'
    CH_NAME = '筑巢小包'
    SDKTYPE = '9'
    PACKAGE_NAME = 'com.winnergame.pokemon.zc'
    CHANNEL = ''

    TD_APPID = '33e15d27544f42d9b5f4f953a44e92e2'
    TD_CHANNEL = ''

    ZC_APPID = '619086295154565120'
    ZC_RSA = 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKSsxVpGBR+BEY0UHNAVoRxN3skQbatVvG3QbO4lm5GLLe1XOzHtHWraMtFiPa4GxhTn1GZFp0dPcvP1EoCbRIyZMkUNmix9rye88kDagpOZSUxUGirnQnT8vxzKhKsMj2JKp6SLIM7BzV2z+iQ7Z2xx/9779OVrj6e9xkhtVCqZAgMBAAECgYBmN3gpGN2FOLCUSa+42jQvRYbMd44blBRqdb2n9WAjb6kKceMkknJ4KQjyP3DZ3QqHX3/QG9xBv2czVyQtADQDswl4urPPQCdPh7P8H2foIxYAIyYnMwK0J8YuHzcs2gVrxQke0jneY+DIZsqcAnVpvzF5ONkxI5zXW40tC+hyoQJBAM/lJAyjzoUT3bw9fW+G90I54N7YUjZCybXIdLc2zD+OQjB5APaGFxmXjR7/qz0sa0WzuAKmG6hE6khSD+5C5AMCQQDKx3EGUhTyeUInwEShR2GrmYs3zDpNJ38cGx5zbZykHFM8iVW6NQSyovv7NVuPaf/irI8FRkR4vrbcetzDMuozAkEAuUInYsAiTAKNCK7+9YCfHDv5gHvinwnbOAu+vnmtf0FlCE78JbMOKLcdga8xyFyp5z4kzu95G/T1labTHW4sQwJAaxcnPrJMs72MTZgB5rbfAxQk7QPjamnIfFxqGYWy6wy2fMr+xkdHwtvGfeWxBC1z4Q9GvP9eG/KEei48tq4F9wJAQen9rksNxrP27XrS3RzW8HUEyZwWXQ2RQ5ToorVyM3bmBl+HwYNdXmKzz3WSOXiAq7cCI9TKPjFOWE83qfx3Vg=='
    PAY_URL = 'http://sdk.fengshen.winnergame.com/sdk/android/zcgame/pay_callback'


ZC_CHANNELS = [
    ('qixiazi', '七匣子'),
    ('youyi', '优艺市场'),
    #('anjingling', '安精灵'),
    ('jufeng', '聚丰网络'),
    ('kaopu', '靠谱助手'),
    ('shouyou', '手游之家'),
    ('anfeng', '安锋网'),
    ('hulizhushou', '狐狸助手'),
    ('fengyou', '疯游网'),
    #('zhidian', '指点传媒'),
    #('wx', 'WX'),
    ('shenma', '神马'),
    ('sougou', '搜狗'),
    ('dashi1', '大使1'),
    ('dashi2', '大使2'),
    ('dashi3', '大使3'),
    ('dashi4', '大使4'),
    ('dashi5', '大使5'),
    ('dashi6', '大使6'),
    ('dashi7', '大使7'),
    ('dashi8', '大使8'),
    ('dashi9', '大使9'),
    ('dashi10', '大使10'),
    ('leiting', '雷霆生动'),
    ('yyh', '应用汇'),
    ('youle', '游乐堂'),
    ('ziliang', '子凉游戏'),
    ('simi', '思蜜创想'),
    ('youxiqun', '游戏群'),
    ('tianxia', '天下江湖'),
    ('qianchi', '千尺'),
    ('sikai', '斯凯'),
    ('tianyu', '天语手机'),
]
for label, name in ZC_CHANNELS:
    register(type('RuleZC%s' % label, (RuleZC,), dict(
        LABEL='zc_%s' % label,
        CH_NAME='筑巢%s小包' % name,
        CHANNEL=label,
    )))


@register
class RuleHaima(RuleBase):
    LABEL = 'haima'
    DIRECTORY = 'haima'
    CH_NAME = '海马小包'
    SDKTYPE = '44'
    PACKAGE_NAME = 'com.winnergame.pokemon.ad.hm'

    APPID = '07958c0f992df0d72e71dfdeaf06d72f'
    APPKEY = '57093e2be9da90b6f7c158a239aa9ad8'
    HM_GAME_CHANNEL = ''

    @classmethod
    def rules(cls):
        return super(RuleHaima, cls).rules() + [
            ('src/org/weilan/SplashActivity.java', 'replace', cls.common_replaces()),
        ]


@register
class RuleXY(RuleBase):
    LABEL = 'xyandroid'
    DIRECTORY = 'xyandroid'
    CH_NAME = 'XY小包'
    SDKTYPE = '45'
    PACKAGE_NAME = 'com.winnergame.pokemon.xyandroid'

    APPID = '10000304'
    APPKEY = 'OJjkDM9ZAjYNDzdDgQCKaXDWilt0nJnM'


@register
class RuleWO17(RuleBase):
    LABEL = 'wo17'
    DIRECTORY = 'wo17'
    CH_NAME = '17WO小包'
    SDKTYPE = '46'
    PACKAGE_NAME = 'com.winnergame.pokemon.wo17'

    APPID = '400556'
    APPKEY = '88cf91a1aef212f3c2cd12406983427d'
    APPSECRET = 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIfdmhoEHp23e31Psvj9Bv+XlxnqpLqOfszO0XwcIt5ZUoSkvb7nbg4LT1gDeJka4U9DJ1l7m5VXfuXjp97qkKsCAwEAAQ=='

@register
class RuleMoLi(RuleBase):
    LABEL = 'moli'
    DIRECTORY = 'moli'
    CH_NAME = '墨狸小包'
    SDKTYPE = '47'
    PACKAGE_NAME = 'com.winnergame.pokemon.moli'

    APPID = '3002650777'
    CPPKEY = 'MIICXgIBAAKBgQDSQEvPkbjVJ5JPpcY6DJrq2A07QdFkXnkjBXEzgmO669uGRwNkK8XUqFfzpwn49V0gEUINbmiWjyECBoK9zg0dVm6OUyNCIE5MtGx7V9cEpTsFMoPidIn/4sbsQqv/Eef9lGCSToxIlbpQNO0xpm8eBCz+6ZyQIuLgsRNeLhN5EwIDAQABAoGBALdpdXjuw1HXQnCOyd0L7/zcWraN1S98pqohbj4kCgIfDJMX0eKJuPupm4gm+LEgwotd4sQ6w6xL0dyld1pCrPaLM3gjA04qxGKgMpsGPtMfCuwu0lb/x3JVIljqigBXFtRIppN+s0mJFPYv4TCOqpebFo92f5KLs/OCL0xIfathAkEA/1giJxfN6oIuDy10AEIo938N2fEmCXll1Czo7e5h/k3dccXj7+HL9/yoG3ekNmVkosVYikeHwI0OwK1cDdO6kQJBANLKhJd/FCk/gr51qm20+cdrLmkbes335//ZRUWDFt9HPEM1KhXl9tqVOJAR3s8MEuJTBvVJ8zPykZ1r29Loo2MCQQC4mer3AEqqQ7sw1deLaEldxMkqyyCIsO9hWaZ8fV7zDzANVNfZURC5FDwkv3ZErUD4PFwqfFQ0bMZBnhNzG6NBAkA/oSBrNtIYLXLDGXPL0BCCMQl+cuwcFpRyt9xgQlT6K1+2jerZV2Sv0NGVM7/FUki1BwkXrC385WEtWuytesovAkEA6QxUmkrLpItVHNrdg9FPBbydUlXsYO5AN0XdF6vzo4Y6Mz5+lTRO64M40gn6OpNodmI+4fU3M3G/ju0lgIWJtw=='
    PUBLICKEY = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0eAOhgqPXHn8d0iOuOt/RkjxzIE7Fjo7KkaiUEMLDLUaVe29vKMFPx6Di4fCbqOZZia02ZhgXikKrrZKcVBoJ/6hn7obwPcN2NBa12uacNqbtPILaNM7Zyd567EZXE7pNNwZDaiCRYDPLV5UXcT8aw4HixhiH2L09RUtw4JTxiwIDAQAB'

@register
class RuleHuawei(RuleBase):
    LABEL = 'huawei'
    DIRECTORY = 'huawei'
    CH_NAME = '华为小包'
    SDKTYPE = '48'
    PACKAGE_NAME = 'com.winnergame.pokemon.huawei'
    YY_PACKAGE_NAME = 'com.winnergame.pokemon.huawei'

    APPID = '10383675'
    PAYID = '900086000022190346'
    PUBLIC_KEY = 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKQM73iEYW/f6JtRgOuwsyvvk1hfKNUnC3F0arwr8+YqrIOrPZMA+5fzV6hMH4Pqeinp7QtRrWwKycH96dWPt08CAwEAAQ=='
    GET_BUOY_PRIVATE_KEY = 'http://pokemon.sdk.dnastdio.com:8888/sdk/android/sdk/huawei/buoy'
    GET_PAY_PRIVATE_KEY = 'http://pokemon.sdk.dnastdio.com:8888/sdk/android/sdk/huawei/pay'

    @classmethod
    def rules(cls):
        return super(RuleHuawei, cls).rules() + [
            ('src/org/weilan/GlobalParam.java', 'replace', cls.common_replaces()),
        ]

@register
class RuleWeilan(RuleBase):
    LABEL = 'weilan'
    DIRECTORY = 'weilan'
    CH_NAME = '微蓝小包'
    SDKTYPE = '49'
    PACKAGE_NAME = 'com.winnergame.pokemon.weilan'

    APP_KEY    = '5i29qfd23a4bghaswx9w'
    APP_SECRET = 'psyvmyra6f7of4sacvpu'
    SERVER_ID  = 'M1028A'
    PROJECT_ID = 'P10116A'
    PRODUCT_ID = 'D10053A'

    @classmethod
    def rules(cls):
        return super(RuleWeilan, cls).rules() + [
            ('src/org/weilan/WLSdkConfig.java', 'replace', cls.common_replaces()),
        ]

@register
class RuleLenovo(RuleBase):
    LABEL = 'lenovo'
    DIRECTORY = 'lenovo'
    CH_NAME = '联想小包'
    SDKTYPE = '50'
    PACKAGE_NAME = 'com.winnergame.pokemon.lenovo'

    OPEN_APPID = '1511180153656.app.ln'
    PAY_APPKEY = 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ3oZzcR7MWIkpyUOv133CgRLbs7IrcHj4VHOHgZl7+naUDpqAXf8BfxzoQJDRJy7zsRADvu044w5UU9Pntbb+eRWQipdArfZ30RRsCA2lwXwbzMuzaGBmlrqwyeZOHS9uU3vQ6oPoIp+ZYpBaEJN8Io3MoxGMy0/0i7V1BMjbTRAgMBAAECgYAky22ZmHSqhqtbDvM78rz7HR2h3iK1sW5Q6QqQea4xe2n8NTXwwICqo66yd4VzQhLamZiLXpgvLteNrbqsdd76IOyAgnHbXHhNLR9HZWAG5iRxjChOAI3B1Hw+6CgFbKA2Z5MAGu1VYzpYdXI364GME1XzoVVuxUsWTK+Qrjw87QJBAOf00NJhvMJmT1RSnaGHmdZjfNBU73U+uv2Z5VxCNUs+rkhgtBt9Rodll3Ut0z41Gri/wORKgweUsBcRX7bnLNsCQQCuRqO3UJGzECNNzVG/7zGqK3GuNzPBiqNPvXHzexwqjW7P9JMLiDbH6mJRBpoukBBQcja5oqZeemC4sZuHRL7DAkAgpYxej/MJSW0Q6S/WdEdqrUX77HhngBTBbM+jVI47sO5GsRWoaNFsbRgMHg8FSmQgbJyxhOs8Pekq1f8qtw7RAkBXV2XDgKiUsljPLEB1Td55J27A3j+dFutEEnDikueJ1/oaVLvBQtIZnTkK3KE3BtJ2Ttdob8Crhu+kgLaus2BbAkEApwxWPb3UEFaQBIwdHALNjF/sQzflbJavCyOLBL3D53hvQ1rOPV9f0FFvozaPy8fnKOC4PPQXGkGNsrC+R+F6Bw=='

    @classmethod
    def rules(cls):
        return super(RuleLenovo, cls).rules() + [
            ('src/org/weilan/SdkConfig.java', 'replace', cls.common_replaces()),
        ]

@register
class RuleAmigo(RuleBase):
    LABEL = 'amigo'
    DIRECTORY = 'amigo'
    CH_NAME = '金立小包'
    SDKTYPE = '51'
    PACKAGE_NAME = 'com.winnergame.pokemon.am'

    APP_KEY    = '11B5BA6537274E798CEED85864AC63CF'
    ORDER_URL  = 'http://pokemon.sdk.dnastdio.com:8888/sdk/android/sdk/gionee/create_order'

    @classmethod
    def rules(cls):
        return super(RuleAmigo, cls).rules() + [
            ('src/org/weilan/SdkConfig.java', 'replace', cls.common_replaces()),
        ]

@register
class RuleYouku(RuleBase):
    LABEL = 'youku'
    DIRECTORY = 'youku'
    CH_NAME = '优酷小包'
    SDKTYPE = '33'
    PACKAGE_NAME = 'com.winnergame.pokemon.youku'

    APPID      = '2285'
    APPKEY     = '7f293b5b56e3f737'
    APPSECRET  = '42de96b7b26686decac50e75409c6d31'
    YOUKU_VERSION_CODE = ''

    PAY_URL = 'http://pokemon.sdk.dnastdio.com:8888/sdk/android/sdk/youku/pay_callback'

    @classmethod
    def rules(cls):
        codeNum = int(RuleBase.VERSION_CODE)
        cls.YOUKU_VERSION_CODE = str(codeNum/10000) + str(codeNum%100)
        print 'YOUKU_VERSION_CODE = ' + cls.YOUKU_VERSION_CODE
        return super(RuleYouku, cls).rules() + [
            ('src/org/weilan/GameProxyImpl.java', 'replace', cls.common_replaces()),
        ]

@register
class RuleGuopan(RuleBase):
    LABEL = 'guopan'
    DIRECTORY = 'guopan'
    CH_NAME = 'XX果盘小包'
    SDKTYPE = '55'
    PACKAGE_NAME = 'com.winnergame.pokemon.ad.guopan'

    APPID      = '102121'
    APPKEY     = 'CJOVUCOA7728E002'

@register
class RuleM4399(RuleBase):
    LABEL = 'm4399'
    DIRECTORY = 'm4399'
    CH_NAME = 'm4399小包'
    SDKTYPE = '30'
    PACKAGE_NAME = 'com.winnergame.pokemon.m4399'

    APPKEY     = 'fa633fea35570a4ecd279da3dcd6d97a'

@register
class RuleMz(RuleBase):
    LABEL = 'mz'
    DIRECTORY = 'mz'
    CH_NAME = '魅族小包'
    SDKTYPE = '56'
    PACKAGE_NAME = 'com.winnergame.pokemon.mz'

    APPID     = '2820741'
    APPKEY    = 'e158c5bd9d2c49a29c890e5a588d9952'
    ORDER_URL = ''

@register
class RuleKaopu(RuleBase):
    LABEL = 'kaopu'
    DIRECTORY = 'kaopu'
    CH_NAME = '靠谱小包'
    SDKTYPE = '57'
    PACKAGE_NAME = 'com.winnergame.pokemon.kaopu'

    APPID      = '10216001'
    APPKEY     = '10216'
    APPSECRET  = 'B7FA47A6-3765-4DE8-8DC5-7C8355D7C7C5'
    SDKVERSION = '1.0'

