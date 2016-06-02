package nl.positor.module;

import com.google.common.collect.ImmutableSet;
import nl.positor.module.definition.ModuleClassPath;
import nl.positor.module.definition.ModuleDefinition;
import nl.positor.module.testcases.Job;
import nl.positor.module.testcases.JobCreator;
import nl.positor.module.testcases.JobCreatorImpl;
import nl.positor.module.testcases.JobRunner;
import nl.positor.module.testcases.JobRunnerImpl;
import nl.positor.module.testcases.JobSupervisor;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Arien on 24-May-16.
 */
public class TestClasspath {
    private static Path tempDirectory;

    private TestClasspath() {}

    public static void init() throws IOException {
        tempDirectory = Files.createTempDirectory("test_classpath");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(tempDirectory.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        copyClassFiles("JobSupervisor", JobSupervisor.class);
        copyClassFiles("JobCreator", JobCreator.class);
        copyClassFiles("JobCreator_private", JobCreatorImpl.class);
        copyClassFiles("JobRunner", JobRunner.class);
        copyClassFiles("JobRunner_private", JobRunnerImpl.class);
        copyClassFiles("Job", Job.class);
    }

    public static URL[] jobSupervisorClasspath() {
        return pathForId("JobSupervisor");
    }

    public static URL[] jobCreatorClasspath() {
        return pathForId("JobCreator");
    }

    public static URL[] jobCreatorPrivateClasspath() {
        return pathForId("JobCreator_private");
    }

    public static URL[] jobRunnerClasspath() {
        return pathForId("JobRunner");
    }

    public static URL[] jobRunnerPrivateClasspath() {
        return pathForId("JobRunner_private");
    }

    public static ModuleClassPath jobApiClasspath() {
        ImmutableSet<String> classnameSet = ImmutableSet.of(Job.class.getName());
        ModuleDefinition definition = new ModuleDefinition() {
            @Override
            public String getEntryPoint() {
                return null;
            }

            @Override
            public Set<String> getPublicClasses() {
                return classnameSet;
            }

            @Override
            public Iterable<ModuleDefinition> getDependencies() {
                return Collections.emptyList();
            }
        };
        return new ModuleClassPath() {
            @Override
            public ModuleDefinition getDefinition() {
                return definition;
            }

            @Override
            public Iterable<URL> getClassPath() {
                return Arrays.<URL>asList(pathForId("Job"));
            }
        }
    }

    private static URL[] pathForId(String id) {
        try {
            return new URL[] {tempDirectory.resolve(id).toFile().toURI().toURL()};
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }



    private static void copyClassFiles(String id, Class<?>... classes) throws IOException {
        Path subdir = tempDirectory.resolve(id);
        for (Class<?> clazz : classes) {
            String resourcePath = clazz.getName().replace('.', '/') + ".class";
            try (InputStream inputStream = TestClasspath.class.getResourceAsStream('/' + resourcePath)) {
                Files.createDirectories(subdir.resolve(clazz.getPackage().getName().replace('.', '/')));
                Path targetPath = subdir.resolve(resourcePath);
                Files.copy(inputStream, targetPath);
            }
        }
    }
}
