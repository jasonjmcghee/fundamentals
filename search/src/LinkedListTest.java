import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jmcghee on 6/11/16.
 */
public class LinkedListTest {

    @Test
    public void testReverse() throws Exception {
        LinkedList ll = new LinkedList(0);
        assertEquals(0, LinkedList.reverse(ll).getItem());

        ll = new LinkedList(0, new LinkedList(1));
        assertEquals(1, LinkedList.reverse(ll).getItem());

        ll = new LinkedList(0);
        LinkedList ll2 = new LinkedList(0);
        int[] items = {4, 3, 6, 1, 2, 7, 8, 0};
        int[] rev = {8, 7, 2, 1, 6, 3, 4, 0};
        ll.insert(items);
        ll2.insert(rev);
        LinkedList llr = LinkedList.reverse(ll);
        assert(llr.equals(ll2));
    }

    @Test
    public void testEquals() {
        LinkedList ll = new LinkedList(0);
        LinkedList ll2 = new LinkedList(0);
        int[] items = {4, 3, 6, 1, 2, 7, 8, 0};
        ll.insert(items);
        ll2.insert(items);

        assert(ll.equals(ll2));

        ll = new LinkedList(0);
        ll2 = new LinkedList(0);
        int[] items2 = {4, 3, 6, 1, 2, 7, 8};
        ll.insert(items2);
        ll2.insert(items2);
        ll.insert(0);

        assert(!ll.equals(ll2));
    }
}