import java.util.*;

class DetermenisticFsm extends FSM {
    private final String _name;
    private final Def _def;
    private final int[][] _fsm;

    private final boolean _hasNoStates;
    private final String[] _notCompletedStates;

    private int current;

    private static char letter2ch(String letter) {
        if (letter.length() != 1) {
            print("ERROR: Determenistic FSM supports only single letter rules: '%s'", letter);
            return 0;
        }

        return letter.charAt(0);        
    }

    private static int getAt(String[] alefBet, char ch) {
        for (int i = 0; i < alefBet.length; i++) {
            if (ch == alefBet[i].charAt(0))
                return i;
        }
        return -1;
    }

    public DetermenisticFsm(String name, Def def, boolean traceInit) {
        _name = name;
        _def = def;

        // initalize alef-bet

        for (int at = 0; at < def.alefBet.length; at++) {
            var ch = letter2ch(def.alefBet[at]);

            if (traceInit)
                print("ch2at['%s'] = %d  ==>  ch2at[%d] = %d", ch, at, (int) ch, at);
        }

        // initialize transition table

        _fsm = new int[def.states.length][def.alefBet.length];

        for (var ln: _fsm) {
            Arrays.fill(ln, -1);
        }

        for (var transition: def.transitions) {
            int from = transition.from.id;
            int to = transition.to.id;

            for (var letter: transition.rules) {
                var ch = letter2ch(letter);

                var at = getAt(def.alefBet, ch);
                assert at != -1;

                if (_fsm[from][at] != -1)
                    print("ERROR: state '%s' is not determenistic (transition: '%s'); \nPlease specify 'nondetermenistic' flag if it's the intention", transition.from.name, ch);
                
                _fsm[from][at] = to;

                if (traceInit)
                    print("fsm[%s]['%s'] = %s  ==>  fsm[%d][%d] = %d", transition.from.name, ch, transition.to.name, from, at, to);
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

    private void traceTransition(State from, State to, int at) {
        print("  '%s': %s --> %s", _def.alefBet[at], from.name, to.name);
    }

    private int next(char ch, boolean trace) {
        var from = _def.states[current];
        assert from != null;

        var at = getAt(_def.alefBet, ch);

        if (at == -1) {
            if (_def.props.notfull()) {
                if (trace)
                    print("  '%s': undefined transition '%s'", from.name, ch);
                return -1;
            }
            return error(from, String.format("Undefined transition '%s'", ch));
        }

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

    public boolean determenistic() {
        return true;
    }

    public boolean accept(String word, boolean trace) {
        trace |= _def.props.trace();

        word = word.trim();

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

    @Override
    public String toString() {
        var sb = new StringBuilder();

        sb
            .append(String.format(
            "FSM['%s', %d, %d, %d]", _name, _def.states.length, _def.alefBet.length, _def.transitions.length))
            .append("\n");

        var states = new String[_def.states.length];
        for (int i = 0; i < _def.states.length; i++) {
            var st = _def.states[i];
            states[i] = st.ok ? 
                String.format("%s((%d))", st.name, st.id) :
                String.format("%s(%d)", st.name, st.id); 
        }

        sb
            .append("  states:  [")
            .append(String.join(", ", states))
            .append("]\n");

        sb
            .append("  alefBet: ")
            .append(Arrays.toString(_def.alefBet))
            .append("\n");

        sb.append("  transitions:  [\n");
        for (var transition: _def.transitions) {
            sb.append(String.format("    %s -%s-> %s\n",
                transition.from.name, Arrays.toString(transition.rules), transition.to.name));
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