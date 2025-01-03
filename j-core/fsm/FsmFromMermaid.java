class FsmFromMermaid {
    public static FSM parse(String name, String file) {
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

        var fsm = new FSM(name, def);

        return fsm;
    }    
}