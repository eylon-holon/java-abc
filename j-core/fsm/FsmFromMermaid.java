class FsmFromMermaid {
    public static FSM parse(String name, String file) {
        var args = new FSM.With();
        var fsm = new FSM(args);
        return fsm;
    }    
}
