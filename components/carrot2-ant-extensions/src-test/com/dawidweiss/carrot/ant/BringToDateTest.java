package com.dawidweiss.carrot.ant;

import org.apache.tools.ant.BuildFileTest;

/**
 * Tests the corresponding ANT task.
 */
public class BringToDateTest extends BuildFileTest {

    public BringToDateTest(String s) {
        super(s);
    }

    public void setUp() {
        configureProject("ant-tests/bringToDate.xml");
    }

    public void testBringToDate() {
        try {
            executeTarget("test");
        } finally {
            System.out.println(super.getLog());
        }
    }

    public void testProfileDependency() {
        executeTarget("test2");
    }

    public void testCircularDependency() {
        super.expectBuildException("test3", "Circular dependency");
    }

}
