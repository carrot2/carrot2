/**
 * AlexaWebSearch.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.alexasearch;

public interface AlexaWebSearch extends javax.xml.rpc.Service {

/**
 * The Alexa Web Search web service offers programmatic access to
 * Alexa's web search engine. The Alexa search engine
 *             is a web-wide search engine that powers the web search
 * on the Alexa website.
 */
    public java.lang.String getAlexaWebSearchHttpPortAddress();

    public com.alexasearch.AlexaWebSearchPortType getAlexaWebSearchHttpPort() throws javax.xml.rpc.ServiceException;

    public com.alexasearch.AlexaWebSearchPortType getAlexaWebSearchHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
