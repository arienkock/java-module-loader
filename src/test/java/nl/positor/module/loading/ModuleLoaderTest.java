package nl.positor.module.loading;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import nl.positor.module.TestClasspath;
import nl.positor.module.definition.ModuleClassPath;
import nl.positor.module.definition.ModuleDefinition;
import nl.positor.module.testcases.Job;
import nl.positor.module.testcases.JobCreator;
import nl.positor.module.testcases.JobCreatorImpl;
import nl.positor.module.testcases.JobRunner;
import nl.positor.module.testcases.JobRunnerImpl;
import nl.positor.module.testcases.JobSupervisor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.of;
import static nl.positor.module.loading.ModuleHolder.holderFor;
import static nl.positor.module.loading.ModuleLoaderWrapper.loaderFrom;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Arien on 24-May-16.
 */
@RunWith(Parameterized.class)
public class ModuleLoaderTest {
    private final ModuleLoaderOnly creatorLoader;
    private final ModuleLoaderOnly runnerLoader;
    private final ModuleLoaderOnly jobApiLoader;
    private final ModuleLoaderOnly supervisorLoader;
    private final ModuleHolder creatorHolder;
    private final ModuleHolder runnerHolder;
    private final ModuleHolder jobApiHolder;
    private final ModuleHolder supervisorHolder;


    @BeforeClass
    public static void init() throws IOException {
        TestClasspath.init();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        ArrayList<Object[]> list = new ArrayList<>();
//        SimpleModuleLoader jobApiModule = new SimpleModuleLoader(
//                null,
//                () -> new ClassLoadingPair(
//                        System.class::getClassLoader,
//                        null,
//                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobApiClasspath())), null);
//        SimpleModuleLoader creatorModule = new SimpleModuleLoader(
//                JobCreatorImpl.class.getName(),
//                () -> new ClassLoadingPair(
//                        SimpleModuleLoader.joinPublic(Arrays.asList(jobApiModule)),
//                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobCreatorPrivateClasspath()),
//                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobCreatorClasspath())),
//                Arrays.asList(jobApiModule));
//        SimpleModuleLoader runnerModule = new SimpleModuleLoader(
//                JobRunnerImpl.class.getName(),
//                () -> new ClassLoadingPair(
//                        SimpleModuleLoader.joinPublic(Arrays.asList(jobApiModule)),
//                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobRunnerPrivateClasspath()),
//                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobRunnerClasspath())),
//                Arrays.asList(jobApiModule));
//        SimpleModuleLoader supervisorModule = new SimpleModuleLoader(
//                JobSupervisor.class.getName(),
//                () -> new ClassLoadingPair(
//                        SimpleModuleLoader.joinPublic(Arrays.asList(runnerModule, creatorModule)),
//                        null,
//                        ClassLoadingPair.urlClassloaderProvider(TestClasspath.jobSupervisorClasspath())),
//                Arrays.asList(runnerModule, creatorModule));
//        jobApiModule.addReloadListener(creatorModule);
//        jobApiModule.addReloadListener(runnerModule);
//        creatorModule.addReloadListener(supervisorModule);
//        runnerModule.addReloadListener(supervisorModule);
        Map<Set<String>, ModuleClassReader> classLoadingMap = Maps.newHashMap();
        Function<ModuleClassPath, Object> function = classPath -> {
            ModuleClassReader classReader = classLoadingMap.computeIfAbsent(ImmutableSet.<String>copyOf(classPath.getDefinition().getPublicClasses()), key -> {
                return ClassReader.classReaderFrom(classPath, () -> {
                    return new AggregateClassLoader(
                            ImmutableSet.copyOf(classPath.getDefinition().getDependencies()).stream()
                                .map(ModuleDefinition::getPublicClasses)
                                .map(classLoadingMap::get)
                                .map(ModuleClassReader::getPublicClassLoader)
                                .collect(Collectors.toList()));
                });
            });
            String entryPoint = classPath.getDefinition().getEntryPoint();
            return entryPoint == null ? null : classReader.loadPrivate(entryPoint);
        };
        list.add(
                new Object[] { function });
        return list;
    }

