#!/usr/bin/env python
# coding: utf-8
import os
from processor import process, all_rules
from optparse import OptionParser


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


parser = OptionParser()
parser.add_option("-v", "--version", dest="version", type="int",
                  help="package version", metavar="VERSION")
parser.add_option("-l", "--label", dest="label", type="string",
                  help="platform label", metavar="LABEL")
parser.add_option("-g", "--game", dest="game", type="string",
                  help="game label", metavar="GAME")


if __name__ == '__main__':
    options, args = parser.parse_args()
    m = __import__('rules_%s' % options.game)
    m.RuleBase.VERSION_CODE = str(options.version)
    m.RuleBase.VERSION_NAME = '%.05f' % (options.version / 100000.0)

    main(all_rules)
