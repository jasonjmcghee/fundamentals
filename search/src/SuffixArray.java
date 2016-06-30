import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Created by jmcghee on 6/28/16.
 */
public class SuffixArray {

    private Integer[] suffixes;
    private String original;
    private char[] text;
    private int length;
    private Integer[] lcps;
    private static final String filesToSearch = "/Users/jmcghee/workspace/git/fundamentals/search/filesToSearch/";

    public SuffixArray (String original) {
        this.original = original + "$";
        this.text = this.original.toCharArray();
        this.length  = this.original.length();
        this.suffixes = new Integer[this.length];
        for (int i = 0; i < this.length; i++) {
            this.suffixes[i] = i;
        }
        SedgewickQuick.sort(this);
        this.lcps = new Integer[this.length];
        for (int i = 0; i < this.length; i++) {
            this.lcps[i] = this.lcp(i, i - 1);
        }
    }

    public SuffixArray(String original, Integer[] suffixes, Integer[] lcps) {
        this.original = original;
        this.suffixes = suffixes;
        this.lcps = lcps;
        this.length = original.length();
    }

    public int length () {
        return this.length;
    }

    public int getIndex(int i) {
        if (i < 0 || i >= this.length) throw new IndexOutOfBoundsException();
        return this.suffixes[i];
    }

    public String getText() {
        return this.original;
    }

    public String get (int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= this.length) throw new IndexOutOfBoundsException();
        return this.original.substring(this.suffixes[i]);
    }

    public String select(int i, int j) throws IndexOutOfBoundsException {
        if (i < 0 || i >= this.length || j < 0 || j >= this.length) throw new IndexOutOfBoundsException();
        return this.get(i).substring(0, j);
    }

    private int lcp (int i, int j) {
        if (j < 0) return 0;
        int length = 0;
        for (int k = 0; k < this.get(i).length() && k < this.get(j).length(); k++) {
            if (this.get(i).charAt(k) != this.get(j).charAt(k)) return length;
            length++;
        }
        return length;
    }

    // Longest Common Prefix
    public int longestCommonPrefix (int i) {
        return this.lcps[i];
    }

    public Integer[] getLcps() {
        return this.lcps;
    }

    public int rank (String query) {
        int low = 0;
        int high = this.length - 1;
        int mid;
        int cmp;
        while (low <= high) {
            mid = (low + high) / 2;
            cmp = query.compareTo(this.get(mid));
            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return low;
    }

    public int[] find (String pattern) {
        int start = rank(pattern);
        char last = pattern.charAt(pattern.length() - 1);
        last++;
        int end = rank(pattern.substring(0, pattern.length() - 1) + last);
        int [] range = {start, end};
        return range;
    }

    public ArrayList<String> complete(String query) {
        return complete(query, 1);
    }

    public ArrayList<String> complete(String query, int max) {
        return complete(query, max, 0);
    }

    public ArrayList<String> complete(String query, int max, int error) {
        ArrayList<String> completions = new ArrayList<>();
        if (query.length() > 0) {
            try {
                int rank = rank(query);
                for (int i = rank - error; i <= max + rank - error; i++) {
                    if (error == 0) {
                        if (this.select(i, query.length()).equals(query)) {
                            completions.add(this.get(i));
                        } else {
                            return completions;
                        }
                    } else {
                        completions.add(this.get(i));
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                return new ArrayList<>();
            }
        }
        return completions;
    }

    public static SuffixArray build (String file, String tableName) {
        String fileName = SuffixArray.filesToSearch + file;
        SuffixArray indexed;
        try {
            indexed = SuffixArrayDAO.read(tableName);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e);
            indexed = SuffixArrayDAO.buildFromFile(fileName, tableName);
        }
        return indexed;
    }

    public static void main (String[] args) {

        String tableName = "TWOCITIESSHORT";
        SuffixArray sarray = build("TaleOfTwoCities", tableName);
        try {
            ArrayList<String> longest10k = SuffixArrayDAO.longestRepeatedSubstrings(tableName, 10000, sarray.getText());
            longest10k.forEach(System.out::println);
        } catch (SQLException|ClassNotFoundException|IOException e) {
            e.printStackTrace();
        }

    }

    // 3-way string quicksort from Algorithms 4th
    static class SedgewickQuick {

        private static SuffixArray s;

        public static void sort(SuffixArray s) {
            SedgewickQuick.s = s;
            sort(0, s.length()-1, 0);
        }

        public static void sort(int lo, int hi, int d) {

            // cutoff to insertion sort for small subarrays
            if (hi <= lo + 5) {
                insertion(lo, hi, d);
                return;
            }

            int lt = lo, gt = hi;
            char v = s.text[s.suffixes[lo] + d];
            int i = lo + 1;
            while (i <= gt) {
                char t = s.text[s.suffixes[i] + d];
                if      (t < v) exch(lt++, i++);
                else if (t > v) exch(i, gt--);
                else            i++;
            }

            // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
            sort(lo, lt-1, d);
            if (v > 0) sort(lt, gt, d+1);
            sort(gt+1, hi, d);
        }

        private static void insertion(int lo, int hi, int d) {
            for (int i = lo; i <= hi; i++)
                for (int j = i; j > lo && less(s.suffixes[j], s.suffixes[j-1], d); j--)
                    exch(j, j-1);
        }

        private static boolean less(int i, int j, int d) {
            if (i == j) return false;
            i = i + d;
            j = j + d;
            while (i < s.length() && j < s.length()) {
                if (s.text[i] < s.text[j]) return true;
                if (s.text[i] > s.text[j]) return false;
                i++;
                j++;
            }
            return i > j;
        }

        private static void exch(int i, int j) {
            int swap = s.suffixes[i];
            s.suffixes[i] = s.suffixes[j];
            s.suffixes[j] = swap;
        }
    }

    // If I use Collections.sort
    class LexicalComparator implements Comparator<Integer> {

        private SuffixArray s;

        public LexicalComparator (SuffixArray s) {
            this.s = s;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return s.original.substring(o1).compareTo(s.original.substring(o2));
        }

        @Override
        public Comparator<Integer> reversed() {
            return null;
        }

        @Override
        public Comparator<Integer> thenComparing(Comparator<? super Integer> other) {
            return null;
        }

        @Override
        public <U> Comparator<Integer> thenComparing(Function<? super Integer, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
            return null;
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<Integer> thenComparing(Function<? super Integer, ? extends U> keyExtractor) {
            return null;
        }

        @Override
        public Comparator<Integer> thenComparingInt(ToIntFunction<? super Integer> keyExtractor) {
            return null;
        }

        @Override
        public Comparator<Integer> thenComparingLong(ToLongFunction<? super Integer> keyExtractor) {
            return null;
        }

        @Override
        public Comparator<Integer> thenComparingDouble(ToDoubleFunction<? super Integer> keyExtractor) {
            return null;
        }
    }

    static class StringLengthComparator implements Comparator<String> {
        public int compare (String a, String b) {
            return -(a.length() - b.length());
        }
    }
}


