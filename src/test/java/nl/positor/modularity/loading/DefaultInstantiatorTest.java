package nl.positor.modularity.loading;

import nl.positor.modularity.loading.impl.DefaultInstantiatorBuilder;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        Boolean obj2 = Boolean.FALSE;
        Integer init1 = new Integer(1);
        Object init2 = new Object();
        HashMap init3 = new HashMap();
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

    @Test
    public void typeMatchingOnConstructor() {
        try {
            Boolean obj1 = Boolean.FALSE;
            Object obj2 = new Object();
            DefaultInstantiatorBuilder.newForClass(ConstructionTestTarget.class.getName())
                    .constructWith(new DummyHolder(obj1), new DummyHolder(obj2))
                    .build()
                    .create(ConstructionTestTarget.class.getClassLoader());
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void typeMatchingOnMethod() {
        try {
            HashMap init1 = new HashMap();
            Integer init2 = new Integer(1);
            Object init3 = new Object();
            DefaultInstantiatorBuilder.newForClass(ConstructionTestTarget.class.getName())
                    .constructWith()
                    .callMethodWith("initWith", new DummyHolder(init1), new DummyHolder(init2), new DummyHolder(init3))
                    .build()
                    .create(ConstructionTestTarget.class.getClassLoader());
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

}