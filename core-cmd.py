import eylon.core

if __name__ == '__main__':
    import sys
    func = getattr(eylon.core, sys.argv[1])
    func(*sys.argv[2::])