    public ModuleLoaderTest(Function<ModuleClassPath, Object> loadingFunction) {
        ModuleDefinition jobDef = new ModuleDefinition() {
            @Override
            public String getEntryPoint() {
                return null;
            }

            @Override
            public Set<String> getPublicClasses() {
                return of(Job.class.getName());
            }

            @Override
            public Iterable<ModuleDefinition> getDependencies() {
                return null;
            }
        };

        ModuleDefinition creatorDef = new ModuleDefinition() {
            @Override
            public String getEntryPoint() {
                return JobCreatorImpl.class.getName();
            }

            @Override
            public Set<String> getPublicClasses() {
                return of(JobCreator.class.getName());
            }

            @Override
            public Iterable<ModuleDefinition> getDependencies() {
                return of(jobDef);
            }
        };

        ModuleDefinition runnerDef = new ModuleDefinition() {
            @Override
            public String getEntryPoint() {
                return JobRunnerImpl.class.getName();
            }

            @Override
            public Set<String> getPublicClasses() {
                return of(JobRunner.class.getName());
            }

            @Override
            public Iterable<ModuleDefinition> getDependencies() {
                return of(jobDef);
            }
        };

        ModuleDefinition supervisorDef = new ModuleDefinition() {
            @Override
            public String getEntryPoint() {
                return JobSupervisor.class.getName();
            }

            @Override
            public Set<String> getPublicClasses() {
                return of(JobSupervisor.class.getName());
            }

            @Override
            public Iterable<ModuleDefinition> getDependencies() {
                return of(runnerDef, creatorDef);
            }
        };

        ModuleClassPath jobClasspath = new ModuleClassPath() {
            @Override
            public ModuleDefinition getDefinition() {
                return jobDef;
            }

            @Override
            public Iterable<URL> getClassPath() {
                return ImmutableSet.<URL>builder()
                        .addAll(Arrays.<URL>asList(TestClasspath.jobApiClasspath()))
                        .build();
            }
        };

        ModuleClassPath creatorClasspath = new ModuleClassPath() {
            @Override
            public ModuleDefinition getDefinition() {
                return creatorDef;
            }

            @Override
            public Iterable<URL> getClassPath() {
                return ImmutableSet.<URL>builder()
                        .addAll(Arrays.<URL>asList(TestClasspath.jobCreatorClasspath()))
                        .addAll(Arrays.<URL>asList(TestClasspath.jobCreatorPrivateClasspath()))
                        .build();
            }
        };

        ModuleClassPath runnerClasspath = new ModuleClassPath() {
            @Override
            public ModuleDefinition getDefinition() {
                return runnerDef;
            }

            @Override
            public Iterable<URL> getClassPath() {
                return ImmutableSet.<URL>builder()
                        .addAll(Arrays.<URL>asList(TestClasspath.jobRunnerClasspath()))
                        .addAll(Arrays.<URL>asList(TestClasspath.jobRunnerPrivateClasspath()))
                        .build();
            }
        };

        ModuleClassPath supervisorClasspath = new ModuleClassPath() {
            @Override
            public ModuleDefinition getDefinition() {
                return supervisorDef;
            }

            @Override
            public Iterable<URL> getClassPath() {
                return ImmutableSet.<URL>builder()
                        .addAll(Arrays.<URL>asList(TestClasspath.jobSupervisorClasspath()))
                        .build();
            }
        };

        jobApiLoader = loaderFrom(jobDef, loadingFunction.compose(definition -> jobClasspath));
        creatorLoader = loaderFrom(creatorDef, loadingFunction.compose(definition -> creatorClasspath));
        runnerLoader = loaderFrom(runnerDef, loadingFunction.compose(definition -> runnerClasspath));
        supervisorLoader = loaderFrom(supervisorDef, loadingFunction.compose(definition -> supervisorClasspath));
        jobApiHolder = holderFor("job-api", jobApiLoader);
        creatorHolder = holderFor("job-creator", creatorLoader);
        runnerHolder = holderFor("job-runner", runnerLoader);
        supervisorHolder = holderFor("job-supervisor", supervisorLoader);
    }

    @Test
    public void testAssumedClasses() throws Exception {
        assertThat(creatorHolder.get().getClass().getName(), is(JobCreatorImpl.class.getName()));
        assertThat(creatorHolder.get().getClass(), is(not(JobCreatorImpl.class)));
        assertThat(runnerHolder.get().getClass().getName(), is(JobRunnerImpl.class.getName()));
        assertThat(runnerHolder.get().getClass(), is(not(JobRunnerImpl.class)));
        assertThat(jobApiHolder.get(), is(nullValue()));
        assertThat(supervisorHolder.get().getClass().getName(), is(JobSupervisor.class.getName()));
        assertThat(supervisorHolder.get().getClass(), is(not(JobSupervisor.class)));
    }

    @Test
    public void testJobApiIsShared() throws Exception {
        Object jobCreator = creatorHolder.get();
        Object job = call(jobCreator, "create");
        Object jobRunner = runnerHolder.get();
        call(jobRunner, "run", job);
        // our classloader isn't shared
        // using a Job instance loaderFrom a differen CL should fail
        try {
            call(jobRunner, "run", new Job() {
                @Override
                public Integer run() {
                    return null;
                }
            });
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testSupervisor() throws Exception {
        Object supervisor = supervisorHolder.get();
        call(supervisor, "setJobCreator", creatorHolder.get());
        call(supervisor, "setJobRunner", runnerHolder.get());
        call(supervisor, "runSomeJobs", 10);
    }

    @Test
    public void testReload() throws Exception {
        Object creator = creatorHolder.get();
        Object supervisor = supervisorHolder.get();
        jobApiHolder.reset();
        assertThat(supervisorHolder.get(), is(not(supervisor)));
        assertThat(creatorHolder.get(), is(not(creator)));
        testSupervisor();
    }

    private static Object call(Object target, String name, Object... params) throws InvocationTargetException, IllegalAccessException {
        return Arrays.<Method>asList(target.getClass().getDeclaredMethods())
                .stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .get()
                .invoke(target, params);
    }
}
