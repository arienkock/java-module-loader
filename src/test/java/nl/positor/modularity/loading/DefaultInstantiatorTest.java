package nl.positor.modularity.loading;

import nl.positor.modularity.loading.impl.DefaultInstantiatorBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultInstantiatorTest {
    @Test
    public void createZeroArg() throws Exception {
        ConstructionTestTarget instance = (ConstructionTestTarget) DefaultInstantiatorBuilder.newForClass(ConstructionTestTarget.class.getName())
                .constructWith()
                .build()
                .create(ConstructionTestTarget.class.getClassLoader());
        assertTrue(instance.isZeroArgCalled());
    }

    @Test
    public void createTwoArg() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object init1 = new Object();
        Object init2 = new Object();
        Object init3 = new Object();
        ConstructionTestTarget instance = (ConstructionTestTarget) DefaultInstantiatorBuilder.newForClass(ConstructionTestTarget.class.getName())
                .constructWith(new DummyHolder(obj1), new DummyHolder(obj2))
                .callMethodWith("initWith", new DummyHolder(init1), new DummyHolder(init2), new DummyHolder(init3))
                .build()
                .create(ConstructionTestTarget.class.getClassLoader());
        assertTrue(instance.getObj1() == obj1);
        assertTrue(instance.getObj2() == obj2);
        assertTrue(instance.getInit1() == init1);
        assertTrue(instance.getInit2() == init2);
        assertTrue(instance.getInit3() == init3);
    }

}