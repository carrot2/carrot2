
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.ant;

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

    public void testDependenciesWithPath() {
        try {
            executeTarget("test3");
        }
        finally {
            System.out.println( super.getOutput());
            System.err.println( super.getError());
        }
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
