package nl.positor.modularity.glue.api;

import org.junit.Test;

/**
 * Created by Arien on 17-Dec-16.
 */
public class DependencyGraphBuilderTest {

    @Test
    public void testBuilder() {
        DependencyGraphBuilder appBuilder = createBuilder();
        Component datasource = appBuilder
                .withComponent()
                .named("datasource")
                .createdBy()
                    .nullaryConstructor()
                    .forClass("java.util.HashMap")
                    .thenCalling("put", appBuilder.constant("root"), appBuilder.constant("password"))
                    .finish()
                .build();
        appBuilder
                .withComponent()
                .named("userDao")
                .createdBy()
                    .callingConstructorWith(appBuilder.dependency(datasource))
                    .finish()
                .build();
        DependencyGraph app = appBuilder.build();
        app.startAll();
    }

    private DependencyGraphBuilder createBuilder() {
        return null;
    }

}