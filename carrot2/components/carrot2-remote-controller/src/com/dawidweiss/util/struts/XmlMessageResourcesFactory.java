

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
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
