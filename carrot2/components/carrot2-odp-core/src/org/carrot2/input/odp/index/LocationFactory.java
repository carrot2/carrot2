
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp.index;

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