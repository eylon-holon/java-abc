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

    public static Queue<Integer> queueFromArray(int[] values) {
        var que = new Queue<Integer>();
        for (var x: values) {
            que.insert(x);
        }
        return que;
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

    public static <T> String toString(Queue<T> que) {
        var sb = new StringBuilder();
        sb.append("[");
        var tmp = new Queue<T>();
        while (!que.isEmpty()) {
            var x = que.remove();
            tmp.insert(x);
            sb.append(x);
            if (!que.isEmpty()) {
                sb.append(", ");
            }
        }
        while (!tmp.isEmpty()) {
            que.insert(tmp.remove());
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

    public static String node2str(BinNode<Integer> node) {
        String l = node.hasLeft() ? Integer.toString(node.getLeft().getValue()) : "null";
        String r = node.hasRight() ? Integer.toString(node.getRight().getValue()) : "null";
        String children = node.hasLeft() || node.hasRight() ? String.format("[%s %s]", l, r) : "X";
        String me = Integer.toString(node.getValue());
        return String.format("%s --> %s", me, children);
    }

    public static void print3(BinNode<Integer> node) {
        if (node == null) {
            return;
        }
        System.out.println(node2str(node));
        print3(node.getLeft());
        print3(node.getRight());
    }
}
