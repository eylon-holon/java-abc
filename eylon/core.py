import builtins
import datetime
import inspect

from eylon.utils import *
from eylon.post import post
from eylon.classwork import ClassWork
from config import cfg


work = None


###################################

def print(*args, **kwargs):
    if work != None:
        work.print(*args, **kwargs)
    builtins.print(*args, **kwargs)

def input(prompt):
    answer = work.input() if work != None else None
    if (answer == None):
        answer = builtins.input(prompt)
    return answer


###################################

def test_submission(func, input, expected):
    with work.capture_io(input):
        func()
    output = work.get_prints()
    failed = False
    for exp in expected:
        found = False
        for line in output:
            if exp in line:
                found = True
                break
        if not found:
            print(f"'{exp}' is not found in output lines")
            print("---------------------------------------------------")
            for line in output:
                print(line.strip())
            print("---------------------------------------------------")
            failed = True
    if failed:
        return None
    return output

def post_results(fname, src, input, output, expected):
    request = {
        'now': datetime.datetime.now().isoformat(),
        'docId': cfg.docId,
        'branch': work.branch,
        'lesson': work.lesson,
        'fname': fname,
        'src': src,
        'input': "\n".join(input),
        'output': "\n".join(output),
        'expected': "\n".join(expected),
        'tags': ' '.join(cfg.tags)
    }
    response = post(cfg.api, request)
    return response["msg"]


###################################

def start(desc):
    global work
    notebook = get_notebook_name_from_stack()
    work = ClassWork(notebook, desc)
    post_results("Here😁", "", [], [], [])
    print("Let's start 🙌, those are waiting 🛠️: ", ','.join(desc.keys()))

    
def submit(func):
    fn = fname(func)
    if work == None:
        print("Please, start the class first")
        return
    if fn not in work.desc:
        print(f"function '{fn}' is not part of this lesson")
        return
    src = inspect.getsource(func)
    input = work.desc[fn][0]
    expected = work.desc[fn][1]
    output = test_submission(func, input, expected)
    if output == None:
        print(f"Your work has errors. Please, fix and submit again.")
        return
    msg = post_results(func.__name__, src, input, output, expected)
    print(msg)


def missing(lesson, people):
    now = datetime.datetime.now().isoformat()
    for stu in people:
        request = {
            'now': now,
            'docId': cfg.docId,
            'branch': stu,
            'lesson': lesson,
            'fname': "",
            'src': "",
            'input': "",
            'output': "",
            'expected': "",
            'tags': ' '.join(cfg.tags)
        }
        post(cfg.api, request)


###################################

def store_all_changes_to_github():
    os_cmd("git add -A")
    os_cmd('git commit -m "auto commit..."')
    os_cmd("git push")


def get_next_lesson():
    os_cmd("git pull origin main")
    os_cmd("git push")

