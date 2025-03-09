import java.util.regex.*;

class FsmParser2 extends FsmParser {
    public static class LnItr {
        private final String[] lines;
        private int at;

        public LnItr(String[] lines) {
            this.lines = lines;
            at = 0;
        }

        public boolean hasNext() {
            return at < lines.length;
        }

        public String next() {
            return lines[at++];
        }

        public String peek() {
            return lines[at];
        }

        public int size() {
            return lines.length;
        }
    }

    public static IFsmParser New(String[] graph, FSM.Props props, boolean log) {
        return !props.withStack() ?
            new FsmParser(graph, log) : 
            new FsmParser2(graph, props, log);
    }

    protected String[] parseRules(LnItr it) {
        return null;
    }
    
    protected boolean itsAComment(LnItr it) {
        return super.itsAComment(it.peek());
    }

    protected boolean itsAStateLine(LnItr it) {
        return super.itsAStateLine(it.peek());
    }

    protected boolean itsATransitionLine(LnItr it) {
        var ln = removeSpaces(it.peek());

        var hasArrow = 
            (ln.contains("-") || ln.contains("=")) &&
            ln.contains(">");

        if (!hasArrow)
            return false;

        if (!ln.contains("|\""))
            print("WARN: '%s' is a transition line without '|\"'", ln);

        return true;
    }

    protected void parseStates(LnItr it, boolean log) {
        var ln = it.next();
        super.parseStates(ln, log);
    }

    private String[] breakTransitionMultiLine(String ln) {
        var pattern = Pattern.compile("(.*?)\\|\"(.*?)\"\\|(.*)");
        var regex = pattern.matcher(ln);

        if (!regex.matches())
            return new String[0];

        return new String[] {
            regex.group(1).trim(),
            regex.group(2).trim(),
            regex.group(3).trim()
        };
    }

    protected void parseRule(String rule) {
        var split = rule.split("\\|");

        if (split.length != 2) {
            print("ERROR: failed to parse rule '%s' (split)", rule);
            return;
        }

        var condition = split[0].trim().split("\\s");

        if (condition.length != 2) {
            print("ERROR: failed to parse rule '%s' (condition)", rule);
            return;
        }

        if (condition[0].length() != 1) {
            print("ERROR: failed to parse rule '%s' (letter)", rule);
            return;
        }

        alefBet.add(condition[0].substring(0, 1));
    }

    protected void parseTransitions(LnItr it, boolean log) {
        var firstLn = it.next();
        var ln = firstLn;
  
        // reconstruct as a single string

        var sb = new StringBuffer();
        
        for (;;) {
            sb.append(ln);

            if (ln.contains("\"|"))
                break;

            if (!it.hasNext())
                break;

            sb.append("&");
            ln = it.next();
        }

        // break into Q0 --> RULES Q1
        var split = breakTransitionMultiLine(sb.toString());

        if (split.length != 3) {
            print("ERROR: failed to parse transitions '%s'", firstLn);
            return;
        }

        //print("### '%s' ### '%s'", split[0], split[2]);

        // parse states

        var fromStates = parseFromStates(split[0]);

        if (fromStates.length == 0)
            return;

        var from = fromStates[0];
        var to = getState(split[2]);

        if (to == null) {
            print("ERROR: failed to parse to state of '%s'", firstLn);
            return;
        }

        // parse rules

        var rules = split[1].split("&");
        for (int i = 0; i < rules.length; i++) {
            var rule = rules[i].trim();
            parseRule(rule);
            rules[i] = rule;
        }

        // add transition

        var transition = new FSM.Transition(from, to, rules);
        transitions.add(transition);

        if (log) print("transition: %s", transition);
    }

    protected void skipEmptyLines(LnItr it) {
        while (it.hasNext() && it.peek().trim().length() == 0)
            it.next();
    }

    protected void skipHeader(LnItr it) {
        skipEmptyLines(it);

        if (!it.peek().startsWith("---"))
            return;

        it.next();

        while (it.hasNext()) {
            if (it.next().startsWith("---"))
                break;
        }

        skipEmptyLines(it);

        if (it.hasNext() && it.peek().toLowerCase().contains("graph"))
            it.next();
    }

    protected FsmParser2(String[] graph, FSM.Props props, boolean log) {
        var it = new LnItr(graph);

        skipHeader(it);

        while (it.hasNext()) {
            skipEmptyLines(it);
            if (itsAComment(it))
                it.next();
            else
            if (itsAStateLine(it))
                parseStates(it, log);
            else
            if (itsATransitionLine(it))
                parseTransitions(it, log);
            else {
                var ln = it.next();
                print("WARN: line '%s' is not recognized; skipped", ln);
            }
        }
    }
}
