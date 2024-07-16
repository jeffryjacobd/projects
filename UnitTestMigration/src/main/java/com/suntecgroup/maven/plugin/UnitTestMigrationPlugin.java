package com.suntecgroup.maven.plugin;

import com.suntecgroup.utils.UnitTestMigration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.nio.file.Files;
import java.nio.file.Paths;

@Mojo(name = "migrate", requiresDirectInvocation = true, threadSafe = true,
        defaultPhase = LifecyclePhase.PROCESS_TEST_SOURCES)
public class UnitTestMigrationPlugin extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        mavenProject.getTestCompileSourceRoots()
                .stream()
                .filter(testPath -> Files.exists(Paths.get(testPath)))
                .forEach(testPath -> {
                    getLog().info("Migrating test files in path :" + testPath);
                    final UnitTestMigration processor = new UnitTestMigration();
                    final SpoonAPI api = new Launcher();
                    api.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(api.getEnvironment()));
                    api.addInputResource(testPath);
                    api.addProcessor(processor);
                    api.setSourceOutputDirectory(testPath);
                    api.run();
                });
    }
}
