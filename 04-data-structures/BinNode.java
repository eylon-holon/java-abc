class BinNode {
    private int value;
    private BinNode left;
    private BinNode right;

    BinNode(int value) {
        this.value = value;
    }  

    BinNode(int value, BinNode left, BinNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public int getValue() {
        return this.value;
    }

    public BinNode getLeft() {
        return this.left;
    }

    public BinNode getRight() {
        return this.right;
    }

    public boolean hasLeft() {
        return this.left != null;
    }

    public boolean hasRight() {
        return this.right != null;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setLeft(BinNode left) {
        this.left = left;
    }

    public void setRight(BinNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        String l = hasLeft() ? Integer.toString(left.getValue()) : "null";
        String r = hasRight() ? Integer.toString(right.getValue()) : "null";
        String children = hasLeft() || hasRight() ? String.format("[%s %s]", l, r) : "X";
        String me = Integer.toString(value);
        return String.format("%s --> %s", me, children);
    }
}
