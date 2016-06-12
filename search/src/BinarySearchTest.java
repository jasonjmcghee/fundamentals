import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by jmcghee on 6/10/16.
 */
public class BinarySearchTest {

    @Test
    public void testFind() throws Exception {
        int[] list = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertEquals("Should return 4: ", 4, BinarySearch.find(4, list));

        int[] list2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertEquals("Should return 0: ", 0, BinarySearch.find(0, list2));

        int[] list3 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertEquals("Should return 10: ", 10, BinarySearch.find(10, list3));

        int[] list4 = {};
        assertEquals("Should return -1: ", -1, BinarySearch.find(3, list4));

        int[] list5 = {0};
        assertEquals("Should return -1: ", -1, BinarySearch.find(3, list5));
    }
}