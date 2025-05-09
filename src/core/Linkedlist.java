package core;

// Basic linked list implementation, will only be interacted with by child classes
public class Linkedlist {
    public int list_length;
    public Node head;
    protected Node tail;

    // Constructors:
    public Linkedlist() {
        list_length = 0;
        head = null;
        tail = null;
    }

    Linkedlist(Node node) {
        list_length = 1;
        head = node;
        tail = head;
    }

    // Methods:
    //
    // adds only to tail, order doesn't matter
    public void appendList(Node new_node) {
        if (head == null) {
            head = tail = new_node;
        } else {
            tail.next = new_node;
            new_node.prev = tail;
            tail = new_node;
        }
        list_length++;
    }

    // removes from anywhere in the list
    public void removeList(int id) { 
        if (id == -1) {
            head = null;
            tail = null;
            return;
        }
        if (head == tail) {
            head = tail = null;
            list_length--;
            return;
        }

        if (head.id == id) { 
            head = head.next;
            if (head != null) {
                head.prev = null;
            }
            list_length--;
            return;
        }

        Node current = head;
        while (current != null && current.next != null) {
            if (current.next.id == id) { 
                Node to_delete = current.next;
                current.next = to_delete.next;
                if (to_delete.next != null) {
                    to_delete.next.prev = current;
                } else {
                    tail = current; 
                }
                list_length--;
                return;
            }
            current = current.next;
        }
        System.out.println(id + " not found");
        return;
    }

    public void printList() {
        Node temp = head;
        System.out.print("[ ");
        while (temp != null) {

            System.out.print(temp.Name); 
            if (temp.next != null) System.out.print(" , "); 
            temp = temp.next;
        }
        System.out.print("]");
        System.out.print("\n");
    }

    public Node findNode(int id) {
        Node temp = head;
        while (temp != null) {
            if (temp.id == id) {
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }
    
    public void sortList() {
        if (head == null || head.next == null) {
            return; 
        }
        for (Node i = head; i != null; i = i.next) {
            for (Node j = head; j != null; j = j.next) {
                if (i.id < j.id) { 
                    int temp = i.id; 
                    i.id = j.id;
                    j.id = temp;
                }
            }
        }
    }
}
