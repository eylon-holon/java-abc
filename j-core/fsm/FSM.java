import java.util.*;

class FSM {
    public static class With {
        public boolean log;
        public boolean strict;

        public With() {
            log = false;
            strict = false;
        }

        public With(boolean log, boolean strict) {
            this.log = log;
            this.strict = strict;
        }

        public With logs() { return new With(true, strict); }
        public With strict() { return new With(log, true); }
    }

    static class Letter {
        public int id;
        public char name;
    }

    static class State {
        public int id;
        public String name;
        public boolean ok;
    }

    static class Rule {
        public State from;
        public State to;
        public char[] chars;
    }

    static class Def {
        public State[] states;
        public char[] alefBet;
        public Rule[] rules;
    }

    private Def def;
    private With args;
    private int[] ch2at;
    private int[][] fsm;
    private int current;

    public FSM(Def def, With args) {
        this.def = def;
        this.args = args;

        ch2at = new int[Character.MAX_VALUE];
        for (var rule: def.rules) {
            for (int at = 0; at < rule.chars.length; at++) {
                ch2at[rule.chars[at]] = at;
            }
        }

        fsm = new int[def.states.length][def.alefBet.length];
        for (var rule: def.rules) {
            int from = rule.from.id;
            int to = rule.to.id;
            for (var ch: rule.chars) {
                var at = ch2at[ch];
                fsm[from][at] = to;
            }
        }
    }

    private boolean error(State in, String fmt) {
        System.out.println("ERROR: " + fmt);
        return false;
    }

    private void log(State from, State to, int at) {

    }

    private int getAt(char ch) {
        var chars = def.alefBet;
        for (int i = 0; i < chars.length; i++) {
            if (ch == chars[i])
                return i;
        }
        return 0;
    }

    private boolean next(char ch) {
        var from = def.states[current];
        assert from != null;

        var at = getAt(ch);
        if (at == 0)
            return error(from, "Can't find letter " + ch);

        var next = fsm[current][at];
        if (next == 0)
            return error(from, "Can't find next state " + next);

        var to = def.states[next];
        assert to != null;

        log(from, to, at);
        return true;
    }

    public boolean accept(String word) {
        current = 0;
        for (var ch: word.toCharArray()) {
            if (!next(ch))
                return false;
        }
        var state = getState(current);
        return state.ok;
    }

    @Override
    public String toString() {
        return "FSM(...)";
    }
}
