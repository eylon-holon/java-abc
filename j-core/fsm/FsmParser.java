import java.util.*;
import java.util.regex.Pattern;

interface IFsmParser {
    FSM.State[] getStates();
    String[] getAlefBet();
    FSM.Transition[] getTransitions();
}

class FsmParser implements IFsmParser {
    protected List<FSM.State> states = new ArrayList<>();
    protected Set<String> alefBet = new HashSet<>();
    protected List<FSM.Transition> transitions = new ArrayList<>();

    public FSM.State[] getStates() {
        return states.toArray(FSM.State[]::new);
    }

    public String[] getAlefBet() {
        var count = alefBet.size();
        var letters = new String[count];
        var i = 0;
        for (var letter: alefBet)
            letters[i++] = letter;
        return letters;
    }

    public FSM.Transition[] getTransitions() {
        return transitions.toArray(FSM.Transition[]::new);
    }

    protected static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    protected static String removeSpaces(String ln) {
        return ln.replaceAll("\\s","");
    }

    protected boolean itsAComment(String ln) {
        return ln.startsWith("%%");
    }

    protected boolean itsAStateLine(String ln) {
        ln = removeSpaces(ln);
        return
            ln.contains("((") && ln.contains("))");
    }

    protected boolean itsATransitionLine(String ln) {
        ln = removeSpaces(ln);

        var hasArrow = 
            (ln.contains("-") || ln.contains("=")) &&
            ln.contains(">");

        if (!hasArrow)
            return false;

        var transitions = ln.split(Pattern.quote("|"));
        var hasTransitions = transitions.length == 3;

        if (!hasTransitions) {
            print("WARN: '%s' is a transition without rules (%d); skipped", ln, transitions.length);
            return false;
        }

        return true;
    }

    protected static boolean parseOk(String def) {
        if (def.contains("(((") && def.contains(")))"))
            return true;

        if (def.contains(":::ok"))
            return true;

        return false;
    }

    protected static boolean parseStart(String def) {
        if (def.contains(":::start"))
            return true;

        return false;
    }

    protected FSM.State parseState(String def) {
        var id = states.size();
        var name = def.split(Pattern.quote("(("))[0];
        var ok = parseOk(def);

        var start = parseStart(def);

        if (start && id > 0)
            print("'%s': start state has to be the first state", def);

        return new FSM.State(id, name, ok);
    }

    protected void parseStates(String ln, boolean log) {
        ln = removeSpaces(ln);
        var defs = ln.split(";");

        for (var def: defs) {
            if (def.equals("")) 
                continue;

            var state = parseState(def);
            if (log) print("state: '%s' [%d, ok=%b]", state.name, state.id, state.ok);

            states.add(state);
        }
    }

    protected FSM.State getState(String name) {
        name = name.trim();
        
        for (var state: states) {
            if (state.name.equals(name))
                return state;
        }
        return null;
    }

    protected FSM.State[] parseFromStates(String ln) {
        var lhs = ln.contains("-") ? 
            ln.split("-")[0] : 
            ln.split("=")[0];

        var names = lhs.split("&");
        var states = new ArrayList<FSM.State>();

        for (var name: names) {
            var state = getState(name);

            if (state == null) {
                print("ERROR: transition '%s': from state '%s' is not defined", ln, name);
                continue;
            }

            states.add(state);
        }

        return states.toArray(FSM.State[]::new);
    }

    protected FSM.State parseToState(String ln) {
        var parts = ln.split(Pattern.quote("|"));
        var name = parts[parts.length-1];

        var state = getState(name);

        if (state == null)
            print("ERROR: transition '%s': to state '%s' is not defined", ln, name);

        return state;
    }

    protected String[] parseRules(String ln) {
        var split = ln.split(Pattern.quote("|"));
        var letters = split[1].split(",");

        var rules = new String[letters.length];

        for (int i = 0; i < letters.length; i++) {
            var letter = letters[i];

            if (letter.length() > 1)
                print("ERROR: currently only single letters are supported in alef-bet (%s)", ln);

            rules[i] = letter;
            alefBet.add(letter);
        }

        return rules;
    }

    protected void parseTransition(String ln, boolean log) {
        ln = removeSpaces(ln);

        var froms = parseFromStates(ln);
        var to = parseToState(ln);
        var rules = parseRules(ln);

        if (froms.length == 0 || to == null)
            return;

        for (var from: froms) {
            if (log) print("transition: %s -> %s %s", from.name, to.name, Arrays.toString(rules));
        
            var transition = new FSM.Transition(from, to, rules);
            transitions.add(transition);
        }
    }

    protected FsmParser() {
    }

    public FsmParser(String[] graph, boolean log) {
        for (var ln: graph) {
            if (itsAComment(ln))
                continue;
            else
            if (itsAStateLine(ln))
                parseStates(ln, log);
            else
            if (itsATransitionLine(ln))
                parseTransition(ln, log);
        }
    }
}
