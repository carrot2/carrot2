package com.dawidweiss.carrot.ant;

import org.apache.tools.ant.BuildFileTest;

/**
 * Test against bugzilla bug #977423
 */
public class MissingDependenciesTest extends BuildFileTest {

    public MissingDependenciesTest(String s) {
        super(s);
    }

    public void setUp() {
        configureProject("ant-tests/missingDepsTest.xml");
    }

    public void testMissingDependencies() {
        try {
            executeTarget("test");
        } finally {
            System.out.println(super.getLog());
        }
    }

}
