
// Generated file. Do not edit by hand.

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
