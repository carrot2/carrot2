/**
 * MSNSearchServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

public class MSNSearchServiceLocator extends org.apache.axis.client.Service implements com.microsoft.msnsearch.MSNSearchService {

    public MSNSearchServiceLocator() {
    }


    public MSNSearchServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MSNSearchServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MSNSearchPort
    private java.lang.String MSNSearchPort_address = "http://soap.search.live.com:80/webservices.asmx";

    public java.lang.String getMSNSearchPortAddress() {
        return MSNSearchPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MSNSearchPortWSDDServiceName = "MSNSearchPort";

    public java.lang.String getMSNSearchPortWSDDServiceName() {
        return MSNSearchPortWSDDServiceName;
    }

    public void setMSNSearchPortWSDDServiceName(java.lang.String name) {
        MSNSearchPortWSDDServiceName = name;
    }

    public com.microsoft.msnsearch.MSNSearchPortType getMSNSearchPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MSNSearchPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMSNSearchPort(endpoint);
    }

    public com.microsoft.msnsearch.MSNSearchPortType getMSNSearchPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.microsoft.msnsearch.MSNSearchPortBindingStub _stub = new com.microsoft.msnsearch.MSNSearchPortBindingStub(portAddress, this);
            _stub.setPortName(getMSNSearchPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMSNSearchPortEndpointAddress(java.lang.String address) {
        MSNSearchPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.microsoft.msnsearch.MSNSearchPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.microsoft.msnsearch.MSNSearchPortBindingStub _stub = new com.microsoft.msnsearch.MSNSearchPortBindingStub(new java.net.URL(MSNSearchPort_address), this);
                _stub.setPortName(getMSNSearchPortWSDDServiceName());
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
        if ("MSNSearchPort".equals(inputPortName)) {
            return getMSNSearchPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "MSNSearchService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "MSNSearchPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MSNSearchPort".equals(portName)) {
            setMSNSearchPortEndpointAddress(address);
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
