#!/usr/bin/env python
# coding: utf-8
import os, sys
from processor import process, all_rules
from optparse import OptionParser


def one(rule):
    os.chdir('sdks/%s' % rule.DIRECTORY)
    if os.popen('git status -s .').read():
        print 'directory is not clean'
        os.system('git status')
        return
    os.system('git clean -f -d .')
    process(rule.rules())
    # preview replaces
    #os.system('git diff -p --raw .')
    os.system('ant clean linkassets release')
    os.system('git clean -f -d .')
    os.system('git checkout -- .')
    # TODO copy package
    os.system('mkdir -p $HOME/android_package/%s'%rule.VERSION_CODE)
    output = '$HOME/android_package/%s/%s_%s.apk'%(rule.VERSION_CODE, rule.CH_NAME, rule.VERSION_CODE)
    os.system('cp bin/poem-release.apk %s'%output)
    os.chdir('../..')


def run_copyassets(d, version):
    cmd = 'copyassets.py %s assets --version %s --ext ".lh"' % (d, version)
    os.system(cmd)


def main(desc, labels=None):
    if labels:
        print labels
        print labels.split( '|' )
        for lable in labels.split( '|' ):
            one(desc[lable])
    else:
        for label, rule in desc.items():
            one(rule)


parser = OptionParser()
parser.add_option("-v", "--version", dest="version", type="int",
                  help="package version", metavar="VERSION")
parser.add_option("-l", "--labels", dest="labels", type="string",
                  help="platform labels", metavar="LABEL")
parser.add_option("-g", "--game", dest="game", type="string",
                  help="game label", metavar="GAME")
parser.add_option("-c", "--copyassets", dest="copyassets_path", type="string",
                  help="copyassets path", metavar="COPYASSETS")
parser.add_option("--list", dest="list", action="store_true",
                  help="list available packages.", default=False)


if __name__ == '__main__':
    options, args = parser.parse_args()
    try:
        m = __import__('rules_%s' % options.game)
    except ImportError:
        print >> sys.stderr, 'invalid game', options.game
        parser.print_help(sys.stderr)
        sys.exit(1)

    if options.list:
        for rule in all_rules.values():
            print rule.LABEL, rule.CH_NAME
    else:
        m.RuleBase.VERSION_CODE = str(options.version)
        m.RuleBase.VERSION_NAME = '%.05f' % (options.version / 100000.0)
        if options.copyassets_path:
            run_copyassets(options.copyassets_path, options.version)

        main(all_rules, options.labels)
