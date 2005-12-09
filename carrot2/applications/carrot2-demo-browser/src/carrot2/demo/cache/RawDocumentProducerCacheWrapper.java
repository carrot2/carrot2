package carrot2.demo.cache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentBase;
import com.dawidweiss.carrot.core.local.LocalInputComponentBase;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;

public class RawDocumentProducerCacheWrapper extends LocalInputComponentBase {

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(
            Arrays.asList(new Object [] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(
            Arrays.asList(new Object [] { RawDocumentsProducer.class }));

    /**
     * The wrapped component.
     */
    private LocalComponent wrapped;

    private String query;

    private boolean firstCall;

    public RawDocumentProducerCacheWrapper(LocalComponent wrappedComponent) {
        // Make sure the wrapped component implements
        // the required capability.
        final Set caps = wrappedComponent.getComponentCapabilities();
        if (false == caps.contains(RawDocumentsProducer.class)) {
            throw new IllegalArgumentException("Wrapped component does not expose the required capability.");
        }
        this.wrapped = wrappedComponent;
        this.firstCall = true;
    }

    public Set getRequiredSuccessorCapabilities() {
        return SUCCESSOR_CAPABILITIES; 
    }

    public Set getRequiredPredecessorCapabilities() {
        return java.util.Collections.EMPTY_SET;
    }

    /**
     * Provides an implementation that has no capabilities (an empty set).
     */
    public Set getComponentCapabilities()
    {
        return java.util.Collections.EMPTY_SET;
    }
    
    public void startProcessing(RequestContext requestContext) throws ProcessingException {
        if (firstCall) {

            firstCall = false;
        } else {
            // Playback the first call from cache.
        }
    }

    public void endProcessing() throws ProcessingException {
    }

    public void flushResources() {
        this.query = null;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
