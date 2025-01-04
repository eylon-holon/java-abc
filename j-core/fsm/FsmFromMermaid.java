class FsmFromMermaid {
    public static FSM fsm_101() {
        var q0 = new FSM.State(0, "q0", false);
        var q1 = new FSM.State(1, "q1", false);
        var q2 = new FSM.State(2, "q2", true);

        var r0 = new FSM.Rule(q0, q1, new char[] {'0', '1'});
        var r1 = new FSM.Rule(q1, q0, new char[] {'0'});
        var r2 = new FSM.Rule(q1, q2, new char[] {'1'});

        var def = new FSM.Def(
            new FSM.State[] {q0, q1, q2},
            new char[] {'0', '1'},
            new FSM.Rule[] {r0, r1, r2}
        );

        var fsm = new FSM("101", def, false);

        return fsm;
    }    

    public static FSM parse(String graphName, String filePath, boolean... logFlags) {
        var log = logFlags.length > 0 && logFlags[0];

        var graph = MermaidFile.readGraphLines(graphName, filePath, log);
        var parser = new FsmParser(graph, log);

        var def = new FSM.Def(
            parser.getStates(),
            parser.getAlefBet(),
            parser.getRules()
        );

        return new FSM(graphName, def, log);
    }    
}

// https://stackoverflow.com/questions/67680483/getting-error-file-cannot-be-resolved-to-a-type-for-java-in-visual-studio-code
