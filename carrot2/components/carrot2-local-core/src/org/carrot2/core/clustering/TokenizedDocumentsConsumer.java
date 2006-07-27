
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

package org.carrot2.core.clustering;

import org.carrot2.core.ProcessingException;


/**
 * A marker interface and data-related interface  for components that can
 * produce {@link TokenizedDocument} objects.
 * 
 * <p>
 * Predecessor components to this one should implement the corresponding {@link
 * TokenizedDocumentsProducer} interface.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see TokenizedDocument
 * @see TokenizedDocumentsProducer
 * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
 * @see org.carrot2.core.LocalComponent
 */
public interface TokenizedDocumentsConsumer {
    /**
     * Data-related method for passing a new {@link TokenizedDocument} object
     * reference to the component implementing this interface.
     *
     * @param doc A new {@link TokenizedDocument} object passed from the
     *        predecessor component.
     *
     * @throws ProcessingException Thrown if this component cannot accept the
     *         document reference for some reason.
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException;
}
