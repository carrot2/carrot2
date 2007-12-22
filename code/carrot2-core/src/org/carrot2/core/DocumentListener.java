/**
 * 
 */
package org.carrot2.core;

/**
 * Using this interface, applications can get notified about documents being fetched
 * before the whole processing has finished. The external code can put collections of
 * implementations of this interface to the attributes map, from which they will be bound
 * to the document source, and if the document source supports incremental pushing, the
 * listener will be notified.
 */
public interface DocumentListener
{
    public void documentFetched(Document document);

    public void documentFetchingFinished();

    // TODO: do we need a method notifying of fetching failure?
}
