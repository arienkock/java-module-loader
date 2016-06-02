package nl.positor.module.loading;

import nl.positor.module.TestClasspath;
import nl.positor.module.testcases.Job;
import org.junit.Test;

import static nl.positor.module.TestClasspath.jobApiClasspath;

/**
 * Created by Arien on 30-May-16.
 */
public class LoadingTest {
    @Test
    public void testLoadAnInstance() {
        Loader.load(jobApiClasspath());
    }
}
