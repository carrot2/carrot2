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
package com.stachoodev.carrot.odp.index;

/**
 * Defines an interface for producing instances of implementations of the
 * {@link Location}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface LocationFactory
{
    /**
     * Creates a new instance of the {@link Location} interface implementation.
     * 
     * @return a new instance of the {@link Location} interface implementation.
     */
    public Location createLocation();
}