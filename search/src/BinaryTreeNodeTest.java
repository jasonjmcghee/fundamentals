import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jmcghee on 6/11/16.
 */
public class BinaryTreeNodeTest {

    private BinaryTreeNode tree;

    @Before
    public void setup () {
        try {
            int[] list = {1, 5, 2, 7, 8, 16, 18, 6, 15, 14};
            tree = new BinaryTreeNode(list);
        } catch (Exception e) {
            System.out.println("Make sure the list doesn't have duplicates.");
        }
    }

    @Test
    public void testFind() throws Exception {
        assertEquals(8, tree.find(8).getData());
        assertEquals(1, tree.find(1).getData());
        assertEquals(18, tree.find(18).getData());
        assertEquals(null, tree.find(3));
    }

    @Test
    public void testInsert() throws Exception {
        tree.insert(0);
        assertEquals(0, tree.find(0).getData());

        tree.insert(100);
        assertEquals(100, tree.find(100).getData());

        tree.insert(9);
        assertEquals(9, tree.find(9).getData());
    }

    @Test
    public void testFloor() throws Exception {
        assertEquals(18, tree.floor(95).getData());
        assertEquals(16, tree.floor(17).getData());
        assertEquals(8, tree.floor(13).getData());
        assertEquals(2, tree.floor(3).getData());
        assertEquals(1, tree.floor(1).getData());
        assertEquals(null, tree.floor(0));
    }

    @Test
    public void testCeil() throws Exception {
        assertEquals(null, tree.ceil(95));
        assertEquals(18, tree.ceil(17).getData());
        assertEquals(14, tree.ceil(13).getData());
        assertEquals(5, tree.ceil(3).getData());
        assertEquals(1, tree.ceil(1).getData());
        assertEquals(1, tree.ceil(0).getData());
    }

    @Test
    public void testMinimum() throws Exception {
        assertEquals(1, tree.minimum().getData());
    }

    @Test
    public void testMaximum() throws Exception {
        assertEquals(18, tree.maximum().getData());
    }

    @Test
    public void testDeleteMin() throws Exception {
        tree.deleteMin();
        assertEquals(2, tree.minimum().getData());
    }

    @Test
    public void testDeleteMax() throws Exception {
        tree.deleteMax();
        assertEquals(16, tree.maximum().getData());
    }

    @Test
    public void testDelete() throws Exception {
        // No children
        tree.delete(18);
        assertEquals(16, tree.maximum().getData());

        // 1 Child
        tree.delete(15);
        assertEquals(14, tree.floor(15).getData());

        // 2 Children
        tree.delete(5);
        assertEquals(6, tree.ceil(5).getData());
    }
}