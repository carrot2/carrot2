/*
 * ProfileEntry.java
 * 
 * Created on 2004-06-30
 */
package com.dawidweiss.carrot.core.local.profiling;

/**
 * Stores an object gathered during the profilling process.
 * 
 * @author stachoo
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