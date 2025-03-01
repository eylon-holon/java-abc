class FsmFromMermaid2 {
    private static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    public static IFSM parse(String graphName, String filePath, boolean... logFlags) {
        var log = logFlags.length > 0 && logFlags[0];

        var graph = MermaidFile2.loadGraph(graphName, filePath, log);
        if (log) print("graph '%s': loaded", graph.name);

        var parser = new FsmParser(graph.lines, log);
        if (log) print("graph '%s': parsed", graph.name);

        var def = new FSM.Def(
            parser.getStates(),
            parser.getAlefBet(),
            parser.getTransitions(),
            graph.props
        );

        if (log) print("graph '%s': ready for fsm", graph.name);

        return graph.props.nondetermenistic() ?
            new NondetermenisticFsm(graphName, def, log) : 
            new DetermenisticFsm(graphName, def, log);
    }    
}

// Ctrl+Shift+P(Command Pallete) -> Clean Java server language workspace-> Restart and delete
// https://stackoverflow.com/questions/67680483/getting-error-file-cannot-be-resolved-to-a-type-for-java-in-visual-studio-code
