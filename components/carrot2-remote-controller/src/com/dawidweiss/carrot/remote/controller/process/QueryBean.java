

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


package com.dawidweiss.carrot.remote.controller.process;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;


class QueryBean
    implements com.dawidweiss.carrot.remote.controller.process.scripted.Query
{
    private final transient Query query;

    public QueryBean(Query query)
    {
        this.query = query;
    }

    public final String getQuery()
    {
        return query.getContent();
    }


    public final int getNumberOfExpectedResults()
    {
        return query.getRequestedResults();
    }
}
