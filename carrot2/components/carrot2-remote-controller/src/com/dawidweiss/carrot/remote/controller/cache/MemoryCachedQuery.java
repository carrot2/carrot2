

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


package com.dawidweiss.carrot.remote.controller.cache;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;


/**
 * A query cached in memory (volatile)
 */
public final class MemoryCachedQuery
    extends CachedQuery
{
    private final Query q;
    private final String componentId;
    private final Map params;
    private final byte [] data;

    /**
     * Creates a new cached query in memory. <b>Do not modify</b> objects used for creating this
     * cached query (they are not duplicated).
     */
    public MemoryCachedQuery(Query q, String componentId, Map optionalParams, byte [] data)
    {
        this.q = q;
        this.componentId = componentId;
        this.params = optionalParams;
        this.data = data;
    }

    public Query getQuery()
    {
        return q;
    }


    public String getComponentId()
    {
        return componentId;
    }


    public Map getOptionalParams()
    {
        return params;
    }


    public InputStream getData()
    {
        return new ByteArrayInputStream(data);
    }


    public int getDataSize()
    {
        if (data == null)
        {
            return 0;
        }
        else
        {
            return this.data.length;
        }
    }
}
