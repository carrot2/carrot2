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
package com.stachoodev.carrot.local.benchmark.report;

import org.dom4j.*;

/**
 * Converts an object to a corresponding XML element.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface ElementFactory
{
    /**
     * Converts the <code>object</code> to a corresponding XML element.
     * Implementations will usually narrow down the reference to some more
     * concrete type.
     * 
     * @param object
     * @return
     */
    public Element createElement(Object object);
}