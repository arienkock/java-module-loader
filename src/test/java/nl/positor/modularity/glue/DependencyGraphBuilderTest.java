package nl.positor.modularity.glue;

import nl.positor.modularity.glue.api.component.Component;
import nl.positor.modularity.glue.api.DependencyGraph;
import nl.positor.modularity.glue.api.DependencyGraphBuilder;
import nl.positor.modularity.glue.impl.DefaultDependencyGraphBuilder;
import nl.positor.modularity.glue.testcase.MessageDatabase;
import nl.positor.modularity.glue.testcase.MessageDatabaseExample;
import nl.positor.modularity.glue.testcase.MessageServer;
import nl.positor.modularity.glue.testcase.MessageServerExample;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Arien on 17-Dec-16.
 */
public class DependencyGraphBuilderTest {

    @Test
    public void testClassloader() throws MalformedURLException {
        String resourceName = MessageDatabase.class.getName().replace('.', '/') + ".class";
        String resourceUrl = MessageDatabase.class.getClassLoader().getResource(resourceName).toString();
        String resourcePathBase = resourceUrl.substring(0, resourceUrl.indexOf(resourceName));
        URL classPathUrl = new URL(resourcePathBase);
        System.out.println(classPathUrl);
        DependencyGraphBuilder application = createBuilder();
        application
                .withComponent()
                .named("db")
                .loadedFrom(classPathUrl)
                .withPublicApi(MessageDatabase.class.getName())
                .withImplementingClass(MessageDatabaseExample.class.getName())
                .createdByCallingNullaryConstructor();
        DependencyGraph dependencyGraph = application.build();
        dependencyGraph.startAll();
        Object db = dependencyGraph.getComponentByName("db").getInstance();
        assertFalse(MessageDatabase.class.isAssignableFrom(db.getClass()));
        assertFalse(MessageDatabaseExample.class.isAssignableFrom(db.getClass()));
    }

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
        MessageServerExample messageServer = (MessageServerExample) app.getComponentByName("messageServer").getInstance();
        messageServer.acceptMessage("Test!");
        assertTrue(messageServer.getCounter().get() == 1);
        MessageDatabaseExample database = (MessageDatabaseExample) app.getComponentByName("database").getInstance();
        assertTrue(database.getData().contains("Test!"));
        messageServer.acceptMessage("Test2!");
        assertTrue(messageServer.getCounter().get() == 2);
        assertTrue(database.getData().size() == 2);
        app.stopAll();
        try {
            messageServer.acceptMessage("Test3!");
            fail();
        } catch (Exception e) {
        }
    }

    private DependencyGraphBuilder createBuilder() {
        return new DefaultDependencyGraphBuilder();
    }

}