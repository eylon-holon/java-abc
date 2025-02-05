class Queue<T> {
    private Node<T> head;

    public Queue() {
    }

    public boolean isEmpty() {
        if(head==null)
        {
        return true;
        }
        return false;
    }

    public T head() {
        return head.getValue();
    }

    public void insert(T value) {
    }

    public T remove() {
        return null;
    }
}
