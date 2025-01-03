import java.util.Arrays;

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

    private String name;
    private Def def;
    private int[] ch2at;
    private int[][] fsm;
    private int current;

    private boolean traceInit = false;

    private void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    public FSM(String name, Def def) {
        this.name = name;
        this.def = def;

        // initalize alef-bet

        ch2at = new int[Character.MAX_VALUE];

        for (int at = 0; at < def.alefBet.length; at++) {
            var ch = def.alefBet[at];
            ch2at[ch] = at;
            if (traceInit) print("ch2at['%s'] = %d  ==>  ch2at[%d] = %d", ch, at, (int) ch, at);
        }

        // initialize transition table

        fsm = new int[def.states.length][def.alefBet.length];

        for (var ln: fsm)
            Arrays.fill(ln, -1);

        for (var rule: def.rules) {
            int from = rule.from.id;
            int to = rule.to.id;
            for (var ch: rule.chars) {
                var at = ch2at[ch];
                fsm[from][at] = to;
                if (traceInit) print("fsm[%s]['%s'] = %s  ==>  fsm[%d][%d] = %d",
                    rule.from.name, ch, rule.to.name, from, at, to);
            }
        }
    }

    private int error(State in, String msg) {
        print("ERROR in %s: %s", in.name, msg);
        return -1;
    }

    private void traceTransition(State from, State to, int at) {
        print("  '%s': %s --> %s", def.alefBet[at], from.name, to.name);
    }

    private int getAt(char ch) {
        var chars = def.alefBet;
        for (int i = 0; i < chars.length; i++) {
            if (ch == chars[i])
                return i;
        }
        return -1;
    }

    private int next(char ch, boolean trace) {
        var from = def.states[current];
        assert from != null;

        var at = getAt(ch);
        if (at == -1)
            return error(from, String.format("Undefined transition '%s'", ch));

        var next = fsm[current][at];
        if (next == -1)
            return error(from, String.format("No transition for '%s' is defined", ch));

        var to = def.states[next];
        assert to != null;

        if (trace)
            traceTransition(from, to, at);

        return next;
    }

    public boolean accept(String word, boolean trace) {
        if (trace)
            print("FSM['%s']: accepting word '%s'", name, word);

        current = 0;

        for (var ch: word.toCharArray()) {
            current = next(ch, trace);
            
            if (current == -1)
                return false;
        }

        var ok = def.states[current].ok;

        if (trace)
            print("FSM['%s']: '%s' is %s", name, word, ok ? "accepted" : "wrong");

        return ok;
    }

    public boolean accept(String word) {
        return accept(word, false);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();

        sb
            .append(String.format(
            "FSM['%s', %d, %d, %d]", name, def.states.length, def.alefBet.length, def.rules.length))
            .append("\n");

        sb
            .append("  states:  [")
            .append(String.join(", ", Arrays
                .stream(def.states)
                .map(st -> String.format("%s(%d)", st.name, st.id))
                .toList()))
            .append("]\n");

        sb
            .append("  alefBet: ")
            .append(Arrays.toString(def.alefBet))
            .append("\n");

        sb.append("  rules:  [\n");
        for (var rule: def.rules) {
            sb.append(String.format("    %s -%s-> %s\n",
                rule.from.name, Arrays.toString(rule.chars), rule.to.name));
        }
        sb.append("  ]\n");

        sb.append("  fsm:  [\n");
        for (int ln = 0; ln < fsm.length; ln++) {
            sb.append(String.format("    %s [", def.states[ln].name));
            var first = true;
            for (int at = 0; at < fsm[ln].length; at++) {
                if (!first)
                    sb.append(", ");
                var target = fsm[ln][at] >= 0 ? def.states[fsm[ln][at]].name : "XX";
                sb.append(String.format("'%s'-> %s", 
                    def.alefBet[at], target));
                first = false;
            }
            sb.append("]\n");
        }
        sb.append("  ]");

        return sb.toString();
    }
}
