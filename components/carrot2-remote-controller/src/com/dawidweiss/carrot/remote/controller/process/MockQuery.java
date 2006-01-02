
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
package com.dawidweiss.carrot.remote.controller.process;


import com.dawidweiss.carrot.remote.controller.process.scripted.*;


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


    public int getNumberOfExpectedResults()
    {
        return 100;
    }
}
