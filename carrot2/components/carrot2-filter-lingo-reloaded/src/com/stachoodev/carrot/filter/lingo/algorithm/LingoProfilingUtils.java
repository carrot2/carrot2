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
package com.stachoodev.carrot.filter.lingo.algorithm;

import java.util.*;

import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoProfilingUtils
{
    /**
     * @param label
     * @param text
     */
    public static void logInfo(Profile profile, String label, Object text)
    {
        if (profile == null)
        {
            return;
        }

        Map info;
        if (profile.getProfileEntry("info") == null)
        {
            info = new LinkedHashMap();
            profile.addProfileEntry("info", new ProfileEntry(
                "Counts and Numbers", null, info));
        }
        else
        {
            info = (Map) profile.getProfileEntry("info").getData();
        }
        info.put(label, text);
    }

    /**
     * @param label
     * @param text
     */
    public static void logInfo(Profile profile, String label, String phase,
        Object text)
    {
        if (profile == null)
        {
            return;
        }

        Map info;
        if (profile.getProfileEntry("info" + phase) == null)
        {
            info = new LinkedHashMap();
            profile.addProfileEntry("info" + phase, new ProfileEntry(
                "Counts and Numbers (" + phase + ")", null, info));
        }
        else
        {
            info = (Map) profile.getProfileEntry("info" + phase).getData();
        }
        info.put(label, text);
    }

    /**
     * @param component
     * @param time
     */
    public static void logMeantime(Profile profile, String component, long time)
    {
        if (profile == null)
        {
            return;
        }

        Map times;
        if (profile.getProfileEntry("subcomponent-times") == null)
        {
            times = new LinkedHashMap();
            profile.addProfileEntry("subcomponent-times", new ProfileEntry(
                "Subcomponent Times", null, times));
        }
        else
        {
            times = (Map) profile.getProfileEntry("subcomponent-times")
                .getData();
        }
        times.put(component, Long.toString(time) + " ms");
    }

    /**
     * @param component
     * @param time
     */
    public static void logMeantime(Profile profile, String component,
        String phase, long time)
    {
        if (profile == null)
        {
            return;
        }

        Map times;
        if (profile.getProfileEntry("subcomponent-times" + phase) == null)
        {
            times = new LinkedHashMap();
            profile.addProfileEntry("subcomponent-times" + phase,
                new ProfileEntry("Subcomponent Times (" + phase + ")", null,
                    times));
        }
        else
        {
            times = (Map) profile.getProfileEntry("subcomponent-times" + phase)
                .getData();
        }
        times.put(component, Long.toString(time) + " ms");
    }

}