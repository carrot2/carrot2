package com.dawidweiss.carrot.ant;

import org.apache.tools.ant.BuildFileTest;

/**
 * Tests the corresponding ANT task.
 */
public class CopyDependenciesTest extends BuildFileTest {

    public CopyDependenciesTest(String s) {
        super(s);
    }

    public void setUp() {
        configureProject("ant-tests/copyDependencies.xml");
    }

    public void testBringToDate() {
        try {
            executeTarget("nocopytest");
        } finally {
            System.out.println(super.getLog());
        }
    }

}
