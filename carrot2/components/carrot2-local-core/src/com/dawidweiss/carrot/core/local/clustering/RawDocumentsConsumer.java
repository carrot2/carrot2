
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

package com.dawidweiss.carrot.core.local.clustering;

import com.dawidweiss.carrot.core.local.ProcessingException;


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
 * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
 * @see com.dawidweiss.carrot.core.local.LocalComponent
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
