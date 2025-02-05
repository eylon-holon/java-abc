class Queue<T> {
    private Node<T> head;

    public Queue() {
    }

    public boolean isEmpty() {
        if (head == null) {
            return true;
        }
        return false;
    }

    public T head() {
        return head.getValue();
    }

    ??????????
    public void insert(T value) {
         while (head != null) {
            if (head.getNext() == null) {
                head.setNext(value);
            }
            head = head.getNext();
         }
    }

    public T remove() {
        T value = head.getValue();
        head = head.getNext();
        return value;
    }
}
