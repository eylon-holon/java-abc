class Helpers {
    public static <T> Node<T> listFromArray(T[] values) {
        if (values.length == 0) {
            return null;
        }
        var head = new Node<>(values[0]);
        var tail = head;
        for (int i = 1; i < values.length; i++) {
            var newNode = new Node<>(values[i]);
            tail.setNext(newNode);
            tail = newNode;
        }
        return head;
    }

    public static Node<Integer> listFromArray(int[] values) {
        if (values.length == 0) {
            return null;
        }
        var head = new Node<>(values[0]);
        var tail = head;
        for (int i = 1; i < values.length; i++) {
            var newNode = new Node<>(values[i]);
            tail.setNext(newNode);
            tail = newNode;
        }
        return head;
    }

    public static <T> String toString(Node<T> node) {
        var sb = new StringBuilder();
        sb.append("[");
        while (node != null) {
            sb.append(node.getValue());
            if (node.hasNext()) {
                sb.append(", ");
            }
            node = node.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> String toString(BinNode<T> node) {
        var sb = new StringBuilder();
        sb.append("[");
        sb.append("]");
        return sb.toString();
    }
}
