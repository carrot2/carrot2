
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


package com.stachoodev.carrot.filter.cluster.lsicluster;


import java.io.ObjectInputStream;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import Jama.Matrix;
import Jama.SingularValueDecomposition;


/**
 * Jama's underflow bug.
 * @author Dawid Weiss
 */
public class TestJamaSVDInfiniteLoopTest
    extends TestCase
{
 
    public TestJamaSVDInfiniteLoopTest(String arg0)
    {
        super(arg0);
    }


    public void testJamaUnderflow()
        throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
        Logger logger = Logger.getLogger("tests.performance");

        System.out.println(this.getClass().getClassLoader().getResource("jama-test/badmatrix"));

        ObjectInputStream is = new ObjectInputStream(
            this.getClass().getClassLoader().getResourceAsStream("jama-test/badmatrix"));
        
        Matrix matrix = (Matrix) is.readObject();

        is.close();

        new SingularValueDecomposition(matrix);
    }
}
