/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp;

/**
 * Represents an ODP's external page reference.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface ExternalPage
{
    /**
     * Returns this ExternalPage's <code>description</code>.
     * 
     * @return
     */
    public abstract String getDescription();

    /**
     * Returns this ExternalPage's <code>title</code>.
     * 
     * @return
     */
    public abstract String getTitle();
    
    /**
     * Returns this ExternalPage's <code>url</code>.
     * 
     * @return
     */
    public abstract String getUrl();
}