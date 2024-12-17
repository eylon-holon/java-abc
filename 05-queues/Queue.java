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
        tail.next = new Node(value, tail);
        if (head == null) {
            head = tail;
        }
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
}
