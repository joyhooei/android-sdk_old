# coding: utf-8
import os
from processor import process

common_rules = [
    ('AndroidManifest.xml', 'replace', {
        'VERSION_CODE': '120005',
        'VERSION_NAME': '1.20005',
    }),
]

g_rules = {
    'zc': common_rules + [
        ('AndroidManifest.xml', 'replace', {
            'TD_APPID': 'f7eac3e7b3014a5487f69f85c07069e6',
            'TD_CHANNEL': '',
            'PACKAGE_NAME': 'com.yunyue.ttxm.zclh',
        }),
    ],
}


def main(desc):
    for label, rules in desc.items():
        os.chdir('sdks/%s' % label)
        os.system('git clean -f -d .')
        process(rules)
        # preview replaces
        os.system('git diff .')
        os.system('ant linkassets release')
        os.system('git clean -f -d .')
        # TODO copy package
        os.chdir('../..')

if __name__ == '__main__':
    main(g_rules)
