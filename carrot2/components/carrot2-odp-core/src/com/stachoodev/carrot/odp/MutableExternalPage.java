/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp;

import java.io.*;

/**
 * Represents a single ODP external page.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class MutableExternalPage implements ExternalPage, Serializable
{
    /** This ExtenralPage's title */
    private String title;

    /** This MutableExternalPage's description */
    private String description;

    /**
     * Creates a new empty MutableExternalPage.
     */
    public MutableExternalPage()
    {
    }

    /**
     * @param title
     * @param description
     */
    public MutableExternalPage(String title, String description)
    {
        this.title = title;
        this.description = description;
    }
    
    /**
     * Returns this MutableExternalPage's <code>description</code>.
     * 
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets this MutableExternalPage's <code>description</code>.
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns this MutableExternalPage's <code>title</code>.
     * 
     * @return
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets this MutableExternalPage's <code>title</code>.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (obj.getClass() != getClass())
        {
            return false;
        }

        return title.equals(((MutableExternalPage) obj).title)
            && description.equals(((MutableExternalPage) obj).description);
    }
}