import java.util.Map;
import java.util.Properties;

interface IFSM {
    boolean isComplete();
    String[] getNotCompletedStates();
    boolean determenistic();
    boolean accept(String word, boolean trace);
    boolean accept(String word);
    boolean[] accept(String... many);
}

// todo: full: false --> then not-accepted instead of no-transition-error

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

    public static class Transition {
        public State from;
        public State to;
        public String[] rules;

        public Transition(State from, State to, String[] rules) {
            this.from = from;
            this.to = to;
            this.rules = rules;
        }
    }

    public static class Props {
        public Map<String, String> _map;

        public Props(Map<String, String> map) {
            _map = map;
        }

        private static boolean theyAreSame(String lhs, String rhs) {
            if (lhs == null && rhs == null)
                return true;
            if (lhs == null || rhs == null)
                return false;
            return 0 == lhs.compareToIgnoreCase(rhs);
        }
    
        public boolean hasTrueValue(String key) {
            var value = _map.get(key);

            if (value == null)
                return false;

            if (theyAreSame(value, "true"))
                return true;

            if (theyAreSame(value, "false"))
                return false;

            if (theyAreSame(value, "1"))
                return true;

            if (theyAreSame(value, "0"))
                return false;

            if (theyAreSame(value, "yes"))
                return true;

            if (theyAreSame(value, "no"))
                return false;

            FSM.print("WARN: '%s' has a wrong value: '%s'", key, value);

            return false;
        }

        public boolean containsKey(String key) {
            return _map.containsKey(key);
        }

        public String get(String key) {
            return _map.get(key);
        }

        public void put(String key, String value) {
            _map.put(key, value);
        }

        public int size() {
            return _map.size();
        }

        public boolean notfull() {
            return 
                hasTrueValue("not-full") ||
                hasTrueValue("notfull");
        }

        public boolean nondetermenistic() {
            return hasTrueValue("nondetermenistic");
        }

        public boolean withStack() {
            return 
                _map.containsKey("withstack") ||
                _map.containsKey("with-stack");
        }
    }

    public static class Def {
        public State[] states;
        public String[] alefBet;
        public Transition[] transitions;
        public Props props;

        public Def(State[] states, String[] alefBet, Transition[] transitions, Props props) {
            this.states = states;
            this.alefBet = alefBet;
            this.transitions = transitions;
            this.props = props;
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