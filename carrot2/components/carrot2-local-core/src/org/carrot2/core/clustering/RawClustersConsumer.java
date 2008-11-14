
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.clustering;

import org.carrot2.core.ProcessingException;


/**
 * A marker interface and data-related interface  for components that can
 * consume {@link RawCluster} objects.
 * 
 * <p>
 * Predecessor components to this one should implement the corresponding {@link
 * RawClustersProducer} interface.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see RawCluster
 * @see RawClustersProducer
 * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
 * @see org.carrot2.core.LocalComponent
 */
public interface RawClustersConsumer {
    /**
     * Data-related method for passing a new {@link RawCluster} object
     * reference to the component implementing this interface.
     *
     * @param cluster A new {@link RawCluster} passed from the predecessor
     *        component.
     *
     * @throws ProcessingException Thrown if this component cannot accept the
     *         cluster reference for some reason.
     */
    public void addCluster(RawCluster cluster) throws ProcessingException;
}
