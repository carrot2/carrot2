package carrot2.demo.cache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponentBase;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent.Result;

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
    private LocalInputComponent wrapped;
    
    /** Next query */
    private String query;
    
    /**
     * Consumer for {@link com.dawidweiss.carrot.core.local.clustering.RawDocument}s.
     */
    private ClustersConsumerOutputComponent consumer; 
    
    /** 
     * A flag indicating the first call (true -- not cached) and then the 
     * replay rounds (false) 
     */
    private boolean firstCall;

	private Result cachedResult;

    public RawDocumentProducerCacheWrapper(LocalInputComponent wrappedComponent) {
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
    
    public Set getComponentCapabilities() {
        return COMPONENT_CAPABILITIES;
    }
    
    public void startProcessing(RequestContext requestContext) throws ProcessingException {
    	super.startProcessing(requestContext);

        if (firstCall) {
        	this.consumer = new ClustersConsumerOutputComponent();
        	wrapped.setNext(consumer);
        	wrapped.setQuery(query);
        	wrapped.startProcessing(requestContext);
        }
    }

    public void endProcessing() throws ProcessingException {
        if (firstCall) {
        	this.wrapped.endProcessing();

        	// Ok, save the result 
        	final ClustersConsumerOutputComponent.Result result = 
        		(ClustersConsumerOutputComponent.Result) this.consumer.getResult();

        	this.wrapped.flushResources();

        	this.cachedResult = result; 
    		firstCall = false;
        }

        // Playback RawDocuments from cache.
        final RawDocumentsConsumer nextComponent = (RawDocumentsConsumer) super.next; 
        for (Iterator i = this.cachedResult.documents.iterator(); i.hasNext();) {
        	final RawDocument doc = (RawDocument) i.next();
        	nextComponent.addDocument(doc);
        }

        super.endProcessing();
    }

    public void flushResources() {
    	super.flushResources();
        this.query = null;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
