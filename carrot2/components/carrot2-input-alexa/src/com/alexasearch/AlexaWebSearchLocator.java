
// Generated file. Do not edit by hand.

package com.alexasearch;

public class AlexaWebSearchLocator extends org.apache.axis.client.Service implements com.alexasearch.AlexaWebSearch {

/**
 * The Alexa Web Search web service offers programmatic access to
 * Alexa's web search engine. The Alexa search engine
 *             is a web-wide search engine that powers the web search
 * on the Alexa website.
 */

    public AlexaWebSearchLocator() {
    }


    public AlexaWebSearchLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AlexaWebSearchLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AlexaWebSearchHttpPort
    private java.lang.String AlexaWebSearchHttpPort_address = "http://wsearch.amazonaws.com/";

    public java.lang.String getAlexaWebSearchHttpPortAddress() {
        return AlexaWebSearchHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AlexaWebSearchHttpPortWSDDServiceName = "AlexaWebSearchHttpPort";

    public java.lang.String getAlexaWebSearchHttpPortWSDDServiceName() {
        return AlexaWebSearchHttpPortWSDDServiceName;
    }

    public void setAlexaWebSearchHttpPortWSDDServiceName(java.lang.String name) {
        AlexaWebSearchHttpPortWSDDServiceName = name;
    }

    public com.alexasearch.AlexaWebSearchPortType getAlexaWebSearchHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AlexaWebSearchHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAlexaWebSearchHttpPort(endpoint);
    }

    public com.alexasearch.AlexaWebSearchPortType getAlexaWebSearchHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.alexasearch.AlexaWebSearchBindingStub _stub = new com.alexasearch.AlexaWebSearchBindingStub(portAddress, this);
            _stub.setPortName(getAlexaWebSearchHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAlexaWebSearchHttpPortEndpointAddress(java.lang.String address) {
        AlexaWebSearchHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.alexasearch.AlexaWebSearchPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.alexasearch.AlexaWebSearchBindingStub _stub = new com.alexasearch.AlexaWebSearchBindingStub(new java.net.URL(AlexaWebSearchHttpPort_address), this);
                _stub.setPortName(getAlexaWebSearchHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AlexaWebSearchHttpPort".equals(inputPortName)) {
            return getAlexaWebSearchHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "AlexaWebSearch");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "AlexaWebSearchHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AlexaWebSearchHttpPort".equals(portName)) {
            setAlexaWebSearchHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
