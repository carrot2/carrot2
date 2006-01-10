
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

package com.dawidweiss.carrot.core.local.profiling;

/**
 * Stores an object gathered during the profilling process.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ProfileEntry
{
    /** Name of this entry */
    private String name;

    /** Description of this entry */
    private String description;

    /** Data for this entry */
    private Object data;

    /**
     * Creates a new profile entry.
     * 
     * @param name the profiling object's name
     * @param description the profiling object's description
     * @param data the profiling object
     */
    public ProfileEntry(String name, String description, Object data)
    {
        this.name = name;
        this.description = description;
        this.data = data;
    }

    /**
     * Returns this ProfileEntry's <code>data</code>.
     * 
     * @return this ProfileEntry's <code>data</code>.
     */
    public Object getData()
    {
        return data;
    }

    /**
     * Returns this ProfileEntry's <code>description</code>.
     * 
     * @return this ProfileEntry's <code>description</code>.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns this ProfileEntry's <code>name</code>.
     * 
     * @return this ProfileEntry's <code>name</code>.
     */
    public String getName()
    {
        return name;
    }
}