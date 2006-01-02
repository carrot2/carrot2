
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
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
