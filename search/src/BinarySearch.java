/**
 * Created by jmcghee on 6/10/16.
 */

public class BinarySearch {

    public static int find (int x, int[] sorted) {
        if (sorted.length < 1) return -1;

        int low = 0;
        int high = sorted.length - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;
            if (sorted[mid] == x) return mid;
            if (sorted[mid] < x) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return -1;
    }
}
