package com.dawidweiss.carrot.ant;

import org.apache.tools.ant.BuildFileTest;

/**
 * Tests the corresponding ANT task.
 */
public class DependencyPathTest extends BuildFileTest {

    public DependencyPathTest(String s) {
        super(s);
    }

    public void setUp() {
        configureProject("ant-tests/dependencyPath.xml");
    }

    public void testDynamicPathUse() {
            executeTarget("test");
    }

    public void testDynamicPathAndProfilesUse() {
        try {
            executeTarget("test2");
        }
        finally {
            System.out.println( super.getOutput());
            System.err.println( super.getError());
        }
    }

}
