package nl.positor.module;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Workspace {
	private static Path workspacePath;
	private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private static Logger logger = Logger.getLogger(Workspace.class.getName());

	public static void clean() throws IOException {
		deleteFiles();
	}

	private static void deleteFiles() throws IOException {
		Files.walkFileTree(workspacePath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

		});
	}

	public static Path workspacePath() throws IOException {
		if (workspacePath == null) {
			workspacePath = Files.createTempDirectory("nl.positor.module.workspace");
			logger.info(workspacePath.toString());
		}
		return workspacePath;
	}

	public static Path compileModule(String className) throws IOException {
		return compileModule(className, null);
	}
	
	public static Path compileModule(String className, String implementationQualifier) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String base = className.replaceAll("\\.", "/");
		if (implementationQualifier != null) {
			base = implementationQualifier + "/" + base;
		}
		Path outDir = workspacePath().resolve(className.replaceAll("\\.", "_"));
		Files.createDirectories(outDir);
		Path apiOutputPath = outDir.resolve("api");
		Files.createDirectories(apiOutputPath);
		Path implOutputPath = outDir.resolve("impl");
		Files.createDirectories(implOutputPath);
		URL apiResource = classLoader.getResource(base + ".java");
		URL implResource = classLoader.getResource(base + "Impl.java");
		compile(apiOutputPath, apiResource.getPath().toString());
		compile(implOutputPath, implResource.getPath().toString());
		return outDir;
	}

	private static void compile(Path outputDirPath, String filePath) {
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> javaFileObjectsFromStrings2 = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(filePath));
		CompilationTask task2 = compiler.getTask(null, fileManager, null, Arrays.asList("-d", outputDirPath.toString()), null, javaFileObjectsFromStrings2);
		task2.call();
	}

	public static Path modulePath(String string) throws IOException {
		return workspacePath().resolve(string.replaceAll("\\.", "_"));
	}

}
