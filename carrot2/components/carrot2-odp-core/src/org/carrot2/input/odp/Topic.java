
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

package org.carrot2.input.odp;

import java.util.*;

/**
 * Represents a sinlge ODP topic.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface Topic
{
    /**
     * Returns this MutableTopic's <code>catid</code>.
     * 
     */
    public abstract int getCatid();

    /**
     * Returns this MutableTopic's <code>id</code>.
     * 
     */
    public abstract String getId();

    /**
     * Returns a list of this MutableTopic's external pages.
     * 
     */
    public abstract List getExternalPages();
}