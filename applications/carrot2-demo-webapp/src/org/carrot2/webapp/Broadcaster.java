
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.core.clustering.RawDocument;

/**
 * Broadcaster and synchronization point for threads utilizing
 * {@link RawDocument}s from a single input.
 * 
 * @author Dawid Weiss
 */
final class Broadcaster {
    /** A list of {@link RawDocument}s ready to be read by iterators. */
    private final ArrayList documents;

    /**
     * There may still be incoming documents, so keep iterators waiting on next()
     * if they reach the end of {@link #documents}.
     */
    private boolean processingEnded = false;

    private int useCount;

    /**
     * Processing error to signal to all consumers.
     */
    private Exception processingError;

    /**
     * Implement a thread-blocking iterator on the document list if there are no
     * elements in the buffer.
     */
    private final class DocIterator implements Iterator {
        int position = -1;

        public boolean hasNext() {
            synchronized (documents) {
                while (true) {
                    if (position + 1 < documents.size()) {
                        return true;
                    } else {
                        if (processingEnded) {
                            if (processingError != null) {
                                throw new BroadcasterException(processingError);
                            }
                            return false;
                        } else {
                            // Go to sleep waiting for new elements
                            // or end-of-stream marker.
                            try {
                                documents.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException("Sleep on hasNext() interrupted.");
                            }
                        }
                    }
                }
            }
        }

        public Object next() {
            synchronized (documents) {
                if (hasNext()) {
                    position++;
                    return documents.get(position);
                } else throw new ArrayIndexOutOfBoundsException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Creates an initially empty broadcaster, ready to accept new documents
     * on {@link #broadcastDocument(RawDocument)}.
     */
    public Broadcaster() {
        this.documents = new ArrayList();
    }

    /**
     * Creates a fixed broadcaster which contains all search results 
     * from the beginning.  
     */
    public Broadcaster(SearchResults results) {
        this.documents = results.getDocuments();
        this.processingEnded = true;
    }

    public final void broadcastDocument(RawDocument doc) throws ProcessingException {
        synchronized (documents) {
            documents.add(doc);
            documents.notifyAll();
        }
    }

    public void endProcessing() {
        synchronized (documents) {
            this.processingEnded = true;
            documents.notifyAll();
        }
    }

    public void attach() {
        useCount++;
    }

    public void detach() {
        useCount--;
    }

    public boolean inUse() {
        return useCount > 0;
    }

    public Iterator docIterator() {
        synchronized (documents) {
            if (processingEnded) {
                // multithreaded read-only access to an array list should
                // be thread-safe.
                return this.documents.iterator();
            } else {
                return new DocIterator();
            }
        }
    }

    public List getDocuments() {
        if (this.processingEnded == false)
            throw new IllegalStateException("Broadcaster not finalized.");
        return this.documents;
    }

    public void endProcessingWithError(Exception e) {
        synchronized (documents) {
            if (this.processingEnded == true) {
                throw new IllegalStateException();
            }

            this.processingError = e;
            endProcessing();
        }
    }
}
