
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

package com.stachoodev.carrot.odp;

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
     * @return
     */
    public abstract int getCatid();

    /**
     * Returns this MutableTopic's <code>id</code>.
     * 
     * @return
     */
    public abstract String getId();

    /**
     * Returns a list of this MutableTopic's external pages.
     * 
     * @return
     */
    public abstract List getExternalPages();
}