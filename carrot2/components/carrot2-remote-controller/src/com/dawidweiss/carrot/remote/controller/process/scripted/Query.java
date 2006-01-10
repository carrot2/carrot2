
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
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
