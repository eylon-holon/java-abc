class Node {
    private int value;
    private Node next;

    public Node(int value) {
        this.value = value;
    }

    public Node(int value, Node next) {
        this.value = value;
        this.next = next;
    }

    public Node(int[] values) {
        assert values.length > 0;
        value = values[0];

        Node node = this;
        for (int i = 1; i < values.length; i++) {
            node.setNext(new Node(values[i]));
            node = node.getNext();
        }
    }
    
    public int getValue() {
        return value;
    }

    public Node getNext() {
        return next;
    }

    public boolean hasNext() {
        return next != null;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (var node = this; node != null; node = node.getNext()) {
            sb.append(node.getValue());
            if (node.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
