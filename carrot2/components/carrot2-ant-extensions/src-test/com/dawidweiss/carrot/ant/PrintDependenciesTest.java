
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
