/**
 * Document.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.alexasearch;

public class Document  implements java.io.Serializable {
    private org.apache.axis.types.URI url;

    private java.lang.String title;

    private java.lang.String bytes;

    private java.lang.String checksum;

    private java.lang.String context;

    private java.lang.String trafficRank;

    public Document() {
    }

    public Document(
           org.apache.axis.types.URI url,
           java.lang.String title,
           java.lang.String bytes,
           java.lang.String checksum,
           java.lang.String context,
           java.lang.String trafficRank) {
           this.url = url;
           this.title = title;
           this.bytes = bytes;
           this.checksum = checksum;
           this.context = context;
           this.trafficRank = trafficRank;
    }


    /**
     * Gets the url value for this Document.
     * 
     * @return url
     */
    public org.apache.axis.types.URI getUrl() {
        return url;
    }


    /**
     * Sets the url value for this Document.
     * 
     * @param url
     */
    public void setUrl(org.apache.axis.types.URI url) {
        this.url = url;
    }


    /**
     * Gets the title value for this Document.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this Document.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the bytes value for this Document.
     * 
     * @return bytes
     */
    public java.lang.String getBytes() {
        return bytes;
    }


    /**
     * Sets the bytes value for this Document.
     * 
     * @param bytes
     */
    public void setBytes(java.lang.String bytes) {
        this.bytes = bytes;
    }


    /**
     * Gets the checksum value for this Document.
     * 
     * @return checksum
     */
    public java.lang.String getChecksum() {
        return checksum;
    }


    /**
     * Sets the checksum value for this Document.
     * 
     * @param checksum
     */
    public void setChecksum(java.lang.String checksum) {
        this.checksum = checksum;
    }


    /**
     * Gets the context value for this Document.
     * 
     * @return context
     */
    public java.lang.String getContext() {
        return context;
    }


    /**
     * Sets the context value for this Document.
     * 
     * @param context
     */
    public void setContext(java.lang.String context) {
        this.context = context;
    }


    /**
     * Gets the trafficRank value for this Document.
     * 
     * @return trafficRank
     */
    public java.lang.String getTrafficRank() {
        return trafficRank;
    }


    /**
     * Sets the trafficRank value for this Document.
     * 
     * @param trafficRank
     */
    public void setTrafficRank(java.lang.String trafficRank) {
        this.trafficRank = trafficRank;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Document)) return false;
        Document other = (Document) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.url==null && other.getUrl()==null) || 
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.bytes==null && other.getBytes()==null) || 
             (this.bytes!=null &&
              this.bytes.equals(other.getBytes()))) &&
            ((this.checksum==null && other.getChecksum()==null) || 
             (this.checksum!=null &&
              this.checksum.equals(other.getChecksum()))) &&
            ((this.context==null && other.getContext()==null) || 
             (this.context!=null &&
              this.context.equals(other.getContext()))) &&
            ((this.trafficRank==null && other.getTrafficRank()==null) || 
             (this.trafficRank!=null &&
              this.trafficRank.equals(other.getTrafficRank())));
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
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getBytes() != null) {
            _hashCode += getBytes().hashCode();
        }
        if (getChecksum() != null) {
            _hashCode += getChecksum().hashCode();
        }
        if (getContext() != null) {
            _hashCode += getContext().hashCode();
        }
        if (getTrafficRank() != null) {
            _hashCode += getTrafficRank().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Document.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Document"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bytes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Bytes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checksum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Checksum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("context");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Context"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trafficRank");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "TrafficRank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
