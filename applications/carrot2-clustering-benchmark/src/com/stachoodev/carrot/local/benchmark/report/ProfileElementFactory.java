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
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * Converts {@link com.dawidweiss.carrot.core.local.profiling.Profile}s into
 * XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ProfileElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        Profile profile = (Profile) object;
        Element profileElement = DocumentHelper.createElement("profile");
        profileElement.addAttribute("component", profile.getComponentName());

        ElementFactory factory = AllKnownElementFactories
            .getElementFactory(ProfileEntry.class);

        for (Iterator iter = profile.getProfileEntryIds().iterator(); iter
            .hasNext();)
        {
            String id = (String) iter.next();
            Element entryElement = factory.createElement(profile
                .getProfileEntry(id));
            entryElement.addAttribute("id", id);
            profileElement.add(entryElement);
        }

        return profileElement;
    }
}