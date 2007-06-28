/**
 * AlexaWebSearchPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.alexasearch;

public interface AlexaWebSearchPortType extends java.rmi.Remote {

    /**
     * Query the Alexa search engine to search the web for documents
     * that match a query. This action returns the search results.
     */
    public com.alexasearch.SearchResponse search(com.alexasearch.Search body) throws java.rmi.RemoteException;
}
