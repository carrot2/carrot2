/**
 * GoogleSearchServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.google.soapapi;

public class GoogleSearchServiceLocator extends org.apache.axis.client.Service implements com.google.soapapi.GoogleSearchService {

    public GoogleSearchServiceLocator() {
    }


    public GoogleSearchServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GoogleSearchServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GoogleSearchPort
    private java.lang.String GoogleSearchPort_address = "http://api.google.com/search/beta2";

    public java.lang.String getGoogleSearchPortAddress() {
        return GoogleSearchPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GoogleSearchPortWSDDServiceName = "GoogleSearchPort";

    public java.lang.String getGoogleSearchPortWSDDServiceName() {
        return GoogleSearchPortWSDDServiceName;
    }

    public void setGoogleSearchPortWSDDServiceName(java.lang.String name) {
        GoogleSearchPortWSDDServiceName = name;
    }

    public com.google.soapapi.GoogleSearchPort getGoogleSearchPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GoogleSearchPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGoogleSearchPort(endpoint);
    }

    public com.google.soapapi.GoogleSearchPort getGoogleSearchPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.google.soapapi.GoogleSearchBindingStub _stub = new com.google.soapapi.GoogleSearchBindingStub(portAddress, this);
            _stub.setPortName(getGoogleSearchPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGoogleSearchPortEndpointAddress(java.lang.String address) {
        GoogleSearchPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.google.soapapi.GoogleSearchPort.class.isAssignableFrom(serviceEndpointInterface)) {
                com.google.soapapi.GoogleSearchBindingStub _stub = new com.google.soapapi.GoogleSearchBindingStub(new java.net.URL(GoogleSearchPort_address), this);
                _stub.setPortName(getGoogleSearchPortWSDDServiceName());
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
        if ("GoogleSearchPort".equals(inputPortName)) {
            return getGoogleSearchPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:GoogleSearch", "GoogleSearchService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:GoogleSearch", "GoogleSearchPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GoogleSearchPort".equals(portName)) {
            setGoogleSearchPortEndpointAddress(address);
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
