class Queue<T> {
    private Node<T> head;

    public Queue() {

    }

    public boolean isEmpty() {
        if(node == null)
            return true;
        return false;
    }

    public T head() {
        
        return head.getValue();
    }

    public void insert(T value) {
        if(isEmpty())
        head = new Node<T>(value);
    }


    public T remove() {

        return null;
    }
}
