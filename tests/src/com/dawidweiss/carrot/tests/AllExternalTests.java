

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.tests;


import com.dawidweiss.carrot.tests.httpunit.ControllerStartupSuccessTest;
import com.dawidweiss.carrot.tests.httpunit.DemoLinksTest;
import com.dawidweiss.carrot.tests.httpunit.regression.RandomCachedQueryTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Performs all external test cases in the required order. Can be run from command line as well as
 * from JUnit runner.
 */
public class AllExternalTests
    extends TestSuite
{
    /**
     * Creates the test suite and adds test cases to it.
     */
    public AllExternalTests()
    {
        super("All external test cases.");
        super.addTestSuite(ControllerStartupSuccessTest.class);
        super.addTestSuite(DemoLinksTest.class);
        super.addTestSuite(RandomCachedQueryTest.class);
    }

    /**
     * Required by junit to run the suite
     */
    public static Test suite()
    {
        return new AllExternalTests();
    }


    /**
     * Helpful if tests are to be run from command line. The tests are invoked using junit's
     * command line runner.
     */
    public static void main(String [] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}
