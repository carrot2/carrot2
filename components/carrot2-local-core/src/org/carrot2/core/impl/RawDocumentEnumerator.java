
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

package org.carrot2.core.impl;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;

/**
 * A local component for adding a sequential number to every {@link org.carrot2.core.clustering.RawDocument}
 * accepted as input.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public final class RawDocumentEnumerator extends ProfiledLocalFilterComponentBase 
    implements RawDocumentsProducer, RawDocumentsConsumer
{
    /**
     * A property set on a {@link RawDocument}, denoting its number
     * in the sequence of documents added to this component. Value of
     * this property is of type {@link Integer}.
     */
    public static final String DOCUMENT_SEQ_NUMBER = "doc-seq-number";

    /**
     * Capabilities exposed by this component.
     */
    private static final Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object [] {
            RawDocumentsProducer.class, RawDocumentsConsumer.class
        }));

    /**
     * Capabilities required of the successor of this component.
     */
    private static final Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object [] {
            RawDocumentsConsumer.class,
        }));

    /**
     * Capabilities required of the predecessor of this component.
     */
    private static final Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class,
        }));

    /**
     * The successor component, consumer of documents accepted by this
     * component.
     */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * Current sequence number.
     */
    private int sequenceNumber;

    public RawDocumentEnumerator() {
    }

    public void addDocument(final RawDocument doc) throws ProcessingException {
        startTimer();
        final Integer seqNum = new Integer(sequenceNumber);
        doc.setProperty(DOCUMENT_SEQ_NUMBER, seqNum);
        sequenceNumber++;
        stopTimer();

        this.rawDocumentConsumer.addDocument(doc);
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException {
        this.sequenceNumber = 0;
        super.startProcessing(requestContext);
    }

    /**
     * Sets the successor component for the duration of the current request. The
     * component should implement <code>RawDocumentsConsumer</code> interface.
     * 
     * @param next The successor component.
     */
    public void setNext(LocalComponent next) {
        super.setNext(next);

        if (next instanceof RawDocumentsConsumer) {
            this.rawDocumentConsumer = (RawDocumentsConsumer) next;
        } else {
            throw new IllegalArgumentException("Successor should implement: "
                + RawDocumentsConsumer.class.getName());
        }
    }

    /**
     * Performs a cleanup before the object is reused.
     */
    public void flushResources() {
        super.flushResources();
        this.rawDocumentConsumer = null;
        this.sequenceNumber = 0;
    }

    public Set getComponentCapabilities() {
        return CAPABILITIES_COMPONENT;
    }

    public Set getRequiredPredecessorCapabilities() {
        return CAPABILITIES_PREDECESSOR;
    }

    public Set getRequiredSuccessorCapabilities() {
        return CAPABILITIES_SUCCESSOR;
    }

    public String getName() {
        return "RawDocument enumerator";
    }
}
