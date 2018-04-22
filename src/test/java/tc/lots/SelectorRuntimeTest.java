package tc.lots;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class SelectorRuntimeTest {
    @Parameterized.Parameters(name="{index}:")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {
                10, 5, 2,
            },
            {
                20, 5, 3,
            },
            {
                30, 5, 3,
            },
            {
                50, 5, 3,
            },
        });
    }
    private List<Lot> lots = new ArrayList<>();
    private List<Integer> targetRuns = new LinkedList<>();
    private Selector selector = null;
    public SelectorRuntimeTest(int numLots, int numTargets, int numSum) {
        Random qGen = new Random();
        for (int i = 0; i < numLots; ++i) {
            int lotQuantity = qGen.nextInt(numLots);
            lots.add(new LotTestImpl(String.format("L%d=%d", i, lotQuantity), lotQuantity));
        }
        System.err.format("LotCount: %d\n", lots.size());
        for (int k = 0; k < numTargets; ++k) {
            int target = 0;
            for (int j = 0; j < numSum; ++j) {
                int i = qGen.nextInt(numLots);
                target += lots.get(i).quantity();
            }
            System.err.format("\ttarget: %d\n", target);
            targetRuns.add(target);
        }
        selector = new Selector(lots);
    }
    @Test
    public void testExecutions() {
        long t0 = System.currentTimeMillis();
        for (int target : targetRuns) {
            var unusedResult = selector.matchTarget(target);
            System.err.format("Target: %d; result: %d\n", target, unusedResult.size());
        }
        long t1 = System.currentTimeMillis();
        long dt = t1 - t0;
        System.err.format("LotCount: %d; numTargets: %d;  dt: %d\n", lots.size(), targetRuns.size(), dt);
    }
}
