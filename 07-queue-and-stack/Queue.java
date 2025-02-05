class Queue<T> {
    private Node<T> head;

    public Queue() {
    }

    public boolean isEmpty() {
        if (head == null)
            return true;
        else
            return false;
    }

    public T head() {
        return head.getValue();
    }

    public void insert(T value) {
    }

    public T remove() {
        head = head.getNext();
        return head.getValue();
    }
}
