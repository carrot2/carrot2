package com.dawidweiss.carrot.ant;

import org.apache.tools.ant.BuildFileTest;

/**
 * Tests the corresponding ANT task.
 */
public class PrintDependenciesTest extends BuildFileTest {

    public PrintDependenciesTest(String s) {
        super(s);
    }

    public void setUp() {
        configureProject("ant-tests/printDependencies.xml");
    }

    public void testPrintDependencies() {
        try {
            executeTarget("test");
        } finally {
            System.out.println(super.getLog());
        }
    }

}
