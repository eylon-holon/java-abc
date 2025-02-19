class Queue<T> {
    private Node<T> head;

    public Queue() {
    }

    public boolean isEmpty() {
        if(head == null)
            return true;
        return false;
    }

    public T head() {
        if(isEmpty())
            return false;
        return head.getvalue();
    }

    public void insert(T value) {
        if(isEmpty())
            head = new Node <T>{value};
        private Node<T> node = head;
        while(node.getNext() != null)
            node = node.getNext();
        node.getNext() = new Node <T>{value};
    }

    public T remove() {
        if(isEmpty())
            return null;
        head = head.getNext();
    }
}
