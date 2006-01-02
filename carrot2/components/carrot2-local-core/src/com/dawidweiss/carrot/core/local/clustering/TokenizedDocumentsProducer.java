
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.clustering;

/**
 * A marker interface  for components that can  produce {@link
 * TokenizedDocument} objects.
 * 
 * <p>
 * Successor components to this one should implement the corresponding {@link
 * TokenizedDocumentsConsumer} interface.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see TokenizedDocument
 * @see TokenizedDocumentsConsumer
 * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
 * @see com.dawidweiss.carrot.core.local.LocalComponent
 */
public interface TokenizedDocumentsProducer {
}
