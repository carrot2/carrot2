

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


package com.dawidweiss.carrot.adapters.localfilter;


/**
 * Represents a single hit from a search engine.
 */
public interface Hit
{
    /**
     * Returns the snippet associated with the hit or <code>null</code> (optional).
     */
    public String getSnippet();


    /**
     * Returns the <code>URL</code> associated with the hit.
     *
     * @return An URL to the resource, represented as a string. <code>null</code> should
     *         <em>never</em> be returned from this method.
     */
    public String getURL();


    /**
     * Returns the title of the hit or <code>null</code> if it is not available.
     */
    public String getTitle();
}
