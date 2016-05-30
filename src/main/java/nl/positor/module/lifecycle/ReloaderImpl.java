package nl.positor.module.lifecycle;

import com.google.common.collect.Iterables;
import nl.positor.util.DistinctParams;
import nl.positor.util.DistinctParams.DistinctParamsFunction;
import nl.positor.util.DistinctParams.DistinctParamsViolationException;

import java.util.LinkedHashSet;

/**
 * Created by Arien on 26-May-16.
 */
public class ReloaderImpl implements Reloader {
    @Override
    public void reload(Lifecycle lifecycleObject, Context context) throws CyclicDependencyException {
        // TODO: Reconsider if cycle detection belongs here
        DistinctParamsFunction<Lifecycle, Iterable<Lifecycle>> contextLookup = DistinctParams.requireDistinct(context::getDependencies);
        LinkedHashSet<Lifecycle> objectsToManage = new LinkedHashSet<>();
        objectsToManage.add(lifecycleObject);
        int index = 0;
        while (index++ < objectsToManage.size()) {
            Lifecycle object = Iterables.get(objectsToManage, index);
            try {
                Iterables.addAll(objectsToManage, contextLookup.apply(object));
            } catch (DistinctParamsViolationException e) {
                throw new CyclicDependencyException(e);
            }
        }
        while (--index < objectsToManage.size()) {
            Lifecycle object = Iterables.get(objectsToManage, index);
            object.stop();
            object.clean();
        }
        while (index++ < objectsToManage.size()) {
            Lifecycle object = Iterables.get(objectsToManage, index);
            object.start();
        }
    }
}
