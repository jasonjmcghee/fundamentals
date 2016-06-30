import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jmcghee on 6/28/16.
 */
public class SuffixArray {

    private Integer[] suffixes;
    private String original;
    private char[] text;
    private int length;
    private Integer[] lcps;

    public SuffixArray (String original) {
        this.original = original + "$";
        this.text = this.original.toCharArray();
        this.length  = this.original.length();
        this.suffixes = new Integer[this.length];
        for (int i = 0; i < this.length; i++) {
            this.suffixes[i] = i;
        }
        SedgewickRadix.sort(this);
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

    public static ArrayList<String> buildSuffixes (String raw) {
        int length = raw.length();
        ArrayList<String> suffixes = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            suffixes.add(raw.substring(i));
        }
        return suffixes;
    }

    public void rebuildLcps() {
        Integer[] lcps = new Integer[this.length];
        for (int i = 1; i < this.length; i++) {
            lcps[i] = this.lcp(i, i - 1);
        }
        this.lcps = lcps;
    }

    public int length () {
        return this.length;
    }

    public String get (int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= this.length) throw new IndexOutOfBoundsException();
        return this.original.substring(this.suffixes[i]);
    }

    public String select(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= this.length) throw new IndexOutOfBoundsException();
        return this.get(i).substring(this.length - i);
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

    static class StringLengthComparator implements Comparator<String> {
        public int compare (String a, String b) {
            return -(a.length() - b.length());
        }
    }

    public String longestRepeatedSubstring (String tableName) throws ClassNotFoundException, SQLException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        ResultSet rs = statement.executeQuery( "SELECT SUFFIX, MAX(LCP) FROM " + tableName + ";" );
        int maxIndex = 0;
        int lcp = 0;
        while ( rs.next() ) {
            maxIndex = rs.getInt("SUFFIX");
            lcp = rs.getInt("MAX(LCP)");
        }
        return this.original.substring(maxIndex, maxIndex + lcp);
    }

    public ArrayList<String> longestRepeatedSubstrings (String tableName, int number) throws ClassNotFoundException, SQLException {
        ArrayList<String> substrings = new ArrayList<String>();
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        String query = "SELECT SUFFIX, LCP FROM " + tableName + " ORDER BY LCP DESC LIMIT " + number + ";";
        ResultSet rs = statement.executeQuery( query );
        int maxIndex;
        int lcp;
        while ( rs.next() ) {
            maxIndex = rs.getInt("SUFFIX");
            lcp = rs.getInt("LCP");
            substrings.add(this.original.substring(maxIndex, maxIndex + lcp));
        }
        return substrings;
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
        return complete(query, 1, 0);
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

    public static void drop(String name) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        String sql = "DROP TABLE " + name;
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {}
        statement.close();
        c.close();
    }

    public void store(String name) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        String sql = "CREATE TABLE " + name +
                     " (ID INT PRIMARY KEY     NOT NULL," +
                     " SUFFIX          INT     NOT NULL," +
                     " LCP             INT     NOT NULL)";

        String sqlText = "CREATE TABLE " + name + "_TEXT" +
                " (ID INT PRIMARY KEY     NOT NULL," +
                " FULL            TEXT    NOT NULL)";
        try {
            statement.executeUpdate(sql);
            statement.executeUpdate(sqlText);
        } catch (SQLException e) {
            System.out.println(e);
        }

        for (int i = 0; i < this.suffixes.length; i++) {
            sql = "INSERT INTO " + name + " (ID, SUFFIX, LCP) " +
                    "VALUES (" + i + ", " + this.suffixes[i] + ", " + this.lcps[i] + ");";
            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        sql = "INSERT INTO " + name + "_TEXT (ID, FULL) " +
                "VALUES (1, '" + this.original.replaceAll("'", "''") + "');";
        statement.executeUpdate(sql);

        statement.close();
        c.close();
    }

    public static SuffixArray read(String name) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        ResultSet rs = statement.executeQuery( "SELECT SUFFIX, LCP FROM " + name + ";" );

        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Integer> lcps = new ArrayList<>();
        while ( rs.next() ) {
            indices.add(rs.getInt(1));
            lcps.add(rs.getInt(2));
        }

        Integer[] _lcps = new Integer[lcps.size()];
        lcps.toArray(_lcps);

        Integer[] _indices = new Integer[indices.size()];
        lcps.toArray(_indices);

        ResultSet rsText = statement.executeQuery( "SELECT \"FULL\" FROM " + name + "_TEXT;" );

        String original = "";
        while (rsText.next()) original = rsText.getString(1);
        SuffixArray s = new SuffixArray(original, _indices, _lcps);

        statement.close();
        c.close();
        return s;
    }

    public static SuffixArray buildFromFile (String fileName, String table) {
        StringBuilder fullText = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            for (String line : stream.collect(Collectors.toList())) {
                fullText.append(line + " ");
            }
        } catch (IOException e) {}
        SuffixArray sarray = new SuffixArray(new String(fullText));

        try {
            sarray.store(table);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        }
        return sarray;
    }

    public static SuffixArray buildFromDB (String name) throws IOException, SQLException, ClassNotFoundException {
        return read(name);
    }

    public static SuffixArray loremIpsum () {
        String fileName = "/Users/jmcghee/workspace/git/fundamentals/search/filesToSearch/LoremIpsum";
        String tableName = "LOREM";
        SuffixArray indexed;
        try {
            indexed = buildFromDB(tableName);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e);
            indexed = SuffixArray.buildFromFile(fileName, tableName);
        }
        return indexed;
    }

    public static SuffixArray taleOfTwoCities () {
        String fileName = "/Users/jmcghee/workspace/git/fundamentals/search/filesToSearch/TaleOfTwoCities";
        String tableName = "TWOCITIESSHORT";
        SuffixArray indexed;
        try {
            indexed = buildFromDB(tableName);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e);
            indexed = SuffixArray.buildFromFile(fileName, tableName);
        }
        return indexed;
    }

    // Going to have to improve sorting time if I want to use this...
    public static SuffixArray warAndPeace () {
        String fileName = "/Users/jmcghee/workspace/git/fundamentals/search/filesToSearch/warAndPeace";
        SuffixArray indexed;
        String tableName = "WARPEACE";
        try {
            drop(tableName);
            indexed = buildFromDB(tableName);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e);
            indexed = SuffixArray.buildFromFile(fileName, tableName);
        }
        return indexed;
    }

    public static void main (String[] args) {

        SuffixArray sarray = taleOfTwoCities();
        try {
            ArrayList<String> longestHundred = sarray.longestRepeatedSubstrings("TWOCITIESSHORT", 10000);
            longestHundred.forEach(System.out::println);
        } catch (SQLException|ClassNotFoundException e) {
            System.out.println(e);
        }

    }

    // 3-way string quicksort from Algorithms 4th
    static class SedgewickRadix {

        private static SuffixArray s;

        public static void sort(SuffixArray s) {
            SedgewickRadix.s = s;
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

}


