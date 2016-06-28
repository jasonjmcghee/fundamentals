import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by jmcghee on 6/28/16.
 */
public class SuffixArray {

    private ArrayList<String> suffixes;
    private int length;
    private int[] lcps;

    public SuffixArray (String original) {
        this.length  = original.length();
        this.suffixes = new ArrayList<>();
        for (int i = 0; i < this.length; i++) {
            this.suffixes.add(original.substring(i));
        }
        Collections.sort(this.suffixes);
        this.lcps = new int[this.length];
        for (int i = 1; i < this.length; i++) {
            this.lcps[i] = this.lcp(i, i - 1);
        }
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
        int[] lcps = new int[this.length];
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
        return this.suffixes.get(i);
    }

    public String select(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= this.length) throw new IndexOutOfBoundsException();
        return this.suffixes.get(i).substring(this.length - i);
    }

    public String select(int i, int j) throws IndexOutOfBoundsException {
        if (i < 0 || i >= this.length || j < 0 || j >= this.length) throw new IndexOutOfBoundsException();
        return this.suffixes.get(i).substring(0, j);
    }

    private int lcp (int i, int j) {
        int length = 0;
        for (int k = 0; k < this.suffixes.get(i).length() && k < this.suffixes.get(j).length(); k++) {
            if (this.suffixes.get(i).charAt(k) != this.suffixes.get(j).charAt(k)) return length;
            length++;
        }
        return length;
    }

    // Longest Common Prefix
    public int longestCommonPrefix (int i) {
        return this.lcps[i];
    }

    public int[] getLcps() {
        return this.lcps;
    }

    public int rank (String query) {
        int low = 0;
        int high = this.length - 1;
        int mid;
        int cmp;
        while (low <= high) {
            mid = (low + high) / 2;
            cmp = query.compareTo(this.suffixes.get(mid));
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

    public String longestRepeatedSubstring () {
        String lrs = "";
        int lrsLength = 0;
        for (int i = 1; i < this.length(); i++) {
            int length = this.longestCommonPrefix(i);
            if (length > lrsLength) {
                lrs = this.select(i, length);
                lrsLength = lrs.length();
            }
        }
        return lrs;
    }

    public void insert(String other) {
        ArrayList<String> otherSuffixes = SuffixArray.buildSuffixes(other);
        int rank;
        for (String otherSuffix : otherSuffixes) {
            rank = rank(otherSuffix);
            if (rank == this.suffixes.size() || !this.suffixes.get(rank).equals(otherSuffix)) {
                this.suffixes.add(rank, otherSuffix);
            }
        }
        Collections.sort(this.suffixes);
        this.length = this.suffixes.size();
        this.rebuildLcps();
    }

    public String complete(String query) {
        if (query.length() > 0) {
            try {
                int rank = rank(query);
                String match = this.select(rank, query.length());
                if (query.equals(match)) {
                    return this.suffixes.get(rank);
                }
            } catch (IndexOutOfBoundsException e) {
                return query;
            }
        }
        return query;
    }
}


