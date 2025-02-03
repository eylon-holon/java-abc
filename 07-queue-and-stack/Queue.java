class Queue<T> {
    private Node<T> head;

    public Queue() {
    }

    public boolean isEmpty() {
        if (head == null) {
            return true;
        }
        else {
            return false;
        }
    }

    public T head() {
        return head.getValue();
    }

    public void insert(T value) {
        Node<T> node = new Node<T>(value);
        node.setValue(value);
        head = node;

    }

    public T remove() {
        T value = head.getValue();
        head = head.getNext();
        return value;
    }
}
