package nl.positor.modularity.lifecycle.impl;

import nl.positor.modularity.lifecycle.api.DependencyLookup;
import nl.positor.modularity.lifecycle.api.ReverseDependencyLookup;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import nl.positor.modularity.lifecycle.api.Restarter;

import java.util.*;

/**
 * Created by Arien on 26-May-16.
 */
public class DefaultRestarter implements Restarter {
    /**
     * Expand reloading context
     * While any !stopped
     * stop all nodes with only stopped dependants
     * Clean all
     * While any !started
     * start all nodes with only started dependencies
     */
    @Override
    public void restart(ReverseDependencyLookup context, Lifecycle... objectsToReload) {
        ReloadingContext reloadingContext = new ReloadingContext(context, objectsToReload).invoke();
        LinkedList<Lifecycle> reloadingNodes = reloadingContext.getReloadingNodes();
        Map<Lifecycle, MemoLifecycle> stateMap = reloadingContext.getStateMap();
        Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap = reloadingContext.getReverseDependencyMap();
        Map<Lifecycle, Collection<Lifecycle>> dependencyMap = reloadingContext.getDependencyMap();
        // TODO: don't re-check stopped/started nodes each loop
        stopAll(reloadingNodes, stateMap, reverseDependencyMap);
        reloadingNodes.forEach(Lifecycle::clean);
        startAll(reloadingNodes, stateMap, dependencyMap);
    }

    @Override
    public void stop(ReverseDependencyLookup context, Lifecycle... objectsToStop) {
        LinkedList<Lifecycle> nodesToStop = new LinkedList<>();
        Map<Lifecycle, MemoLifecycle> stateMap = new HashMap<>();
        Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap = new HashMap<>();
        Arrays.stream(objectsToStop).forEach(o -> {
            nodesToStop.addFirst(o);
            stateMap.computeIfAbsent(o, MemoLifecycle::new);
        });
        for (int i = 0; i < nodesToStop.size(); i++) {
            Lifecycle current = nodesToStop.get(i);
            Collection<? extends Lifecycle> dependants = context.getDependants(current);
            if (dependants != null) {
                dependants.forEach(d -> {
                    reverseDependencyMap.computeIfAbsent(current, c -> new HashSet<>()).add(d);
                    if (!stateMap.containsKey(d)) {
                        stateMap.computeIfAbsent(d, MemoLifecycle::new);
                        nodesToStop.addLast(d);
                    }
                });
            }
        }
        stopAll(nodesToStop, stateMap, reverseDependencyMap);
    }

    @Override
    public void start(DependencyLookup context, Lifecycle... objectsToStart) {
        LinkedList<Lifecycle> nodesToStart = new LinkedList<>();
        Map<Lifecycle, MemoLifecycle> stateMap = new HashMap<>();
        Map<Lifecycle, Collection<Lifecycle>> dependencyMap = new HashMap<>();
        Arrays.stream(objectsToStart).forEach(o -> {
            nodesToStart.addFirst(o);
            stateMap.computeIfAbsent(o, MemoLifecycle::new);
        });
        for (int i = 0; i < nodesToStart.size(); i++) {
            Lifecycle current = nodesToStart.get(i);
            Collection<? extends Lifecycle> dependencies = context.getDependencies(current);
            if (dependencies != null) {
                dependencies.forEach(d -> {
                    dependencyMap.computeIfAbsent(current, c -> new HashSet<>()).add(d);
                    if (!stateMap.containsKey(d)) {
                        stateMap.computeIfAbsent(d, MemoLifecycle::new);
                        nodesToStart.addLast(d);
                    }
                });
            }
        }
        startAll(nodesToStart, stateMap, dependencyMap);
    }

