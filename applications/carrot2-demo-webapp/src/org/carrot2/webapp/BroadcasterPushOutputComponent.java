
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

package org.carrot2.webapp;

import java.util.*;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalOutputComponentBase;

/**
 * An output component for accepting {@link RawDocument}s
 * from the input and broadcasting them to any interested consumers.  
 * 
 * @author Dawid Weiss
 */
final class BroadcasterPushOutputComponent extends ProfiledLocalOutputComponentBase
    implements RawDocumentsConsumer
{
    public final static String BROADCASTER = "broadcaster.instance";

    /** Broadcaster for {@link RawDocument}s */
    private Broadcaster broadcaster;

    public void startProcessing(RequestContext requestContext) throws ProcessingException {
        super.startProcessing(requestContext);

        final Broadcaster broadcaster = (Broadcaster) requestContext.getRequestParameters().get(BROADCASTER);
        if (broadcaster == null) {
            throw new ProcessingException("A broadcaster object is required.");
        }
        this.broadcaster = broadcaster;
    }

    public void endProcessing() throws ProcessingException {
        super.endProcessing();
        broadcaster.endProcessing();
    }

    public void processingErrorOccurred() {
        super.processingErrorOccurred();

        // broadcaster.endProcessing();
        // The above is called from the fetcher thread (to allow passing the exception).
    }

    public void flushResources() {
        super.flushResources();
        
        this.broadcaster = null;
    }

    public Set getRequiredSuccessorCapabilities() {
        return Collections.EMPTY_SET;
    }

    public Set getRequiredPredecessorCapabilities() {
        return new HashSet(Arrays.asList(
                new Object [] {RawDocumentsProducer.class}));
    }

    public Set getComponentCapabilities() {
        return new HashSet(Arrays.asList(
                new Object [] {RawDocumentsConsumer.class}));
    }

    public String getName() {
        return BroadcasterPushOutputComponent.class.getName();
    }

    public Object getResult() {
        return "no-result";
    }

    /**
     * Accept new document from the input component.
     */
    public void addDocument(RawDocument doc) throws ProcessingException {
        broadcaster.broadcastDocument(doc);
    }
}
