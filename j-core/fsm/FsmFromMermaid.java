import java.util.HashMap;

class FsmFromMermaid {
    public static IFSM fsm_101() {
        var q0 = new FSM.State(0, "q0", false);
        var q1 = new FSM.State(1, "q1", false);
        var q2 = new FSM.State(2, "q2", true);

        var r0 = new FSM.Transition(q0, q1, new String[] {"0", "1"});
        var r1 = new FSM.Transition(q1, q0, new String[] {"0"});
        var r2 = new FSM.Transition(q1, q2, new String[] {"1"});

        var def = new FSM.Def(
            new FSM.State[] {q0, q1, q2},
            new String[] {"0", "1"},
            new FSM.Transition[] {r0, r1, r2},
            new FSM.Props(new HashMap<>())
        );

        var fsm = new DetermenisticFsm("101", def, false);

        return fsm;
    }    

    public static IFSM parse(String graphName, String filePath, boolean... logFlags) {
        var log = logFlags.length > 0 && logFlags[0];

        var graph = MermaidFile.readGraphLines(graphName, filePath, log);
        var parser = new FsmParser(graph, log);

        var def = new FSM.Def(
            parser.getStates(),
            parser.getAlefBet(),
            parser.getTransitions(),
            new FSM.Props(new HashMap<>())
        );

        return new DetermenisticFsm(graphName, def, log);
    }    
}

// Ctrl+Shift+P(Command Pallete) -> Clean Java server language workspace-> Restart and delete
// https://stackoverflow.com/questions/67680483/getting-error-file-cannot-be-resolved-to-a-type-for-java-in-visual-studio-code
