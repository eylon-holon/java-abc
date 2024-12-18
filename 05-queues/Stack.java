class Stack {
    private Node head;

    public Stack() {
    }

    public boolean isEmpty() {
        return head == null;
    }

    int top() {
        assert head != null;
        
        return head.getValue();
    }

    void push(int value) {
        head = new Node(value, head);
    }

    int pop() {
        assert head != null;

        int value = head.getValue();
        head = head.getNext();

        return value;
    }
}
