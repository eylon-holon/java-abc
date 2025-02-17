import java.util.*;

interface IFSM {
    boolean isComplete();
    String[] getNotCompletedStates();
    boolean accept(String word, boolean trace);
    boolean accept(String word);
    boolean[] accept(String... many);
}

abstract class FSM implements IFSM {
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
        public String[] chars;

        public Rule(State from, State to, String[] chars) {
            this.from = from;
            this.to = to;
            this.chars = chars;
        }
    }

    public static class Def {
        public State[] states;
        public String[] alefBet;
        public Rule[] rules;
        public boolean nondetermenistic;

        public Def(State[] states, String[] alefBet, Rule[] rules) {
            this.states = states;
            this.alefBet = alefBet;
            this.rules = rules;
            this.nondetermenistic = false;
        }
    }

    protected static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    protected static int error(State in, String errMsg) {
        print("ERROR in %s: %s", in.name, errMsg);
        return -1;
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
}