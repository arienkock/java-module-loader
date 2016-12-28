package nl.positor.modularity.glue;

import nl.positor.modularity.glue.api.DependencyGraph;
import nl.positor.modularity.glue.api.DependencyGraphBuilder;
import org.junit.Test;

/**
 * Created by Arien on 17-Dec-16.
 */
public class DependencyGraphBuilderTest {

    @Test
    public void testBuilder() {
        DependencyGraphBuilder application = createBuilder();
        application
                .withComponent()
                .named("messageServer")
                .withImplementingClass("nl.positor.modularity.glue.testcase.MessageServerExample")
                .createdByCallingNullaryConstructor()
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
    }

    private DependencyGraphBuilder createBuilder() {
        return null;
    }

}