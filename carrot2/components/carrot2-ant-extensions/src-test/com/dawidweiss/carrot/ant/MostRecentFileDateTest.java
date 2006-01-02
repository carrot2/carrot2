
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;

/**
 * Tests the corresponding ANT task.
 */
public class MostRecentFileDateTest extends BuildFileTest {

    public MostRecentFileDateTest(String s) {
        super(s);
    }

    public void setUp() {
        configureProject("ant-tests/mostRecentFileDate.xml");
    }
    
    public void testMostRecentFileDateTask() {
        executeTarget("test");
    }

    public void testNoFilesInTheFileset() {
        try {
            executeTarget("test2");
        } catch (BuildException e) {
            if (e.toString().indexOf("No files included in") > 0) {
                // ok, expected.
            } else {
                fail();
            }
        }
    }


}
