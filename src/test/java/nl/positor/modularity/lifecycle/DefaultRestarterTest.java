package nl.positor.modularity.lifecycle;

import nl.positor.modularity.lifecycle.api.ReverseDependencyLookup;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import nl.positor.modularity.lifecycle.impl.DefaultRestarter;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static java.lang.Math.floor;
import static java.lang.Math.random;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultRestarterTest {
    @Test
    public void testStart() {
        Map<StrictLifecycle, Collection<StrictLifecycle>> dependencyMap = new HashMap<>();
        dependsOn(dependencyMap, Collections.singleton(new StrictLifecycle()), Arrays.asList(new StrictLifecycle(), new StrictLifecycle()), Collections.singleton(new StrictLifecycle()), Arrays.asList(new StrictLifecycle(), new StrictLifecycle()));
        DefaultRestarter restarter = new DefaultRestarter();
        Set<Lifecycle> allObjects = new HashSet<>();
        dependencyMap.entrySet().forEach(e -> {
            allObjects.addAll(e.getValue());
            allObjects.add(e.getKey());
        });
        Lifecycle[] allLifecycleObjects = allObjects.toArray(new Lifecycle[]{});
        restarter.start(dependencyMap::get, allLifecycleObjects);
        restarter.stop(dependencyMap::get, allLifecycleObjects);
        restarter.start(dependencyMap::get, allLifecycleObjects);
        restarter.restart(dependencyMap::get, allLifecycleObjects);
        for (int i = 0; i < 50000; i++) {
            List<Lifecycle> restartSet = new ArrayList<>(allObjects);
            Collections.shuffle(restartSet);
            List<Lifecycle> subList = restartSet.subList(0, 1 + (int) floor(random() * (restartSet.size() - 1)));
            restarter.restart(dependencyMap::get, subList.toArray(new Lifecycle[]{}));
        }
    }

    private void dependsOn(Map<StrictLifecycle, Collection<StrictLifecycle>> dependencyMap, Collection<StrictLifecycle>... levels) {
        for (int i = 0; i < levels.length - 1; i++) {
            for (StrictLifecycle current : levels[i]) {
                dependencyMap.put(current, levels[i + 1]);
            }
        }
//        for (StrictLifecycle lifecycle : levels[levels.length - 1]) {
//            dependencyMap.put(lifecycle, Collections.emptyList());
//        }
    }

    @Test
    public void reloadMultiple() {
        DefaultRestarter reloader = new DefaultRestarter();
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

        ReverseDependencyLookup context = d -> reverseDependencyMap.getOrDefault(d, Collections.emptyList());
        reloader.restart(context, root1, root2);

        List<Integer> reverse = Arrays.asList(3, 2, 2, 2, 2, 1, 1, 0, 0);
        ArrayList<Integer> expected = new ArrayList<>(reverse);
        Collections.reverse(expected);
        Assert.assertEquals(expected, startOrder);
        Assert.assertEquals(reverse, stopOrder);
    }

    @Test
    public void reload() throws Exception {
        DefaultRestarter reloader = new DefaultRestarter();
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

        ReverseDependencyLookup context = d -> reverseDependencyMap.getOrDefault(d, Collections.emptyList());
        reloader.restart(context, root);

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