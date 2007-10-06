
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
