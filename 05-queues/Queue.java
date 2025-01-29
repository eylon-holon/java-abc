class Queue {
    private Node head;
    private Node tail;

    public Queue() {

    }

    public boolean isEmpty() {
        return head == null;
    }

    public int head() {
        assert head != null;
        return head.getValue();
    }

    public void insert(int value) {
        var node = new Node(value);

        if (head == null) {
            head = node;
            tail = node;
            return;
        }

        tail.setNext(node);
        tail = node;
    }

    public int remove() {
        assert head != null;

        int value = head.getValue();
        head = head.getNext();

        if (head == null) {
            tail = null;
        }

        return value;
    }

    public static Queue fromArray(int[] a) {
        var que = new Queue();

        for (int x: a)
            que.insert(x);

        return que;
    }
}
