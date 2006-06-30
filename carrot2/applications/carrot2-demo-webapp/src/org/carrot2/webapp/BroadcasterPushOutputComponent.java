
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

package org.carrot2.webapp;

import java.util.*;

import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.ProfiledLocalOutputComponentBase;

/**
 * An output component for accepting {@link RawDocument}s
 * from the input and broadcasting them to any interested consumers.  
 * 
 * @author Dawid Weiss
 */
final class BroadcasterPushOutputComponent extends ProfiledLocalOutputComponentBase
    implements RawDocumentsConsumer
{
    public final static String BROADCASTER = "broadcaster.intance";

    /** Broadcaster for {@link RawDocument}s */
    private Broadcaster broadcaster;

    public void startProcessing(RequestContext requestContext) throws ProcessingException {
        final Broadcaster broadcaster = (Broadcaster) requestContext.getRequestParameters().get(BROADCASTER);
        if (broadcaster == null) {
            throw new ProcessingException("A broadcaster object is required.");
        }
        this.broadcaster = broadcaster;
    }

    public void endProcessing() throws ProcessingException {
        broadcaster.endProcessing();
    }

    public void processingErrorOccurred() {
        broadcaster.endProcessing();
    }

    public void flushResources() {
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
