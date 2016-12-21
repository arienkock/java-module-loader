package nl.positor.modularity.lifecycle;

import nl.positor.modularity.lifecycle.api.Context;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import nl.positor.modularity.lifecycle.impl.DefaultReloader;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultReloaderTest {
    @Test
    public void reloadMultiple() {
        DefaultReloader reloader = new DefaultReloader();
        Vector<Integer> startOrder = new Vector<>();
        Vector<Integer> stopOrder = new Vector<>();
        Vector<Integer> cleanOrder = new Vector<>();

        Lifecycle rootLevelIgnored1 = lifecycleAtLevel(0, startOrder, stopOrder, cleanOrder);
        Lifecycle root1 = lifecycleAtLevel(0, startOrder, stopOrder, cleanOrder);
        Lifecycle root2 = lifecycleAtLevel(0, startOrder, stopOrder, cleanOrder);

        Lifecycle level1A = lifecycleAtLevel(1, startOrder, stopOrder, cleanOrder);
        Lifecycle level1B = lifecycleAtLevel(1, startOrder, stopOrder, cleanOrder);

        Lifecycle level2AA = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);
        Lifecycle level2AB = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);
        Lifecycle level2BA = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);
        Lifecycle level2BB = lifecycleAtLevel(2, startOrder, stopOrder, cleanOrder);

        Lifecycle level3 = lifecycleAtLevel(3, startOrder, stopOrder, cleanOrder);

        Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap = new HashMap<>();
        reverseDependencyMap.put(rootLevelIgnored1, Arrays.asList(level1A));
        reverseDependencyMap.put(root1, Arrays.asList(level1A, level1B));
        reverseDependencyMap.put(root2, Arrays.asList(level1B));

        reverseDependencyMap.put(level1A, Arrays.asList(level2AA, level2AB));
        reverseDependencyMap.put(level1B, Arrays.asList(level2BB, level2BA));

        reverseDependencyMap.put(level2AA, Arrays.asList(level3));
        reverseDependencyMap.put(level2AB, Arrays.asList(level3));
        reverseDependencyMap.put(level2BA, Arrays.asList(level3));
        reverseDependencyMap.put(level2BB, Arrays.asList(level3));

        Context context = d -> reverseDependencyMap.getOrDefault(d, Collections.emptyList());
        reloader.reload(context, root1, root2);

        List<Integer> reverse = Arrays.asList(3, 2, 2, 2, 2, 1, 1, 0, 0);
        ArrayList<Integer> expected = new ArrayList<>(reverse);
        Collections.reverse(expected);
        Assert.assertEquals(expected, startOrder);
        Assert.assertEquals(reverse, stopOrder);
    }

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

        Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap = new HashMap<>();
        reverseDependencyMap.put(rootLevelIgnored1, Arrays.asList(level1A));
        reverseDependencyMap.put(root, Arrays.asList(level1A, level1B));
        reverseDependencyMap.put(rootLevelIgnored2, Arrays.asList(level1B));

        reverseDependencyMap.put(level1A, Arrays.asList(level2AA, level2AB));
        reverseDependencyMap.put(level1B, Arrays.asList(level2BB, level2BA));

        reverseDependencyMap.put(level2AA, Arrays.asList(level3));
        reverseDependencyMap.put(level2AB, Arrays.asList(level3));
        reverseDependencyMap.put(level2BA, Arrays.asList(level3));
        reverseDependencyMap.put(level2BB, Arrays.asList(level3));

        Context context = d -> reverseDependencyMap.getOrDefault(d, Collections.emptyList());
        reloader.reload(context, root);

        List<Integer> reverse = Arrays.asList(3, 2, 2, 2, 2, 1, 1, 0);
        ArrayList<Integer> expected = new ArrayList<>(reverse);
        Collections.reverse(expected);
        Assert.assertEquals(expected, startOrder);
        Assert.assertEquals(reverse, stopOrder);
    }

    private Lifecycle lifecycleAtLevel(int level, Vector<Integer> startOrder, Vector<Integer> stopOrder, Vector<Integer> cleanOrder) {
        return new Lifecycle() {
            boolean started = true;
            @Override
            public boolean startIfStopped() {
                if (!started) {
                    startOrder.add(level);
                    started = true;
                    return true;
                }
                return false;
            }

            @Override
            public void clean() {
                if (!started) {
                    cleanOrder.add(level);
                }
            }

            @Override
            public boolean stopIfStarted() {
                if (started) {
                    stopOrder.add(level);
                    started = false;
                    return true;
                }
                return false;
            }
        };
    }

}