package nl.positor.modularity.lifecycle.impl;

import nl.positor.modularity.lifecycle.api.Context;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import nl.positor.modularity.lifecycle.api.Reloader;

import java.util.LinkedList;

/**
 * Created by Arien on 26-May-16.
 */
public class DefaultReloader implements Reloader {
    @Override
    public void reload(Lifecycle objectToReload, Context context) {
        LinkedList<Lifecycle> objectsToManage = new LinkedList<>();
        objectsToManage.add(objectToReload);
        for (int index = 0; index < objectsToManage.size(); index++) {
            Lifecycle object = objectsToManage.get(index);
            Iterable<Lifecycle> dependants = context.getDependants(object);
            if (dependants != null) {
                for (Lifecycle lifecycle : dependants) {
                    if (!objectsToManage.contains(lifecycle)) {
                        objectsToManage.add(lifecycle);
                    }
                }
            }
        }
        for (int index = objectsToManage.size() - 1; index >= 0; index--) {
            Lifecycle object = objectsToManage.get(index);
            object.stop();
            object.clean();
        }
        for (int index = 0; index < objectsToManage.size(); index++) {
            Lifecycle object = objectsToManage.get(index);
            object.start();
        }
    }
}
