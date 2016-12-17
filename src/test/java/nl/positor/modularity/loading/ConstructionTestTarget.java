package nl.positor.modularity.loading;

/**
 * Created by Arien on 16-Dec-16.
 */
public class ConstructionTestTarget {
    private Object obj1;
    private Object obj2;
    private Object init1;
    private Object init2;
    private Object init3;
    private boolean zeroArgCalled = false;

    public ConstructionTestTarget() {
        zeroArgCalled = true;
    }

    public ConstructionTestTarget(Object obj1, Object obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    private ConstructionTestTarget(Object obj) {
    }

    public void initWith(Object init1, Object init2, Object init3) {
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
