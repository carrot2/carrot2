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

    public void testProjectReferencesOneProjectWithTwoProfiles() {
        try {
            executeTarget("proj_reference_two_profiles");
        } finally {
            System.out.println(super.getLog());
        }
    }
    
    public void testCopyTest() {
        try {
            executeTarget("copytest");
        } finally {
            System.out.println(super.getLog());
        }
    }    
    
    public void testNoCopyTest() {
        try {
            executeTarget("nocopytest");
        } finally {
            System.out.println(super.getLog());
        }
    }
}
