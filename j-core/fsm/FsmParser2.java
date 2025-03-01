class FsmParser2 extends FsmParser {
    protected FsmParser2(String[] graph, FSM.Props props, boolean log) {
        super(graph, log);
    }

    public static IFsmParser New(String[] graph, FSM.Props props, boolean log) {
        return props.withStack() ?
            new FsmParser(graph, log) : 
            new FsmParser2(graph, props, log);
    }
}
