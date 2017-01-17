package nl.positor.modularity.glue;

import nl.positor.modularity.glue.api.component.Component;
import nl.positor.modularity.glue.api.DependencyGraph;
import nl.positor.modularity.glue.api.DependencyGraphBuilder;
import nl.positor.modularity.glue.impl.DefaultDependencyGraphBuilder;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by Arien on 17-Dec-16.
 */
public class DependencyGraphBuilderTest {

    @Test
    public void singleComponentTest() {
        DependencyGraphBuilder application = createBuilder();
        application
                .withComponent()
                .named("single")
                .withImplementingClass(HashMap.class.getName())
                .createdByCallingNullaryConstructor()
                .startedByCalling("clear")
                .shutdownByCalling("clear");
        DependencyGraph graph = application.build();
        Component component = graph.getComponentByName("single");
        assertTrue(component.getInstance() == component.getInstance());
        assertTrue(component.getInstance() instanceof HashMap);
        assertTrue(component.getName().equals("single"));
        HashMap instance = (HashMap) component.getInstance();
        instance.put("a", "1");
        assertTrue(instance.size() == 1);
        graph.startAll();
        assertTrue(instance.size() == 0);
        instance.put("a", "1");
        assertTrue(instance.size() == 1);
        graph.stopAll();
        assertTrue(instance.size() == 0);
    }

    @Test
    public void testBuilder() {
        DependencyGraphBuilder application = createBuilder();
        application
                .withComponent()
                .named("messageServer")
                .withImplementingClass("nl.positor.modularity.glue.testcase.MessageServerExample")
                .createdByCallingNullaryConstructor()
                .thenCalling("init")
                .startedByCalling("start")
                .shutdownByCalling("stop");
        application
                .withComponent()
                .named("database")
                .withImplementingClass("nl.positor.modularity.glue.testcase.MessageDatabaseExample")
                .createdByCallingNullaryConstructor();
        application
                .withComponent()
                .named("myHandler")
                .withImplementingClass("nl.positor.modularity.glue.testcase.MyHandler")
                .createdByCallingConstructorWith(
                        application.dependencyNamed("messageServer"),
                        application.dependencyNamed("database"))
                .startedByCalling("start")
                .shutdownByCalling("stop");
        DependencyGraph app = application.build();
        app.startAll();
        app.stopAll();
    }

    private DependencyGraphBuilder createBuilder() {
        return new DefaultDependencyGraphBuilder();
    }

}