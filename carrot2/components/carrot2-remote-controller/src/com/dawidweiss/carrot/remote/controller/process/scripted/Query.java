

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller.process.scripted;


/**
 * A Query object is passed to a scriptable process and can be used to retrieve user query and
 * number of requested results.
 */
public interface Query
{
    public String getQuery();


    public int getNumberOfExpectedResults();
}
