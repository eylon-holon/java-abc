from .core import *

if __name__ == '__main__':
    import sys
    func = getattr(core, sys.argv[1])
    func(*sys.argv[2::])