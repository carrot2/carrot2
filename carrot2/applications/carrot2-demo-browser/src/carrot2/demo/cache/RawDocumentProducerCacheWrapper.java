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

/**
 * A simple input component caching results from a
 * {@link com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer}
 * and reusing it in subsequent requests.
 * 
 * <b>This component can be considered a hack, so I wouldn't use it anywhere outside of the browser
 * component.</b> The cached request is remembered as a pair of {wrapped-component, query}. The request
 * context is important only at the time the first request is made (and cached). All subsequent requests
 * acquired from the cache do not rely on the request context parameters (which might affect the
 * input component's response if cache were not used).
 * 
 * @author Dawid Weiss
 */
public class RawDocumentProducerCacheWrapper extends LocalInputComponentBase {

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(
            Arrays.asList(new Object [] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(
            Arrays.asList(new Object [] { RawDocumentsProducer.class }));

    /**
     * Static shared cache of query results.
     */
    private final static RawDocumentsCache cache = 
        new WeakRawDocumentsCache(/* default hard cache size */ 3);

    /** The wrapped component. */
    private final LocalInputComponent wrapped;

    /** Current query. */
    private String query;

    /**
     * Consumer for {@link com.dawidweiss.carrot.core.local.clustering.RawDocument}s.
     */
    private ClustersConsumerOutputComponent consumer; 
	private Result cachedResult;

    public RawDocumentProducerCacheWrapper(LocalInputComponent wrappedComponent) {
        // Make sure the wrapped component implements
        // the required capability.
        final Set caps = wrappedComponent.getComponentCapabilities();
        if (false == caps.contains(RawDocumentsProducer.class)) {
            throw new IllegalArgumentException("Wrapped component does not expose the required capability: "
                    + RawDocumentsProducer.class);
        }
        this.wrapped = wrappedComponent;
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

        final ClustersConsumerOutputComponent.Result result
            = cache.get(new CacheEntry(wrapped, query));

        if (result == null) {
            // Requires caching.
            this.consumer = new ClustersConsumerOutputComponent();
            wrapped.setNext(consumer);
            wrapped.setQuery(query);
            wrapped.startProcessing(requestContext);
            this.cachedResult = null;
        } else {
            // Already cached.
            this.cachedResult = result;
        }
    }

    public void endProcessing() throws ProcessingException {
        if (this.cachedResult == null) {
            // Caching in progress, finish it.
            this.wrapped.endProcessing();

            // Ok, save the result 
            final ClustersConsumerOutputComponent.Result result = 
                (ClustersConsumerOutputComponent.Result) this.consumer.getResult();

            this.wrapped.flushResources();
            this.cachedResult = result;

            cache.put(new CacheEntry(wrapped, query), this.cachedResult);
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
        this.cachedResult = null;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}