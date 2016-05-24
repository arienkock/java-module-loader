package nl.positor.module.loading;

import nl.positor.module.TestClasspath;
import nl.positor.module.testcases.Job;
import nl.positor.module.testcases.JobCreatorImpl;
import nl.positor.module.testcases.JobRunnerImpl;
import nl.positor.module.testcases.JobSupervisor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Arien on 24-May-16.
 */
@RunWith(Parameterized.class)
public class ModuleLoaderTest {
    private final ModuleLoader creatorLoader;
    private final ModuleLoader runnerLoader;
    private final ModuleLoader jobApiLoader;
    private final ModuleLoader supervisorLoader;

    @BeforeClass
    public static void init() throws IOException {
        TestClasspath.init();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>();
        list.add(
                new Object[] {
                        new SimpleModuleLoader(
                                JobCreatorImpl.class.getName(),
                                () -> new ClassLoadingPair(
                                        System.class::getClassLoader,
                                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobCreatorPrivateClasspath()),
                                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobCreatorClasspath()))),
                        new SimpleModuleLoader(
                                JobRunnerImpl.class.getName(),
                                () -> new ClassLoadingPair(
                                        System.class::getClassLoader,
                                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobRunnerPrivateClasspath()),
                                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobRunnerClasspath()))),
                        new SimpleModuleLoader(
                                Job.class.getName(),
                                () -> new ClassLoadingPair(
                                        System.class::getClassLoader,
                                        null,
                                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobApiClasspath()))),
                        new SimpleModuleLoader(
                                JobSupervisor.class.getName(),
                                () -> new ClassLoadingPair(
                                        System.class::getClassLoader,
                                        null,
                                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobSupervisorClasspath())))

                });
        return list;
    }

    public ModuleLoaderTest(ModuleLoader creatorLoader,
                            ModuleLoader runnerLoader,
                            ModuleLoader jobApiLoader,
                            ModuleLoader supervisorLoader) {
        this.creatorLoader = creatorLoader;
        this.runnerLoader = runnerLoader;
        this.jobApiLoader = jobApiLoader;
        this.supervisorLoader = supervisorLoader;
    }

    @Test
    public void testAssumedClasses() throws Exception {
        assertThat(creatorLoader.get().getClass().getName(), is(JobCreatorImpl.class.getName()));
        assertThat(creatorLoader.get().getClass(), is(not(JobCreatorImpl.class)));
        assertThat(runnerLoader.get().getClass().getName(), is(JobRunnerImpl.class.getName()));
        assertThat(runnerLoader.get().getClass(), is(not(JobRunnerImpl.class)));
    }

    @Test
    public void testBootstrapIsShared() {
    }
}
