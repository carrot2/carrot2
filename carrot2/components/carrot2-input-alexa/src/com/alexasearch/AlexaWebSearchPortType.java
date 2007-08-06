
// Generated file. Do not edit by hand.

package com.alexasearch;

public interface AlexaWebSearchPortType extends java.rmi.Remote {

    /**
     * Query the Alexa search engine to search the web for documents
     * that match a query. This action returns the search results.
     */
    public com.alexasearch.SearchResponse search(com.alexasearch.Search body) throws java.rmi.RemoteException;
}
