
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

package org.carrot2.core.clustering;

/**
 * A marker interface  for components that can  produce {@link RawCluster}
 * objects.
 * 
 * <p>
 * Successor components to this one should implement the corresponding {@link
 * RawClustersConsumer} interface.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see RawCluster
 * @see RawClustersConsumer
 * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
 * @see org.carrot2.core.LocalComponent
 */
public interface RawClustersProducer {
}
