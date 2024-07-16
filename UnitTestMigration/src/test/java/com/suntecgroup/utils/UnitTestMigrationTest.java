package com.suntecgroup.utils;

import org.testng.TestListenerAdapter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtClass;
import spoon.support.sniper.SniperJavaPrettyPrinter;


@Listeners(TestListenerAdapter.class)
public class UnitTestMigrationTest {

    @Test
    public void testSpoon() {
        UnitTestMigration processor = new UnitTestMigration();
        SpoonAPI api = new Launcher();
        api.getEnvironment().setPrettyPrinterCreator(()->new SniperJavaPrettyPrinter(api.getEnvironment()));
        api.addInputResource("src/test/resources/src/main/java");
        api.addProcessor(processor);
        api.setSourceOutputDirectory("src/test/resources/src/main/java1");
        api.run();
    }

}