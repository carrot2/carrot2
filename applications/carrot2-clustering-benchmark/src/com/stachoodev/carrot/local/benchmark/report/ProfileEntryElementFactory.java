
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

package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * Converts {@link com.dawidweiss.carrot.core.local.profiling.ProfileEntry}
 * instances into XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ProfileEntryElementFactory implements ElementFactory
{

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        ProfileEntry profileElement = (ProfileEntry) object;
        Element entryElement = DocumentHelper.createElement("profile-entry");

        if (profileElement.getName() != null)
        {
            entryElement.addElement("name").addText(profileElement.getName());
        }

        if (profileElement.getDescription() != null)
        {
            entryElement.addElement("description").addText(
                profileElement.getDescription());
        }

        Object data = profileElement.getData();
        ElementFactory factory = AllKnownElementFactories
            .getElementFactory(data.getClass());
        if (factory != null)
        {
            entryElement.add(factory.createElement(data));
        }
        else
        {
            if (data instanceof List)
            {
                entryElement.add(XMLReportUtils.createListElement((List) data,
                    "data", "entry"));
            }
            else if (data instanceof Map)
            {
                entryElement.add(XMLReportUtils.createMapElement((Map) data,
                    "data", "entry", "key"));
            }
            else
            {
                entryElement.addElement("data").addText(data.toString());
            }
        }

        return entryElement;
    }
}