    private void startAll(Collection<Lifecycle> nodesToStart, Map<Lifecycle, MemoLifecycle> stateMap, Map<Lifecycle, Collection<Lifecycle>> dependencyMap) {
        Collection<Lifecycle> fullListCopy = new ArrayList<>(nodesToStart);
        while (stateMap.values().stream().anyMatch(m -> !m.isStarted())) {
            Collection<Lifecycle> workList = new ArrayList<>(fullListCopy);
            fullListCopy.forEach(n -> {
                if (dependencyMap.getOrDefault(n, Collections.emptyList()).stream().map(stateMap::get).allMatch(u -> u.isStarted())) {
                    stateMap.get(n).startIfStopped();
                    workList.remove(n);
                }
            });
            fullListCopy = workList;
        }
    }

    private void stopAll(Collection<Lifecycle> nodesToStop, Map<Lifecycle, MemoLifecycle> stateMap, Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap) {
        Collection<Lifecycle> fullListCopy = new ArrayList<>(nodesToStop);
        while (stateMap.values().stream().anyMatch(m -> !m.isStopped())) {
            Collection<Lifecycle> workList = new ArrayList<>(fullListCopy);
            fullListCopy.forEach(n -> {
                if (reverseDependencyMap.getOrDefault(n, Collections.emptyList()).stream().map(stateMap::get).allMatch(u -> u.isStopped())) {
                    stateMap.get(n).stopIfStarted();
                    workList.remove(n);
                }
            });
            fullListCopy = workList;
        }
    }

    private static class MemoLifecycle {
        private Lifecycle delegate;
        private Boolean started;

        private MemoLifecycle(Lifecycle delegate) {
            this.delegate = delegate;
        }

        public boolean isStarted() {
            return started != null && started;
        }

        public boolean isStopped() {
            return started != null && !started;
        }

        public boolean startIfStopped() {
            if (!isStarted()) {
                boolean b = delegate.startIfStopped();
                started = true;
                return b;
            }
            return false;
        }

        public void clean() {
            delegate.clean();
        }

        public boolean stopIfStarted() {
            if (!isStopped()) {
                boolean b = delegate.stopIfStarted();
                started = false;
                return b;
            }
            return false;
        }
    }

    private class ReloadingContext {
        private ReverseDependencyLookup context;
        private Lifecycle[] objectsToReload;
        private LinkedList<Lifecycle> reloadingNodes;
        private Map<Lifecycle, MemoLifecycle> stateMap;
        private Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap;
        private Map<Lifecycle, Collection<Lifecycle>> dependencyMap;

        public ReloadingContext(ReverseDependencyLookup context, Lifecycle... objectsToReload) {
            this.context = context;
            this.objectsToReload = objectsToReload;
        }

        public LinkedList<Lifecycle> getReloadingNodes() {
            return reloadingNodes;
        }

        public Map<Lifecycle, MemoLifecycle> getStateMap() {
            return stateMap;
        }

        public Map<Lifecycle, Collection<Lifecycle>> getReverseDependencyMap() {
            return reverseDependencyMap;
        }

        public Map<Lifecycle, Collection<Lifecycle>> getDependencyMap() {
            return dependencyMap;
        }

        public ReloadingContext invoke() {
            reloadingNodes = new LinkedList<>();
            stateMap = new HashMap<>();
            reverseDependencyMap = new HashMap<>();
            dependencyMap = new HashMap<>();
            Arrays.stream(objectsToReload).forEach(o -> {
                reloadingNodes.addFirst(o);
                stateMap.computeIfAbsent(o, MemoLifecycle::new);
            });
            for (int i = 0; i < reloadingNodes.size(); i++) {
                Lifecycle current = reloadingNodes.get(i);
                Collection<? extends Lifecycle> dependants = context.getDependants(current);
                if (dependants != null) {
                    dependants.forEach(d -> {
                        reverseDependencyMap.computeIfAbsent(current, c -> new HashSet<>()).add(d);
                        dependencyMap.computeIfAbsent(d, c -> new HashSet<>()).add(current);
                        if (!stateMap.containsKey(d)) {
                            stateMap.computeIfAbsent(d, MemoLifecycle::new);
                            reloadingNodes.addLast(d);
                        }
                    });
                }
            }
            return this;
        }
    }
}
