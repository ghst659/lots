package tc.lots;

import java.util.*;

public class Selector {
    private List<Lot> order = new ArrayList<>();
    public Selector(Collection<Lot> lots) {
        order.clear();
        order.addAll(lots);
        Collections.sort(order, (Lot a, Lot b) -> {
           Integer aQ = a.quantity();
           Integer bQ = b.quantity();
           return aQ.compareTo(bQ);
        });
    }
    private void enumerate(int target, int lastHead, List<List<Lot>> result, LinkedList<Integer>stack) {
        for (int head = locate(target, lastHead-1); head >= 0; --head) {
            int remainder = target - order.get(head).quantity();
            stack.addLast(head);
            if (remainder == 0) {
                List<Lot> trail = new LinkedList<>();
                for (int index : stack) {
                    trail.add(order.get(index));
                }
                result.add(trail);
            } else if (remainder > 0) {
                enumerate(remainder, head, result, stack);
            }
            stack.removeLast();
        }
    }
    private Collection<Set<String>> lotListsToNameSets(List<List<Lot>> lotList) {
        List<Set<String>> result = new LinkedList<>();
        for (List<Lot> ll : lotList) {
            Set<String> trail = new TreeSet<>();
            for (Lot lot : ll) {
                trail.add(lot.name());
            }
            result.add(trail);
        }
        return result;
    }
    private int locate(int target, int hi) {
        if (hi < 0 || hi >= order.size() || target < order.get(0).quantity()) {
            return -1;
        }
        int lo = 0;
        for (int mid = (lo + hi) / 2; hi - lo > 1; mid = (lo + hi) / 2) {
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
        List<List<Lot>> lotList = new LinkedList<>();
        enumerate(targetSum, order.size(), lotList, new LinkedList<>());
        return lotListsToNameSets(lotList);
    }
}
