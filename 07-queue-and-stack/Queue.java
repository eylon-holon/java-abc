class Queue<T> {
    private Node<T> head;

    public Queue() {
        this.head=null;
    }

    public boolean isEmpty() {
        if (this.head == null)
        return true;
        else
        return false;
        
    }

   

    public void insert(T value) {
      int a=new int [this.head.length()+1];
      Node node=null;
      int count=0;
      while (this.head!=null){
      a[count]=this.head.getValue;
      count++;
      this.head=this.head.getNext;
      }
      a[this.head.length()+1]=value;
      

    }

    public T remove() {
        return null;
    }
}
