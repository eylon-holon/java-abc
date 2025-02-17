class NondetermenisticFsm extends FSM {
    public NondetermenisticFsm(String name, Def def, boolean traceInit) {
    }

    public boolean isComplete() {
        return true;
    }

    public String[] getNotCompletedStates() {
        return new String[0];
    }

    public boolean accept(String word, boolean trace) {
        return false;
    }
}
