import java.util.*;

class FSM {
    public static class State {
        public int id;
        public String name;
        public boolean ok;

        public State(int id, String name, boolean ok) {
            this.id = id;
            this.name = name;
            this.ok = ok;
        }
    }

    public static class Rule {
        public State from;
        public State to;
        public char[] chars;

        public Rule(State from, State to, char[] chars) {
            this.from = from;
            this.to = to;
            this.chars = chars;
        }
    }

    public static class Def {
        public State[] states;
        public char[] alefBet;
        public Rule[] rules;

        public Def(State[] states, char[] alefBet, Rule[] rules) {
            this.states = states;
            this.alefBet = alefBet;
            this.rules = rules;
        }
    }

    private final String _name;
    private final Def _def;
    private final int[] _ch2at;
    private final int[][] _fsm;

    private final boolean _hasNoStates;
    private final String[] _notCompletedStates;

    private int current;

    private void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    public FSM(String name, Def def, boolean traceInit) {
        _name = name;
        _def = def;

        // initalize alef-bet

        _ch2at = new int[Character.MAX_VALUE];

        for (int at = 0; at < def.alefBet.length; at++) {
            var ch = def.alefBet[at];
            _ch2at[ch] = at;

            if (traceInit)
                print("ch2at['%s'] = %d  ==>  ch2at[%d] = %d", ch, at, (int) ch, at);
        }

        // initialize transition table

        _fsm = new int[def.states.length][def.alefBet.length];

        for (var ln: _fsm) {
            Arrays.fill(ln, -1);
        }

        for (var rule: def.rules) {
            int from = rule.from.id;
            int to = rule.to.id;

            for (var ch: rule.chars) {
                var at = _ch2at[ch];
                _fsm[from][at] = to;

                if (traceInit)
                    print("fsm[%s]['%s'] = %s  ==>  fsm[%d][%d] = %d", rule.from.name, ch, rule.to.name, from, at, to);
            }
        }

        // initialize not completed states

        _hasNoStates = _def.states.length == 0;

        var notCompletedNames = new ArrayList<String>();

        for (int lnNo = 0; lnNo < _fsm.length; lnNo++) {
            var ln = _fsm[lnNo];

            var completed = true;
            for (var target: ln) {
                completed &= target >= 0;
            }

            if (!completed) {
                var notCompleted = def.states[lnNo].name;

                if (traceInit)
                    print("state '%s' is not complete", notCompleted);

                notCompletedNames.add(notCompleted);
            }
        }

        _notCompletedStates = notCompletedNames.toArray(String[]::new);
    }

    private int error(State in, String errMsg) {
        print("ERROR in %s: %s", in.name, errMsg);
        return -1;
    }

    private void traceTransition(State from, State to, int at) {
        print("  '%s': %s --> %s", _def.alefBet[at], from.name, to.name);
    }

    private int getAt(char ch) {
        var chars = _def.alefBet;
        for (int i = 0; i < chars.length; i++) {
            if (ch == chars[i])
                return i;
        }
        return -1;
    }

    private int next(char ch, boolean trace) {
        var from = _def.states[current];
        assert from != null;

        var at = getAt(ch);
        if (at == -1)
            return error(from, String.format("Undefined transition '%s'", ch));

        var next = _fsm[current][at];
        if (next == -1)
            return error(from, String.format("No transition for '%s' is defined", ch));

        var to = _def.states[next];
        assert to != null;

        if (trace)
            traceTransition(from, to, at);

        return next;
    }

    public boolean isComplete() {
        return _notCompletedStates.length == 0;
    }

    public String[] getNotCompletedStates() {
        return _notCompletedStates;
    }

    public boolean accept(String word, boolean trace) {
        if (trace)
            print("FSM['%s']: accepting word '%s'", _name, word);

        if (_hasNoStates) {
            print("FSM['%s']: I have no states", _name);
            return false;
        }

        current = 0;

        for (var ch: word.toCharArray()) {
            current = next(ch, trace);

            if (current == -1)
                return false;
        }

        var ok = _def.states[current].ok;

        if (trace)
            print("FSM['%s']: '%s' is %s", _name, word, ok ? "accepted" : "wrong");

        return ok;
    }

    public boolean accept(String word) {
        return accept(word, false);
    }

    public boolean[] accept(String... many) {
        var result = new boolean[many.length];

        for (int i = 0; i < many.length; i++) {
            result[i] = accept(many[i], false);
        }

        return result;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();

        sb
            .append(String.format(
            "FSM['%s', %d, %d, %d]", _name, _def.states.length, _def.alefBet.length, _def.rules.length))
            .append("\n");

        var states = new String[_def.states.length];
        for (int i = 0; i < _def.states.length; i++) {
            var st = _def.states[i];
            states[i] = String.format("%s(%d)", st.name, st.id); 
        }

        sb
            .append("  states:  [")
            .append(String.join(", ", states))
            .append("]\n");

        sb
            .append("  alefBet: ")
            .append(Arrays.toString(_def.alefBet))
            .append("\n");

        sb.append("  rules:  [\n");
        for (var rule: _def.rules) {
            sb.append(String.format("    %s -%s-> %s\n",
                rule.from.name, Arrays.toString(rule.chars), rule.to.name));
        }
        sb.append("  ]\n");

        sb.append("  fsm:  [\n");
        for (int ln = 0; ln < _fsm.length; ln++) {
            sb.append(String.format("    %s [", _def.states[ln].name));
            var first = true;
            for (int at = 0; at < _fsm[ln].length; at++) {
                if (!first)
                    sb.append(", ");
                var target = _fsm[ln][at] >= 0 ? _def.states[_fsm[ln][at]].name : "XX";
                sb.append(String.format("'%s'-> %s", 
                    _def.alefBet[at], target));
                first = false;
            }
            sb.append("]\n");
        }
        sb.append("  ]");

        return sb.toString();
    }
}