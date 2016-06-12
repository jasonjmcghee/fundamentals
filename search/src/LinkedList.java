/**
 * Created by jmcghee on 6/11/16.
 */
public class LinkedList {
    private int item;
    private LinkedList next;

    public LinkedList (int item) {
        this.item = item;
    }

    public LinkedList (int item, LinkedList next) {
        this.item = item;
        this.next = next;
    }

    public void insert (int[] items) {
        for (int item : items) {
            this.insert(item);
        }
    }

    public void insert (int item) {
        if (this.next == null) {
            this.next = new LinkedList(item);
        } else {
            this.next.insert(item);
        }
    }

    public static LinkedList reverse(LinkedList head) {
        LinkedList reversed = null, next;

        while (head != null) {
            next = head.next;
            head.next = reversed;
            reversed = head;
            head = next;
        }

        return reversed;

    }

    public int length () {
        if (this.next == null) return 1;
        return 1 + this.next.length();
    }

    public int getItem() {
        return item;
    }

    public boolean equals(LinkedList other) {
        LinkedList tmp = this;
        for (; tmp.next != null; tmp = tmp.next, other = other.next) {
            if (other.next == null || tmp.item != other.item) {
                return false;
            }
        }
        return other.next == null;
    }

    public String toString () {
        String str = "[" + this.item + ", ";
        LinkedList tmp = this.next;
        for (; tmp != null; tmp = tmp.next) {
            str += tmp.item + ", ";
        }
        return str + "]";
    }
}
