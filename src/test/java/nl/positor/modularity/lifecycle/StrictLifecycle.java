package nl.positor.modularity.lifecycle;

import nl.positor.modularity.lifecycle.api.Lifecycle;

import static org.junit.Assert.fail;

/**
 * Created by Arien on 28-Dec-16.
 */
public class StrictLifecycle implements Lifecycle {
    boolean started;

    public StrictLifecycle() {
        started = false;
    }

    @Override
    public boolean startIfStopped() {
        if (started) {
            fail();
        }
        started = true;
        return true;
    }

    @Override
    public void clean() {
        if (started) {
            fail();
        }
    }

    @Override
    public boolean stopIfStarted() {
        if (!started) {
            fail();
        }
        started = false;
        return true;
    }
}
