

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


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import com.dawidweiss.carrot.util.net.URLEncoding;


/**
 * An abstract cached query has only methods for generating signatures.
 */
public abstract class CachedQuery
{
    private Object signature;

    public abstract Query getQuery();


    public abstract String getComponentId();


    public abstract Map getOptionalParams();


    public abstract InputStream getData()
        throws IOException;


    public final Object getSignature()
    {
        if (signature == null)
        {
            synchronized (this)
            {
                if (signature == null)
                {
                    this.signature = generateSignature(
                            this.getQuery(), this.getComponentId(), this.getOptionalParams()
                        );
                }
            }
        }

        return signature;
    }


    public final boolean equals(Object o)
    {
        if (o instanceof CachedQuery)
        {
            CachedQuery co = (CachedQuery) o;

            if (this.getSignature().equals(co.getSignature()))
            {
                return true;
            }
        }

        return false;
    }


    public final int hashCode()
    {
        return this.getSignature().hashCode();
    }


    public static Object generateSignature(Query query, String componentId, Map optionalParams)
    {
        StringBuffer buffer = new StringBuffer();
        final String FIELD_SEPARATOR = "#";

        try
        {
            buffer.append(URLEncoding.encode(query.getContent(), "UTF-8"));
            buffer.append(FIELD_SEPARATOR);

            if (query.hasRequestedResults())
            {
                buffer.append(Integer.toString(query.getRequestedResults(), 10));
            }

            buffer.append(FIELD_SEPARATOR);
            buffer.append(componentId);
            buffer.append(FIELD_SEPARATOR);

            if (optionalParams != null)
            {
                TreeSet ts = new TreeSet(optionalParams.keySet());

                for (Iterator i = ts.iterator(); i.hasNext();)
                {
                    Object key = i.next();
                    buffer.append(URLEncoding.encode((String) key, "UTF-8"));
                    buffer.append(FIELD_SEPARATOR);

                    Object value = optionalParams.get(key);

                    if (value != null)
                    {
                        buffer.append(URLEncoding.encode((String) value, "UTF-8"));
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(
                "UTF-8 encoding must be supported for generating signatures."
            );
        }

        return buffer.toString();
    }
}
