/**
 * SearchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.alexasearch;

public class SearchResponse  implements java.io.Serializable {
    private com.alexasearch.SearchResult searchResult;

    private com.alexasearch.ResponseMetadata responseMetadata;

    public SearchResponse() {
    }

    public SearchResponse(
           com.alexasearch.SearchResult searchResult,
           com.alexasearch.ResponseMetadata responseMetadata) {
           this.searchResult = searchResult;
           this.responseMetadata = responseMetadata;
    }


    /**
     * Gets the searchResult value for this SearchResponse.
     * 
     * @return searchResult
     */
    public com.alexasearch.SearchResult getSearchResult() {
        return searchResult;
    }


    /**
     * Sets the searchResult value for this SearchResponse.
     * 
     * @param searchResult
     */
    public void setSearchResult(com.alexasearch.SearchResult searchResult) {
        this.searchResult = searchResult;
    }


    /**
     * Gets the responseMetadata value for this SearchResponse.
     * 
     * @return responseMetadata
     */
    public com.alexasearch.ResponseMetadata getResponseMetadata() {
        return responseMetadata;
    }


    /**
     * Sets the responseMetadata value for this SearchResponse.
     * 
     * @param responseMetadata
     */
    public void setResponseMetadata(com.alexasearch.ResponseMetadata responseMetadata) {
        this.responseMetadata = responseMetadata;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchResponse)) return false;
        SearchResponse other = (SearchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.searchResult==null && other.getSearchResult()==null) || 
             (this.searchResult!=null &&
              this.searchResult.equals(other.getSearchResult()))) &&
            ((this.responseMetadata==null && other.getResponseMetadata()==null) || 
             (this.responseMetadata!=null &&
              this.responseMetadata.equals(other.getResponseMetadata())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSearchResult() != null) {
            _hashCode += getSearchResult().hashCode();
        }
        if (getResponseMetadata() != null) {
            _hashCode += getResponseMetadata().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">SearchResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "SearchResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">SearchResult"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseMetadata");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "ResponseMetadata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">ResponseMetadata"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
