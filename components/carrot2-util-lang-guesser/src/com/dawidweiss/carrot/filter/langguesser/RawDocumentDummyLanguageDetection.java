package com.dawidweiss.carrot.filter.langguesser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser;
import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * A local component that adds a predefined language code
 * to the properties of a document. No real recognition is performed.
 * 
 * <p>The component implements {@link RawDocumentConsumer}
 * and {@link RawDocumentProducer}, so basically, it acts
 * as a filter between two other components. 
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawDocumentDummyLanguageDetection extends ProfiledLocalFilterComponentBase
    implements RawDocumentsProducer, RawDocumentsConsumer {
    
    /**
     * Capabilities exposed by this component.
     */
    private static final Set CAPABILITIES_COMPONENT = new HashSet(Arrays.asList(
                new Object[] { RawDocumentsProducer.class, RawDocumentsConsumer.class }));

    /**
     * Capabilities required of the successor of this component.
     */
    private static final Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays.asList(
                new Object[] { RawDocumentsConsumer.class, }));

    /**
     * Capabilities required of the predecessor of this component.
     */
    private static final Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays.asList(
                new Object[] { RawDocumentsProducer.class, }));

    /**
     * The successor component, consumer of
     * documents accepted by this component.
     */
	private RawDocumentsConsumer rawDocumentConsumer;
    
    /** Language code to be added to the documents */
    private String languageCode;
    
    /**
     * Public contructor requires an instance of {@link LanguageGuesser}
     * interface that is used for language detection.
     * @param languageGuesser A language guesser instance.
     */
    public RawDocumentDummyLanguageDetection(String languageCode) {
        this.languageCode = languageCode;
    }
    
	/**
     * New document to process: identify language and
     * add a new property to the document.
     * 
	 * @see com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.RawDocument)
	 */
	public void addDocument(RawDocument doc) throws ProcessingException {
	    startTimer();
        doc.setProperty(RawDocument.PROPERTY_LANGUAGE, languageCode);
        stopTimer();
        
        // pass the document reference...
        this.rawDocumentConsumer.addDocument(doc);
	}
    
    
    /**
     * Sets the successor component for the duration of the current request.
     * The component should implement <code>RawDocumentsConsumer</code>
     * interface.
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
    *
    * @see com.dawidweiss.carrot.core.local.LocalComponent.flushResources()
    */
   public void flushResources() {
       super.flushResources();
       this.rawDocumentConsumer = null;
   }
    
	/*
	 * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
	 */
	public Set getComponentCapabilities() {
        return CAPABILITIES_COMPONENT;
	}
	
    /*
	 * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
	 */
	public Set getRequiredPredecessorCapabilities() {
        return CAPABILITIES_PREDECESSOR;
	}
	
    /* 
	 * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
	 */
	public Set getRequiredSuccessorCapabilities() {
        return CAPABILITIES_SUCCESSOR;
	}

	/* (non-Javadoc)
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Dummy Language Guesser";
    }
}
