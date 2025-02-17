class NondetermenisticFsm extends FSM {
    private final String _name;
    private final Def _def;
    private final int[] _ch2at;
    private final int[][][] _fsm;

    private final boolean _hasNoStates;
    private final String[] _notCompletedStates;

    private int current;

    public NondetermenisticFsm(String name, Def def, boolean traceInit) {
        _name = name;
        _def = def;
        _ch2at = null;
        _fsm = null;
        _hasNoStates = false;
        _notCompletedStates = null;
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
