class Queue<T> {
    private Node<T> head;

    public Queue() {
    }

    public boolean isEmpty() {
        if(head==null)
            return true;
        return false;
    }

    public T head() {
        return head.getValue();
    }

    public void insert(T value) {
        if(head==null)
            head = new Node<T>(value);
        else{
            private Node<T> node = head;
        while(h.getNext()!=null)
        
            {
                head.setValue(head.getNext());
            }
            head.setNext(value);
        }
    }

    public T remove() {
        T i = head.getValue();

        return i;
    }
}
