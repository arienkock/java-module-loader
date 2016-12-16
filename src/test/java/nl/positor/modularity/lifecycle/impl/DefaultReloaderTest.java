package nl.positor.modularity.lifecycle.impl;

import nl.positor.modularity.lifecycle.api.Context;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultReloaderTest {
    @Test
    public void reload() throws Exception {
        DefaultReloader reloader = new DefaultReloader();
        Vector<Integer> startOrder = new Vector<>();
        Vector<Integer> stopOrder = new Vector<>();
        Vector<Integer> cleanOrder = new Vector<>();

        Lifecycle rootLevelIgnored1 = lifecycleAtLevel(0, startOrder, stopOrder, cleanOrder);
        Lifecycle root = lifecycleAtLevel(0, startOrder, stopOrder, cleanOrder);
        Lifecycle rootLevelIgnored2 = lifecycleAtLevel(0, startOrder, stopOrder, cleanOrder);

        Lifecycle level1A = lifecycleAtLevel(1, startOrder, stopOrder, cleanOrder);
        Lifecycle level1B = lifecycleAtLevel(1, startOrder, stopOrder, cleanOrder);

        Lifecycle level2AA = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);
        Lifecycle level2AB = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);
        Lifecycle level2BA = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);
        Lifecycle level2BB = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);

        Lifecycle level3 = lifecycleAtLevel(3, startOrder, stopOrder, cleanOrder);

        Map<Lifecycle, Iterable<Lifecycle>> reverseDependencyMap = new HashMap<>();
        reverseDependencyMap.put(rootLevelIgnored1, Arrays.asList(level1A));
        reverseDependencyMap.put(root, Arrays.asList(level1A, level1B));
        reverseDependencyMap.put(rootLevelIgnored2, Arrays.asList(level1B));

        reverseDependencyMap.put(level1A, Arrays.asList(level2AA, level2AB));
        reverseDependencyMap.put(level1B, Arrays.asList(level2BB, level2BA));

        reverseDependencyMap.put(level2AA, Arrays.asList(level3));
        reverseDependencyMap.put(level2AB, Arrays.asList(level3));
        reverseDependencyMap.put(level2BA, Arrays.asList(level3));
        reverseDependencyMap.put(level2BB, Arrays.asList(level3));

        Context context = reverseDependencyMap::get;
        reloader.reload(root, context);

        List<Integer> reverse = Arrays.asList(3, 2, 2, 2, 2, 1, 1, 0);
        ArrayList<Integer> expected = new ArrayList<>(reverse);
        Collections.reverse(expected);
        Assert.assertEquals(expected, startOrder);
        Assert.assertEquals(reverse, stopOrder);
        Assert.assertEquals(reverse, cleanOrder);
    }

    private Lifecycle lifecycleAtLevel(int level, Vector<Integer> startOrder, Vector<Integer> stopOrder, Vector<Integer> cleanOrder) {
        return new Lifecycle() {
            @Override
            public void start() {
                startOrder.add(level);
            }

            @Override
            public void clean() {
                cleanOrder.add(level);
            }

            @Override
            public void stop() {
                stopOrder.add(level);
            }
        };
    }

}