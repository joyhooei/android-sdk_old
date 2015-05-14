#!/usr/bin/env python
# coding: utf-8
import os
from processor import process, all_rules
from optparse import OptionParser


def one(rule):
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


def run_copyassets(d, version):
    cmd = 'copyassets.py %s assets --version %s --ext ".lh"' % (d, version)
    os.system(cmd)


def main(desc, label=None):
    if label:
        one(desc[label])
    else:
        for label, rule in desc.items():
            one(rule)


parser = OptionParser()
parser.add_option("-v", "--version", dest="version", type="int",
                  help="package version", metavar="VERSION")
parser.add_option("-l", "--label", dest="label", type="string",
                  help="platform label", metavar="LABEL")
parser.add_option("-g", "--game", dest="game", type="string",
                  help="game label", metavar="GAME")
parser.add_option("-c", "--copyassets", dest="copyassets_path", type="string",
                  help="copyassets path", metavar="COPYASSETS")


if __name__ == '__main__':
    options, args = parser.parse_args()
    m = __import__('rules_%s' % options.game)
    m.RuleBase.VERSION_CODE = str(options.version)
    m.RuleBase.VERSION_NAME = '%.05f' % (options.version / 100000.0)

    if options.copyassets_path:
        run_copyassets(options.copyassets_path, options.version)

    main(all_rules, options.label)
