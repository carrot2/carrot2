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
package com.dawidweiss.carrot.core.local.profiling;

import java.util.*;

/**
 * Stores profiling and debuging information gathered by a single component
 * during processing of a single request. An instance of this class will be
 * available from an instance of
 * {@link com.dawidweiss.carrot.core.local.profiling.ProfiledRequestContext}
 * returned when querying a
 * {@link com.dawidweiss.carrot.core.local.profiling.ProfiledLocalController}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class Profile
{
    /** Stores the time elapsed */
    private long totalTimeElapsed;

    /** System time recorded during the last startTimer() call */
    private long lastStartTime;

    /** Component name */
    private String componentName;

    /** Profile entries */
    private Map entries;

    /**
     * Creates a new profile for a component with given name.
     * 
     * @param componentName name of the component associated with this profile
     */
    public Profile(String componentName)
    {
        this.componentName = componentName;
        this.totalTimeElapsed = 0;
        this.lastStartTime = -1;
        this.entries = new LinkedHashMap();
    }

    /**
     * Returns the name of the LocalComponent with which this Profile is
     * associated or <code>null</code> if the name is not available.
     * 
     * @return this profile's local component's name or <code>null</code>
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * Starts measuring time spent by the component on processing the query.
     * Make sure you stop the timer before calling methods on components next in
     * the processing chain. If you don't you're likely to measure the
     * processing time of the whole chain.
     */
    public void startTimer()
    {
        if (lastStartTime != -1)
        {
            throw new IllegalStateException("Timer has not been stopped.");
        }

        lastStartTime = System.currentTimeMillis();
    }

    /**
     * Stops measuring time spent by the component on processing the query.
     * 
     * @return time elapsed since the <b>last </b> call to {@link #startTimer()}.
     *         To get the total elapsed time for this profile use
     *         {@link #getTotalTimeElapsed()}.
     */
    public long stopTimer()
    {
        // Don't delay stopping
        long stop = System.currentTimeMillis();

        if (lastStartTime == -1)
        {
            throw new IllegalStateException("Timer has not been started.");
        }

        long elapsed = stop - lastStartTime;
        totalTimeElapsed += elapsed;
        lastStartTime = -1;

        return elapsed;
    }

    /**
     * Returns total time spent by the component on processing the query (in
     * miliseconds).
     * 
     * @return total time spent by the component on processing the query (in
     *         miliseconds).
     */
    public long getTotalTimeElapsed()
    {
        return totalTimeElapsed;
    }

    /**
     * Returns a {@link Set}of identifiers of this profile's entries.
     * 
     * @return a {@link Set}of identifiers of this profile's entries.
     */
    public Set getProfileEntryIds()
    {
        return entries.keySet();
    }

    /**
     * Returns a {@link ProfileEntry}for given <code>entryId</code>.
     * 
     * @param entryId
     * @return
     */
    public ProfileEntry getProfileEntry(String entryId)
    {
        return (ProfileEntry) entries.get(entryId);
    }

    /**
     * Adds a new {@link ProfileEntry}to this profile under given
     * <code>entryId</code>.
     * 
     * @param entryId
     * @param entry
     */
    public void addProfileEntry(String entryId, ProfileEntry entry)
    {
        entries.put(entryId, entry);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return componentName + ": " + totalTimeElapsed + " ms";
    }
}