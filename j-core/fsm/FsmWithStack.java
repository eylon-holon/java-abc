import java.util.*;

class FsmWithStack extends FSM {
    public static class Stack {
        private List<String> lifo;

        public Stack() {
            lifo = new ArrayList<String>();
            lifo.addLast("┴");
        }

        public boolean isEmpty() {
            return lifo.size() <= 1;
        }

        public void push(String letters) {
            if (letters.length() == 0)
                return;

            for (var letter: letters.split("")) {
                lifo.addLast(letter);
            }
        }

        public void pop() {
            if (isEmpty()) {
                FSM.print("ERROR: Stack is empty");
                return;
            }
            lifo.removeLast();
        }

        public String peek() {
            return lifo.getLast();
        }

        public String toString() {
            var sb = new StringBuffer();
            for (var letter: lifo) {
                sb.append(letter);
            }
            return sb.toString();
        }
    }

    private final String _name;
    private final Def _def;
    private final boolean _notfull;

    public FsmWithStack(String name, Def def, boolean traceInit) {
        _name = name;
        _def = def;
        _notfull = def.props.notfull();
    }

    public boolean isComplete() {
        return !_notfull;
    }

    public String[] getNotCompletedStates() {
        return new String[] {"n/a"};
    }

    public boolean determenistic() {
        return !_def.props.nondetermenistic();
    }

    protected boolean notAcceptedWithError(State in, String fmt, Object... args) {
        print("  ERROR in %s: %s", in.name, String.format(fmt, args));
        return false;
    }

    protected boolean inAlefBet(String letter) {
        for (var lt: _def.alefBet) {
            if (lt.equals(letter))
                return true;
        }
        return false;
    }

    protected String[] parseTerms(String rule) {
        var split = rule.trim().split("\\|");

        if (split.length != 2)
            return null;

        var terms = split[0].trim().split("\\s");

        if (terms.length != 2)
            return null;

        return new String[] {
            terms[0].trim(),
            terms[1].trim()
        };
    }

    protected boolean termsAreMatched(State state, Stack stack, String rule, String letter) {
        var terms = parseTerms(rule);

        if (terms == null)
            return notAcceptedWithError(state, "Failed to parse terms of rule '%s'", rule);

        if (!letter.equals(terms[0]))
            return false;

        if (!stack.peek().equals(terms[1]))
            return false;

        return true;
    }

    protected Transition[] matchRules(State state, Stack stack, String letter) {
        var transitions = new ArrayList<Transition>();

        for (var tr: _def.transitions) {
            if (tr.from != state)
                continue;

            for (var rule: tr.rules) {
                if (!termsAreMatched(state, stack, rule, letter))
                    continue;

                var matched = new Transition(state, tr.to, new String[] {rule});
                transitions.add(matched);
            }
        }

        return transitions.toArray(Transition[]::new);
    }

    protected String[] parseOperation(State state, String rule) {
        var split = rule.trim().split("\\|");

        if (split.length != 2)
            return null;

        var operands = split[1].trim().split("\\s");

        if (operands.length == 1) {
            return new String[] {
                operands[0].trim()
            };
        }

        if (operands.length == 2) {
            return new String[] {
                operands[0].trim(),
                operands[1].trim()
            };
        }

        return null;
    }

    protected boolean itsNop(String[] ops) {
        var op = ops[0].trim().toLowerCase();

        if (!op.equals("nop"))
            return false;

        if (ops.length == 1)
            return true;

        print("ERROR: Operation NOP should not have arguments (%s)", ops[1]);
        return false;
    }

    protected boolean itsPush(Stack stack, String[] ops) {
        var op = ops[0].trim().toLowerCase();

        if (!op.equals("push"))
            return false;

        if (ops.length != 2 || ops[1].length() == 0) {
            print("ERROR: Operation PUSH should have one argument");
            return false;
        }

        stack.push(ops[1]);

        return true;
    }

    protected boolean itsPop(Stack stack, String[] ops) {
        var op = ops[0].trim().toLowerCase();

        if (!op.equals("pop"))
            return false;

        if (ops.length == 1)
            return true;

        var letter = ops[1];

        if (letter.length() > 1) {
            print("ERROR: Operation POP can pop only one letter each time (%s)", letter);
            return false;
        }

        if (!stack.peek().equals(letter)) {
            print("WARN: Operation POP: letter '%s' is different from the one in stack ([%s])", letter, stack);
        }

        stack.pop();

        return true;
    }

    protected State toNextState(State state, Stack stack, String letter, Transition tr, boolean trace) {
        var rule = tr.rules[0];
        var ops = parseOperation(state, rule);

        if (ops == null)
            return null;

        var fromPeek = stack.peek();

        do {
            if (itsNop(ops))
                break;

            if (itsPush(stack, ops))
                break;

            if (itsPop(stack, ops))
                break;

            print("Unknown operation '%s' in rule '%s'", ops[0], rule);
            return null;
        } while (false);

        if (trace) print("  '%s %s': %s --> %s (%s) [%s]", letter, fromPeek, state.name, tr.to.name, tr.rules[0], stack);

        return tr.to;
    }

    public boolean accept(String word, boolean trace) {
        trace |= _def.props.trace();

        word = word.trim();

        if (trace)
            print("FSM┴['%s']: accepting word '%s'", _name, word);

        var current = _def.states[0];
        var stack = new Stack();

        if (word.length() == 0)
            return current.ok;

        for (var letter: word.split("")) {
            if (!inAlefBet(letter))
                return _notfull && !trace ? false : notAcceptedWithError(current, "Letter '%s' is not in alefBet", letter);

            var transitions = matchRules(current, stack, letter);
            
            if (transitions.length == 0)
                return _notfull && !trace ? false : notAcceptedWithError(current, "Can't find transition for condition '%s %s'", letter, stack.peek());

            if (transitions.length > 1)
                return notAcceptedWithError(current, "Found %d transitions for letter '%s'; nondeterministic FSM┴ is not yet supported", transitions.length, letter);

            var rule = transitions[0].rules[0];
            var next = toNextState(current, stack, letter, transitions[0], trace);

            if (next == null)
                return notAcceptedWithError(current, "Failed to process rule '%s' for letter '%s'", rule, letter);

            current = next;
        }

        if (trace)
            print("FSM┴['%s']: '%s' is %s", _name, word, current.ok ? "accepted" : "rejected");

        return current.ok;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();

        sb
            .append(String.format(
            "FSM ┴['%s', %b, %d, %d, %d]", _name, determenistic(), _def.states.length, _def.alefBet.length, _def.transitions.length))
            .append("\n");

        var states = new String[_def.states.length];
        for (int i = 0; i < _def.states.length; i++) {
            var st = _def.states[i];
            states[i] = st.ok ? 
                String.format("%s((%d))", st.name, st.id) :
                String.format("%s(%d)", st.name, st.id); 
        }

        sb
            .append("  states: [")
            .append(String.join(", ", states))
            .append("]\n");

        sb
            .append("  alefBet: ")
            .append(Arrays.toString(_def.alefBet))
            .append("\n");

        sb.append("  transitions: [\n");
        for (var transition: _def.transitions) {
            sb.append(String.format("    %s -%s-> %s\n",
                transition.from.name, Arrays.toString(transition.rules), transition.to.name));
        }
        sb.append("  ]\n");

        return sb.toString();
    }
}