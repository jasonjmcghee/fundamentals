import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by jmcghee on 6/11/16.
 */
public class DynamicArray {

    private int[] array;
    private int length;

    public DynamicArray () {
        this.array = new int[0];
    }

    public DynamicArray (int[] array) {
        this.array = array;
        this.length = this.array.length;
    }

    public void add (DynamicArray list) {
        for (int i = 0; i < this.length; i++) {
            this.add(list.get(i));
        }
    }

    public void add (int[] list) {
        for (int n : list) {
            this.add(n);
        }
    }

    public void add (int x) {
        if (this.array.length == this.length) {
            int[] temp = this.array;
            this.array = new int[this.array.length * 2];
            for (int i = 0; i < this.length; i++) {
                this.array[i] = temp[i];
            }
        }
        this.array[this.length] = x;
        this.length++;
    }

    public void remove (int x) {
        int spliceIndex = this.find(x);
        if (spliceIndex == -1) return;
        int[] temp;
        if (this.length - 1 <= this.array.length / 4) {
            temp = new int[this.array.length / 2];
        } else {
            temp = new int[this.array.length];
        }

        for (int i = 0; i < this.length; i++) {
            if (i < spliceIndex) {
                temp[i] = this.array[i];
            } else if (i > spliceIndex) {
                temp[i - 1] = this.array[i];
            }
        }
        this.array = temp;
        this.length--;
    }

    public int find (int x) {
        for (int i = 0; i < this.length; i++) {
            if (this.array[i] == x) {
                return i;
            }
        }
        return -1;
    }

    public int get (int i) {
        return this.array[i];
    }

    public void set (int i, int x) {
        this.array[i] = x;
    }

    public boolean equals (DynamicArray list) {
        if (this.length != list.length) return false;
        for (int i = 0; i < this.length; i++) {
            if (list.get(i) != this.get(i)) {
                return false;
            }
        }
        return true;
    }

    public int size () {
        return this.length;
    }

    public int allocSize() {
        return this.array.length;
    }

    public String toString() {
        return Arrays.toString(this.array);
    }
}
