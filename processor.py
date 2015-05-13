'''
rules = [file_pattern, command, args]
'''
import glob


def command_replace(path, trans):
    s = open(path, 'rb').read()
    for key, value in trans.items():
        s.replace('${%s}' % key, value)
    open(path, 'wb').write(s)


def process(rules):
    for pattern, command, args in rules:
        for path in glob.glob(pattern):
            globals()['command_'+command](path, *args)
