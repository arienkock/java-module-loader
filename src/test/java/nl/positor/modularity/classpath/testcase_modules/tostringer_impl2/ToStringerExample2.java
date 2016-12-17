package nl.positor.modularity.classpath.testcase_modules.tostringer_impl2;

import nl.positor.modularity.classpath.testcase_modules.tostringer_api.ToStringer;

/**
 * Created by Arien on 16-Dec-16.
 */
public class ToStringerExample2 implements ToStringer {
    @Override
    public String convertToString() {
        return "This was another example.";
    }
}
