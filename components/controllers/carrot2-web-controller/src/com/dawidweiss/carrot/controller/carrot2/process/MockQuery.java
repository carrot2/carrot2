

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


package com.dawidweiss.carrot.controller.carrot2.process;


import com.dawidweiss.carrot.controller.carrot2.process.scripted.*;


/**
 * Mock object for the scripted query bean.
 */
public class MockQuery
    implements Query
{
    public String getQuery()
    {
        return "Mock object query";
    }


    public int getNumberOfExceptedResults()
    {
        return 100;
    }
}
