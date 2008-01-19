/**
 * GoogleSearchPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.google.soapapi;

public interface GoogleSearchPort extends java.rmi.Remote {
    public byte[] doGetCachedPage(java.lang.String key, java.lang.String url) throws java.rmi.RemoteException;
    public java.lang.String doSpellingSuggestion(java.lang.String key, java.lang.String phrase) throws java.rmi.RemoteException;
    public com.google.soapapi.GoogleSearchResult doGoogleSearch(java.lang.String key, java.lang.String q, int start, int maxResults, boolean filter, java.lang.String restrict, boolean safeSearch, java.lang.String lr, java.lang.String ie, java.lang.String oe) throws java.rmi.RemoteException;
}
