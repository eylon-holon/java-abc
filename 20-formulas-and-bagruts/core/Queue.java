class Queue<T> {
    private Node<T> head;
    private Node<T> tail;

    public Queue() {

    }

    public boolean isEmpty() {
        return head == null;
    }

    public T head() {
        return head.getValue();
    }

    public void insert(T value) {
        var node = new Node<T>(value);

        if (head == null) {
            head = node;
            tail = node;
            return;
        }

        tail.setNext(node);
        tail = node;
    }

    public T remove() {
        T value = head.getValue();
        head = head.getNext();

        if (head == null) {
            tail = null;
        }

        return value;
    }

    public String toString() {
        return Helpers.toString(this);
    }
}
