
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

package com.dawidweiss.util.struts;


import org.apache.struts.util.*;


/**
 * A custom MessageResourcesFactory for STRUTS, which creates an instance of XmlMessageResources
 * for a given XML file name.
 */
public class XmlMessageResourcesFactory
    extends MessageResourcesFactory
{
    /**
     * Create an instance of XmlMessageResources for the given file name. The XML resource file
     * must be present in /WEB-INF/classes/i18n folder.
     *
     * @param configName The name of the configuration file to load.
     *
     * @return XmlMessageResources instance.
     */
    public MessageResources createResources(String configName)
    {
        return new XmlMessageResources(this, configName);
    }
}
