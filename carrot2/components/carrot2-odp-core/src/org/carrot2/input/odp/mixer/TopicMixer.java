
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

package org.carrot2.input.odp.mixer;

import java.util.*;

/**
 * Mixes a number of topics from ODP based on some criteria.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicMixer
{
    /**
     * Mixes ODP topics based on given criteria. Implementations may cast the
     * <code>criteria</code> parameter to a more specific type.
     * 
     * @param criteria
     * @return a {@link List}of {@link org.carrot2.input.odp.Topic}
     *         instances matching mixing criteria. If no topics match mixing
     *         criteria a non- <code>null</code> empty list must be returned.
     */
    public List mix(Object criteria);
}