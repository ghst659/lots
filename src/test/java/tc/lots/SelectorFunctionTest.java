package tc.lots;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class SelectorFunctionTest {
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
                new int[]{900, 100, 700, 500, 50, 600, 500, 200, 33, 25},
                1000,
                new String[][]{
                    {"L0=900", "L1=100"},
                    {"L3=500", "L6=500"},
                    {"L2=700", "L1=100", "L7=200"}
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
            },
            {
                new int[]{2, 2, 1, 1},
                3,
                new String[][]{
                    {"L0=2", "L2=1"},
                    {"L0=2", "L3=1"},
                    {"L1=2", "L2=1"},
                    {"L1=2", "L3=1"},
                },
            },
        });
    }
    private Collection<Lot> lots = new LinkedList<>();
    private int target = 0;
    private List<Set<String>> expected = new ArrayList<>();
    private Selector selector = null;
    /**
     * Constructs the parameterised data.
     * @param lotCounts List of lot quantities n[k]; lot names will be Lk=n
     * @param testTarget Integer target sum for lots to sum up to.
     * @param wantedLabels Expected sets of Lx=n string labels, each set summing to target.
     */
    public SelectorFunctionTest(int[] lotCounts, int testTarget, String[][] wantedLabels) {
        StringBuffer buf = new StringBuffer(String.format("Target: %d; Lots:", testTarget));
        for (int i = 0; i < lotCounts.length; ++i) {
            String label = String.format("L%d=%d", i, lotCounts[i]);
            lots.add(new LotTestImpl(label, lotCounts[i]));
            buf.append(" ");
            buf.append(label);
        }
        System.err.println(buf.toString());
        selector = new Selector(lots);
        target = testTarget;
        for (String[] tags : wantedLabels) {
            Set<String> wantedSet = new TreeSet<>();
            wantedSet.addAll(Arrays.asList(tags));
            expected.add(wantedSet);
        }
        Collections.sort(expected, listOfSetsCompare);
    }

    @Test
    public void testSelectorImpl() {
        List<Set<String>> got = new ArrayList<>(selector.matchTarget(target));
        Assert.assertEquals(expected.size(), got.size());
        Collections.sort(got, listOfSetsCompare);
        for (int i = 0; i < got.size(); ++i) {
            Assert.assertEquals(expected.get(i), got.get(i));
            System.err.format("\t%s v %s\n",expected.get(i), got.get(i));
        }
    }

    private static Comparator<Set<String>> listOfSetsCompare = new Comparator<>() {
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
