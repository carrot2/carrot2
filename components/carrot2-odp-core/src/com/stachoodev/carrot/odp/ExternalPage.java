
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