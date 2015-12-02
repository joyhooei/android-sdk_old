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

    # icon path
    os.environ['ICON_DIRECTORY'] = rule.ICON_PATH
    if not os.path.isabs( rule.ICON_PATH ):
        os.environ['ICON_DIRECTORY'] = os.path.abspath( rule.ICON_PATH )

    process(rule.rules())
    # preview replaces
    #os.system('git diff -p --raw .')
    os.system('ant clean linkassets release')

    # TODO copy package
    d = '$HOME/android_package/%s/%s' % (rule.APPLABEL, rule.VERSION_CODE)
    os.system('mkdir -p %s'%d)
    output = '%s/%s_%s.apk'%(d, rule.CH_NAME, rule.VERSION_CODE)
    os.system('cp bin/poem-release.apk %s'%output)

    os.system('git clean -f -d .')
    os.system('git checkout -- .')

    os.chdir('../..')


def run_copyassets(version, mc_postfix):
    cmd = 'copyassets.py $CLIENT_DIRECTORY assets --version %s --ext ".lh"' % version
    if mc_postfix:
        cmd += ' --mc-postfix %s' % mc_postfix
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
parser.add_option("-i", "--iconspath", dest="copyicons_path", type="string",
                  help="copyicons path", metavar="COPYICONS")
parser.add_option("--list", dest="list", action="store_true",
                  help="list available packages.", default=False)
parser.add_option("--mc-postfix", dest="mc_postfix", action="store", type="string",
                  help="alternative mc directory", metavar="MC-POSTFIX")


if __name__ == '__main__':
    options, args = parser.parse_args()
    try:
        m = __import__('rules_%s' % options.game)
    except ImportError:
        print >> sys.stderr, 'invalid game', options.game
        parser.print_help(sys.stderr)
        sys.exit(1)

    if options.copyassets_path:
        os.environ['CLIENT_DIRECTORY'] = options.copyassets_path
        if not os.path.isabs(os.environ['CLIENT_DIRECTORY']):
            os.environ['CLIENT_DIRECTORY'] = os.path.abspath(os.environ['CLIENT_DIRECTORY'])

    #if options.copyicons_path:
    #    os.environ['ICON_DIRECTORY'] = options.copyicons_path
    #else :
    #    c_dir = os.environ.get("CLIENT_DIRECTORY", "")
    #    if c_dir != "" :
    #        os.environ['ICON_DIRECTORY'] = os.path.join(c_dir, '../pokemon_icons/android')

    #i_dir = os.environ.get("ICON_DIRECTORY", "")
    #if i_dir != "" :
    #    if not os.path.isabs(i_dir):
    #        os.environ['ICON_DIRECTORY'] = os.path.abspath(i_dir)

    if options.list:
        for rule in all_rules.values():
            print rule.LABEL, rule.CH_NAME
    else:
        m.RuleBase.VERSION_CODE = str(options.version)
        m.RuleBase.VERSION_NAME = '%.05f' % (options.version / 100000.0)
        if options.copyassets_path:
            run_copyassets(options.version, options.mc_postfix)

        main(all_rules, options.labels)
