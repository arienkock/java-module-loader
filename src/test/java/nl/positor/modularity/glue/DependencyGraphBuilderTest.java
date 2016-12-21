package nl.positor.modularity.glue;

import nl.positor.modularity.glue.api.ComponentBuilder;
import nl.positor.modularity.glue.api.DependencyGraph;
import nl.positor.modularity.glue.api.DependencyGraphBuilder;
import org.junit.Test;

/**
 * Created by Arien on 17-Dec-16.
 */
public class DependencyGraphBuilderTest {

    @Test
    public void testBuilder() {
        DependencyGraphBuilder appBuilder = createBuilder();
        ComponentBuilder datasource = appBuilder
                .withComponent()
                .named("datasource")
                .withImplementingClass("java.util.HashMap")
                .createdByCallingNullaryConstructor()
                .thenCalling("put", appBuilder.constant("root"), appBuilder.constant("password"));
        appBuilder
                .withComponent()
                .named("userDao")
                .createdByCallingConstructorWith(appBuilder.dependency(datasource));
        DependencyGraph app = appBuilder.build();
        app.startAll();
    }

    private DependencyGraphBuilder createBuilder() {
        return null;
    }

}