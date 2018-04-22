package tc.lots;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.logging.Logger;

@RunWith(Parameterized.class)
public class SelectorRuntimeTest {
    @Parameterized.Parameters(name="Case {index}: {0} {1} {2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {
                5, 100, 2,
            },
            {
                10, 100, 3,
            },
            {
                20, 50, 3,
            },
            {
                30, 25, 2,
            },
            {
                50, 25, 2,
            },
        });
    }
    private Logger log = Logger.getLogger(this.getClass().getName());
    private List<Lot> lots = new ArrayList<>();
    private Map<String, Lot> map = new HashMap<>();
    private List<Integer> targetRuns = new LinkedList<>();
    private Selector selector = null;
    public SelectorRuntimeTest(int numLots, int numTargets, int numSum) {
        Random qGen = new Random();
        for (int i = 0; i < numLots; ++i) {
            int lotQuantity = qGen.nextInt(numLots);
            String lotName = String.format("L%d=%d", i, lotQuantity);
            Lot theLot = new LotTestImpl(lotName, lotQuantity);
            lots.add(theLot);
            map.put(lotName, theLot);
        }
        for (int k = 0; k < numTargets; ++k) {
            int target = 0;
            for (int j = 0; j < numSum; ++j) {
                int i = qGen.nextInt(numLots);
                target += lots.get(i).quantity();
            }
            targetRuns.add(target);
        }
        selector = new Selector(lots);
    }

    @Test
    public void testExecutions() {
        double numTargets = (double) targetRuns.size();
        long t0 = System.currentTimeMillis();
        for (int target : targetRuns) {
            var matches = selector.matchTarget(target);
            // log.info(String.format("\tTarget: %d; Number of combinations: %d", target, matches.size()));
            for (var trail : matches) {
                int sum = 0;
                for (var lotName : trail) {
                    sum += map.get(lotName).quantity();
                }
                Assert.assertEquals(target, sum);
            }
        }
        long t1 = System.currentTimeMillis();
        Long dt = t1 - t0;
        double ndt = dt.doubleValue() / numTargets;
        log.info(String.format("LotCount: %d; numTargets: %g;  dt: %g", lots.size(), numTargets, ndt));
    }
}
