package nl.positor.modularity.loading;

import java.util.Map;

/**
 * Created by Arien on 16-Dec-16.
 */
public class ConstructionTestTarget {
    private Object obj1;
    private Boolean obj2;
    private Number init1;
    private Object init2;
    private Map init3;
    private boolean zeroArgCalled = false;

    public ConstructionTestTarget() {
        zeroArgCalled = true;
    }

    public ConstructionTestTarget(Object obj1, Boolean obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    private ConstructionTestTarget(Object obj) {
    }

    public void initWith(Number init1, Object init2, Map init3) {
        this.init1 = init1;
        this.init2 = init2;
        this.init3 = init3;
    }

    public Object getObj1() {
        return obj1;
    }

    public Object getObj2() {
        return obj2;
    }

    public Object getInit1() {
        return init1;
    }

    public Object getInit2() {
        return init2;
    }

    public Object getInit3() {
        return init3;
    }

    public boolean isZeroArgCalled() {
        return zeroArgCalled;
    }
}
