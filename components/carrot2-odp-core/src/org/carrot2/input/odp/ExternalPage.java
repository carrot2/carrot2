
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp;

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
     */
    public abstract String getDescription();

    /**
     * Returns this ExternalPage's <code>title</code>.
     * 
     */
    public abstract String getTitle();
    
    /**
     * Returns this ExternalPage's <code>url</code>.
     * 
     */
    public abstract String getUrl();
}