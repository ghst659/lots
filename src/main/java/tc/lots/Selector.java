package tc.lots;

import java.util.*;

public class Selector {
    /**
     * Finds the sets of lot names that satisfy a target sum.
     * @param lots A collection of Lots
     * @param targetSum The target sum
     * @return A collection of lot name sets, each of which has lots that satisfy the sum.
     */
    public static Collection<Set<String>> findMatches(Collection<Lot> lots, int targetSum) {
        Collection<Set<String>> result = new LinkedList<>();
        Map<String, Lot> map = new HashMap<>();
        List<String> keys = new ArrayList<>();
        fillKeyMap(lots, map, keys);
        // ===========================================
        // find all the matching sets
        Set<String> trailHead = new TreeSet<>();
        collectTrails(keys, map, targetSum, keys.size(), result, trailHead);
        return result;
    }

    public static List<String> findRoots(Collection<Lot> lots, int targetSum) {
        List<String> result = new LinkedList<>();
        Map<String, Lot> map = new HashMap<>();
        List<String> keys = new ArrayList<>();
        fillKeyMap(lots, map, keys);
        // ===========================================
        int head = findIndex(keys, map, targetSum, 0, keys.size()-1);
        while (head > 0) {
            result.add(keys.get(head));
            head = findIndex(keys, map, targetSum, 0, head-1);
        }
        return result;
    }

    private static void fillKeyMap(Collection<Lot> lots, Map<String, Lot> map, List<String> keys) {
        map.clear();
        keys.clear();
        for (Lot l: lots) {
            String key = l.name();
            map.put(l.name(), l);
            keys.add(key);
        }
        Collections.sort(keys, (String a, String b) -> {
            Integer aQ = map.get(a).quantity();
            Integer bQ = map.get(b).quantity();
            return aQ.compareTo(bQ);
        });
    }

    public static void collectTrails(List<String> keys, Map<String, Lot> map, int target, int lastHead,
                                     Collection<Set<String>> result, Set<String> accumTrail) {
        int head = findIndex(keys, map, target, 0, lastHead - 1);
        while (head >= 0) {
            Set<String> currentTrail = new TreeSet<>(accumTrail);
            currentTrail.add(keys.get(head));
            int nodeValue = value(keys, map, head);
            int remainder = target - nodeValue;
            if (nodeValue == target) {
                result.add(currentTrail);  // add this trail to the result.
            } else if (remainder > 0) {
                collectTrails(keys, map, remainder, head, result, currentTrail);
            }
            head = findIndex(keys, map, target, 0, head - 1);
        }
    }

    /**
     * Finds the highest index into the key list whose lot quantity is less than or equal to the target.
     * @param keys A list of key strings.
     * @param map A map from key string to a Lot.
     * @param target The target value to locate in the key list.
     * @param lo Lower boundary for the search.
     * @param hi Upper boundary for the search.
     * @return Returns the matching index or -1 if not found.
     */
    public static int findIndex(List<String> keys, Map<String, Lot> map, int target, int lo, int hi) {
        if (hi < lo || lo < 0 || hi >= keys.size() || target < value(keys, map, lo)) {
            return -1;
        }
        for (int mid = avg(lo, hi); hi - lo > 1; mid = avg(lo, hi)) {
            int qty = value(keys, map, mid);
            if (qty <= target) {
                lo = mid;
            } else if (qty > target) {
                hi = mid;
            }
        }
        return value(keys, map, hi) > target ? lo : hi;
    }

    private static int avg(int x, int y) {
        return (x + y) / 2;
    }

    private static int value(List<String> keys, Map<String, Lot> map, int index) {
        return map.get(keys.get(index)).quantity();
    }

    private static String tstr(Set<String> t) {
        return "<" + String.join(",", t) + ">";
    }
}
