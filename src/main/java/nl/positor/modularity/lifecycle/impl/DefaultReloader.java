package nl.positor.modularity.lifecycle.impl;

import nl.positor.modularity.lifecycle.api.Context;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import nl.positor.modularity.lifecycle.api.Reloader;

import java.util.*;

/**
 * Created by Arien on 26-May-16.
 */
public class DefaultReloader implements Reloader {
    @Override
    public void reload(Context context, Lifecycle... objectsToReload) {
        /**
         * Expand reloading context
         * While any !stopped
         *   stop all nodes with only stopped dependants
         * Clean all
         * While any !started
         *   start all nodes with only started dependencies
         */
        LinkedList<Lifecycle> reloadingNodes = new LinkedList<>();
        Map<Lifecycle, MemoLifecycle> stateMap = new HashMap<>();
        Map<Lifecycle, Collection<Lifecycle>> reverseDependencyMap = new HashMap<>();
        Map<Lifecycle, Collection<Lifecycle>> dependencyMap = new HashMap<>();
        Arrays.stream(objectsToReload).forEach(o -> {reloadingNodes.addFirst(o); stateMap.computeIfAbsent(o, MemoLifecycle::new);});
        for (int i = 0; i < reloadingNodes.size(); i++) {
            Lifecycle current = reloadingNodes.get(i);
            context.getDependants(current).forEach(d -> {
                reverseDependencyMap.computeIfAbsent(current, c -> new HashSet<>()).add(d);
                dependencyMap.computeIfAbsent(d, c -> new HashSet<>()).add(current);
                if (!stateMap.containsKey(d)) {
                    stateMap.computeIfAbsent(d, MemoLifecycle::new);
                    reloadingNodes.addLast(d);
                }
            });
        }
        while (stateMap.values().stream().anyMatch(m -> !m.isStopped())) {
            reloadingNodes.forEach(n -> {
                if (reverseDependencyMap.getOrDefault(n, Collections.emptyList()).stream().map(stateMap::get).allMatch(u -> u.isStopped())) {
                    stateMap.get(n).stopIfStarted();
                }
            });
        }
        reloadingNodes.forEach(Lifecycle::clean);
        while (stateMap.values().stream().anyMatch(m -> !m.isStarted())) {
            reloadingNodes.forEach(n -> {
                if (dependencyMap.getOrDefault(n, Collections.emptyList()).stream().map(stateMap::get).allMatch(u -> u.isStarted())) {
                    stateMap.get(n).startIfStopped();
                }
            });
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
            boolean b = delegate.startIfStopped();
            started = true;
            return b;
        }

        public void clean() {
            delegate.clean();
        }

        public boolean stopIfStarted() {
            boolean b = delegate.stopIfStarted();
            started = false;
            return b;
        }
    }

}
