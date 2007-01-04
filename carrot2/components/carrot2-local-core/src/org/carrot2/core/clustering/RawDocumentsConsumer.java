
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

import org.carrot2.core.ProcessingException;


/**
 * A marker interface and data-related interface  for components that can
 * produce {@link RawDocument} objects.
 * 
 * <p>
 * Predecessor components to this one should implement the corresponding {@link
 * RawDocumentsProducer} interface.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see RawDocument
 * @see RawDocumentsProducer
 * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
 * @see org.carrot2.core.LocalComponent
 */
public interface RawDocumentsConsumer {
    /**
     * Data-related method for passing a new {@link RawDocument} object
     * reference to the component implementing this interface.
     *
     * @param doc A new {@link RawDocument} object passed from the predecessor
     *        component.
     *
     * @throws ProcessingException Thrown if this component cannot accept the
     *         document reference for some reason.
     */
    public void addDocument(RawDocument doc) throws ProcessingException;
}
