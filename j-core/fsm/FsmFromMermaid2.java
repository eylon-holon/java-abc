class FsmFromMermaid2 {
    private static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    public static IFSM parse(String graphName, String filePath, boolean... logFlags) {
        var log = logFlags.length > 0 && logFlags[0];

        var graph = MermaidFile2.loadGraph(graphName, filePath, log);
        if (log) print("graph '%s': loaded", graph.name);

        var parser = FsmParser2.New(graph.lines, graph.props, log);
        if (log) print("graph '%s': parsed (%d lines)", graph.name, graph.lines.length);

        var def = new FSM.Def(
            parser.getStates(),
            parser.getAlefBet(),
            parser.getTransitions(),
            graph.props
        );

        if (log) print("graph '%s': ready for fsm", graph.name);

        if (graph.props.withStack())
            return new FsmWithStack(graphName, def, log);

        if (graph.props.nondetermenistic())
            return new NondetermenisticFsm(graphName, def, log);

        return
            new DetermenisticFsm(graphName, def, log);
    }    
}

// Ctrl+Shift+P(Command Pallete) -> Clean Java server language workspace-> Restart and delete
// https://stackoverflow.com/questions/67680483/getting-error-file-cannot-be-resolved-to-a-type-for-java-in-visual-studio-code
