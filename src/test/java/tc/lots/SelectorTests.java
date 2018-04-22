package tc.lots;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class SelectorTests {
    @Parameterized.Parameters(name="{index}: {1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                5,
                new String[][]{
                    {"L4=5"},
                    {"L3=4", "L0=1"},
                    {"L2=3", "L1=2"}
                },
            },
            {
                new int[]{90, 10, 70, 50, 60, 50},
                100,
                new String[][]{
                    {"L0=90", "L1=10"},
                    {"L3=50", "L5=50"}
                },
            },
            {
                new int[]{900, 100, 700, 500, 600, 500, 200},
                1000,
                new String[][]{
                    {"L0=900", "L1=100"},
                    {"L3=500", "L5=500"},
                    {"L2=700", "L1=100", "L6=200"}
                },
            },
            {
                new int[]{2, 2, 1, 1},
                6,
                new String[][]{
                    {"L0=2", "L1=2", "L2=1", "L3=1"}
                },
            },
            {
                new int[]{5, 5},
                10,
                new String[][]{
                    {"L0=5", "L1=5"}
                }
            }
        });
    }
    private Collection<Lot> lots = new LinkedList<>();
    private int target = 0;
    private List<Set<String>> expected = new ArrayList<>();
    /**
     * Constructs the parameterised data.
     * @param lotCounts
     * @param testTarget
     * @param wantedLabels
     */
    public SelectorTests(int[] lotCounts, int testTarget, String[][] wantedLabels) {
        StringBuffer buf = new StringBuffer("Lots:");
        for (int i = 0; i < lotCounts.length; ++i) {
            String label = String.format("L%d=%d", i, lotCounts[i]);
            lots.add(new LotTestImpl(label, lotCounts[i]));
            buf.append(" ");
            buf.append(label);
        }
        System.err.println(buf.toString());
        target = testTarget;
        for (String[] tags : wantedLabels) {
            Set<String> wantedSet = new TreeSet<>();
            wantedSet.addAll(Arrays.asList(tags));
            expected.add(wantedSet);
        }
        Collections.sort(expected, lsComp);
    }

    // @Test
    public void testFindHead() {
       List<String> got = Selector.findRoots(lots, target);
       for (var key : got) {
           System.err.println(key);
       }
    }

    @Test
    public void testSelectorImpl() {
        List<Set<String>> got = new ArrayList<>(Selector.findMatches(lots, target));
        Assert.assertEquals(expected.size(), got.size());
        Collections.sort(got, lsComp);
        for (int i = 0; i < got.size(); ++i) {
            Assert.assertEquals(expected.get(i), got.get(i));
        }
    }
    /*
     ********************************************************************************
     */

    /**
     * Compares two sets of strings.
     */
    private static Comparator<Set<String>> lsComp = new Comparator<>() {
        @Override
        public int compare(Set<String> a, Set<String> b) {
            if (a.size() < b.size()) {
                return -1;
            } else if (a.size() > b.size()) {
                return 1;
            } else {
                List<String> aList = setList(a);
                List<String> bList = setList(b);
                for (int j = 0; j < a.size(); ++j) {
                    int c = aList.get(j).compareTo(bList.get(j));
                    if (c != 0) {
                        return c;
                    }
                }
                return 0;
            }
        }
    };

    private static List<String> setList(Set<String> s) {
        List<String> result = new ArrayList<>(s);
        Collections.sort(result);
        return result;
    }
}