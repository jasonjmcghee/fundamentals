import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jmcghee on 6/11/16.
 */
public class DynamicArrayTest {

    @Test
    public void testAdd() throws Exception {
        int[] starter = {4, 3, 7, 2, 8, 3, 2};
        DynamicArray list = new DynamicArray(starter);

        list.add(4);
        int[] other = {4, 3, 7, 2, 8, 3, 2, 4};
        DynamicArray list2 = new DynamicArray(other);
        assert(list.equals(list2));
        assertEquals(14, list.allocSize());
    }

    @Test
    public void testRemove() throws Exception {
        int[] starter = {4, 3, 7, 2, 8, 3, 2, 4};
        DynamicArray list = new DynamicArray(starter);
        list.remove(3);
        int[] other = {4, 7, 2, 8, 3, 2, 4};
        DynamicArray list2 = new DynamicArray(other);
        assert(list.equals(list2));

        for (int i = 0; i < 5; i++) list.remove(other[i]);
        assertEquals(4, list.allocSize());
    }
}