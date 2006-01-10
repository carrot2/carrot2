
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

import java.io.*;
import java.util.*;

/**
 * Represents a single ODP topic.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class MutableTopic implements Topic, Serializable
{
    /** This topic's id attribute */
    private String id;

    /** This topic's catid attribute */
    private int catid;

    /** A list of this page's external pages */
    private List externalPages;

    /**
     * Crates a new MutableTopic.
     * 
     * @param id
     * @param catid
     */
    public MutableTopic(String id)
    {
        this.id = id;
        externalPages = new ArrayList();
    }

    /**
     * Returns this MutableTopic's <code>catid</code>.
     * 
     * @return
     */
    public int getCatid()
    {
        return catid;
    }

    /**
     * Sets this MutableTopic's <code>catid</code>.
     * 
     * @param catid
     */
    public void setCatid(int catid)
    {
        this.catid = catid;
    }

    /**
     * Returns this MutableTopic's <code>id</code>.
     * 
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns a list of this MutableTopic's external pages.
     * 
     * @return
     */
    public List getExternalPages()
    {
        return externalPages;
    }

    /**
     * Adds an external page to this topic.
     * 
     * @param externalPage
     */
    public void addExternalPage(ExternalPage externalPage)
    {
        externalPages.add(externalPage);
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

        return id.equals(((MutableTopic) obj).id)
            && externalPages.equals(((MutableTopic) obj).externalPages);
    }
}