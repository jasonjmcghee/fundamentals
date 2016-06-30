import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by jmcghee on 6/28/16.
 */
public class SuffixArrayTest {

    SuffixArray sarray;

    @Before
    public void initialize () {
        String test = "blahblah";
        sarray = new SuffixArray(test);
    }

    @Test
    public void testGet() throws Exception {
        assertEquals("blah$", sarray.get(3));
    }

    @Test
    public void testSelect() throws Exception {
        assertEquals("bla", sarray.select(4, 3));
    }

    @Test
    public void testLcp() throws Exception {
        String result = Arrays.toString(sarray.getLcps());
        assertEquals("[0, 0, 2, 0, 4, 0, 1, 0, 3]", result);
    }

    @Test
    public void testRank() throws Exception {
        // None
        assertEquals(0, sarray.rank("$"));
        // Only dollar sign
        assertEquals(1, sarray.rank("aaaaaa"));
        // $, ah$, ahblah$, blah$, blahblah$
        assertEquals(5, sarray.rank("c"));
        // all
        assertEquals(9, sarray.rank("z"));
    }

    @Test
    public void testComplete() throws Exception {
        String test = "fantastic";
        SuffixArray testSuffixArray = new SuffixArray(test);
        String match = testSuffixArray.complete("fanta").get(0);
        assertEquals("fantastic$", match);
    }
}