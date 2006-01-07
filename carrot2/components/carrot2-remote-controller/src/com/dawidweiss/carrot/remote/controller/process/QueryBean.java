
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


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query;


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
