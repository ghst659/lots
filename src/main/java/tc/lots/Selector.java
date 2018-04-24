package tc.lots;

import java.util.*;

public class Selector {
    private Map<String, Lot> map = new HashMap<>();  // Prefer O(1) lookup
    private List<String> keys = new ArrayList<>(); // Prefer O(1) random access
    private List<Lot> order = new ArrayList<>();
    public Selector(Collection<Lot> lots) {
        fillKeyMap(lots, map, keys);
        order.clear();
        order.addAll(lots);
        Collections.sort(order, (Lot a, Lot b) -> {
           Integer aQ = a.quantity();
           Integer bQ = b.quantity();
           return aQ.compareTo(bQ);
        });
    }
    private void enumerate(int target, int lastHead, Collection<Set<String>> result, LinkedList<Integer>stack) {
        for (int head = locate(target, lastHead-1); head >= 0; --head) {
            int remainder = target - order.get(head).quantity();
            stack.addLast(head);
            if (remainder == 0) {
                Set<String> trail = new TreeSet<>();
                for (int index : stack) {
                    trail.add(order.get(index).name());
                }
                result.add(trail);
            } else if (remainder > 0) {
                enumerate(remainder, head, result, stack);
            }
            stack.removeLast();
        }
    }
    private int locate(int target, int hi) {
        if (hi < 0 || hi >= order.size() || target < order.get(0).quantity()) {
            return -1;
        }
        int lo = 0;
        for (int mid = avg(lo, hi); hi - lo > 1; mid = avg(lo, hi)) {
            int qty = order.get(mid).quantity();
            if (qty <= target) {
                lo = mid;
            } else if (qty > target) {
                hi = mid;
            }
        }
        return order.get(hi).quantity() > target ? lo : hi;
    }
    /**
     * Finds the sets of lot names that satisfy a target sum.
     * @param targetSum The target sum
     * @return A collection of lot name sets, each of which has lots that satisfy the sum.
     */
    public Collection<Set<String>> matchTarget(int targetSum) {
        Collection<Set<String>> result = new LinkedList<>();
        // collectTrails(keys, map, targetSum, keys.size(), result, new TreeSet<>());
        enumerate(targetSum, keys.size(), result, new LinkedList<>());
        return result;
    }

    private static void fillKeyMap(Collection<Lot> lots, Map<String, Lot> map, List<String> keys) {
        map.clear();
        keys.clear();
        for (Lot l: lots) {
            String key = l.name();
            assert ! map.containsKey(key) : String.format("Duplicate Lot name: %s", key);
            map.put(l.name(), l);
            keys.add(key);
        }
        Collections.sort(keys, (String a, String b) -> {
            Integer aQ = map.get(a).quantity();
            Integer bQ = map.get(b).quantity();
            return aQ.compareTo(bQ);
        });
    }

    private static void collectTrails(List<String> keys, Map<String, Lot> map, int target, int lastHead,
                                      Collection<Set<String>> result, Set<String> accumTrail) {
        for (int head = findIndex(keys, map, target, 0, lastHead - 1); head >= 0; --head) {
            int remainder = target - value(keys, map, head);
            Set<String> currentTrail = new TreeSet<>(accumTrail);
            currentTrail.add(keys.get(head));
            if (remainder == 0) {
                result.add(currentTrail);  // add this trail to the result.
            } else if (remainder > 0) {
                collectTrails(keys, map, remainder, head, result, currentTrail);
            }
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
    private static int findIndex(List<String> keys, Map<String, Lot> map, int target, int lo, int hi) {
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
}
