import java.util.*;

class NondetermenisticFsm extends FSM {
    private final String _name;
    private final Def _def;
    private final int[][][] _fsm;

    private final boolean _hasNoStates;
    private final String[] _notCompletedStates;

    private boolean _determenistic;

    private static boolean addTarget(int[][][] fsm, int from, int at, int to) {
        var prev = fsm[from][at];

        var count = prev == null ? 1 : prev.length + 1;
        var targets = new int[count];

        for (int i = 0; i < count-1; i++)
            targets[i] = prev[i];

        targets[count-1] = to;
        fsm[from][at] = targets;

        return count == 1;
    }

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

    public NondetermenisticFsm(String name, Def def, boolean traceInit) {
        _name = name;
        _def = def;

        // initalize alef-bet

        for (int at = 0; at < def.alefBet.length; at++) {
            var ch = letter2ch(def.alefBet[at]);

            if (traceInit)
                print("ch2at['%s'] = %d  ==>  ch2at[%d] = %d", ch, at, (int) ch, at);
        }

        // initialize transition table

        _fsm = new int[def.states.length][def.alefBet.length][];
        _determenistic = true;

        for (var transition: def.transitions) {
            int from = transition.from.id;
            int to = transition.to.id;

            for (var letter: transition.rules) {
                var ch = letter2ch(letter);

                var at = getAt(def.alefBet, ch);
                assert at != -1;

                _determenistic &= addTarget(_fsm, from, at, to);
                
                if (traceInit)
                    print("nfsm[%s]['%s'] = %s  ==>  nfsm[%d][%d] = %d", transition.from.name, ch, transition.to.name, from, at, to);
            }
        }

        // initialize not completed states

        _hasNoStates = _def.states.length == 0;

        var notCompletedNames = new ArrayList<String>();

        for (int lnNo = 0; lnNo < _fsm.length; lnNo++) {
            var ln = _fsm[lnNo];

            var completed = true;
            for (var target: ln) {
                completed &= target != null;
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

    private void traceTransition(State from, State to, int at, List<String> log) {
        var line = String.format("  '%s': %s --> %s", _def.alefBet[at], from.name, to.name);
        log.add(line);
    }

    public boolean isComplete() {
        return _notCompletedStates.length == 0;
    }

    public String[] getNotCompletedStates() {
        return _notCompletedStates;
    }

    public boolean determenistic() {
        return _determenistic;
    }

    private boolean iterate(String word, int current, int step, boolean trace, List<String> log) {
        var from = _def.states[current];
        assert from != null;

        if (step >= word.length())
            return from.ok;

        var ch = word.charAt(step);
        var at = getAt(_def.alefBet, ch);

        if (at == -1) {
            if (_determenistic && trace)
                error(from, String.format("Undefined transition '%s'", ch));
            return false;
        }

        var targets = _fsm[current][at];

        if (targets == null || targets.length == 0) {
            if (_determenistic && trace)
                error(from, String.format("No transition for '%s' is defined", ch));
            return false;
        }

        var ok = false;
        var next = -1;

        for (int i = 0; i < targets.length; i++) {
            next = targets[i];
            ok = iterate(word, next, step+1, trace, log);

            if (ok)
                break;              // success path is found
        }

        if (trace && (ok || _determenistic)) {
            var to = _def.states[next];
            assert to != null;
            traceTransition(from, to, at, log);
        }

        return ok;
    }

    public boolean accept(String word, boolean trace) {
        if (trace)
            print("NFSM['%s']: accepting word '%s'", _name, word);

        if (_hasNoStates) {
            print("NFSM['%s']: I have no states", _name);
            return false;
        }

        var log = new ArrayList<String>();
        var ok = iterate(word, 0, 0, trace, log);

        if (trace) {
            for (int i = log.size(); i-- > 0; )
                print(log.get(i));
            print("NFSM['%s']: '%s' is %s", _name, word, ok ? "accepted" : "wrong");
        }

        return ok;
    }

    private static String getTargets(Def def, int[][][] fsm, int from, int at, String xx) {
        var targets = fsm[from][at];

        if (targets == null || targets.length == 0)
            return xx;

        if (targets.length == 1)
            return def.states[targets[0]].name;

        var names = new String[targets.length];

        for (int i = 0; i < targets.length; i++)
            names[i] = def.states[targets[i]].name;

        return Arrays.toString(names);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();

        sb
            .append(String.format(
            "NFSM['%s', %d, %d, %d]", _name, _def.states.length, _def.alefBet.length, _def.transitions.length))
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
                var targets = getTargets(_def, _fsm, ln, at, "XX");
                sb.append(String.format("'%s'-> %s", 
                    _def.alefBet[at], targets));
                first = false;
            }
            sb.append("]\n");
        }
        sb.append("  ]");

        return sb.toString();
    }
}