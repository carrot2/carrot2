/**
 * ResultElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.google.soapapi;

public class ResultElement  implements java.io.Serializable {
    private java.lang.String summary;

    private java.lang.String URL;

    private java.lang.String snippet;

    private java.lang.String title;

    private java.lang.String cachedSize;

    private boolean relatedInformationPresent;

    private java.lang.String hostName;

    private com.google.soapapi.DirectoryCategory directoryCategory;

    private java.lang.String directoryTitle;

    public ResultElement() {
    }

    public ResultElement(
           java.lang.String summary,
           java.lang.String URL,
           java.lang.String snippet,
           java.lang.String title,
           java.lang.String cachedSize,
           boolean relatedInformationPresent,
           java.lang.String hostName,
           com.google.soapapi.DirectoryCategory directoryCategory,
           java.lang.String directoryTitle) {
           this.summary = summary;
           this.URL = URL;
           this.snippet = snippet;
           this.title = title;
           this.cachedSize = cachedSize;
           this.relatedInformationPresent = relatedInformationPresent;
           this.hostName = hostName;
           this.directoryCategory = directoryCategory;
           this.directoryTitle = directoryTitle;
    }


    /**
     * Gets the summary value for this ResultElement.
     * 
     * @return summary
     */
    public java.lang.String getSummary() {
        return summary;
    }


    /**
     * Sets the summary value for this ResultElement.
     * 
     * @param summary
     */
    public void setSummary(java.lang.String summary) {
        this.summary = summary;
    }


    /**
     * Gets the URL value for this ResultElement.
     * 
     * @return URL
     */
    public java.lang.String getURL() {
        return URL;
    }


    /**
     * Sets the URL value for this ResultElement.
     * 
     * @param URL
     */
    public void setURL(java.lang.String URL) {
        this.URL = URL;
    }


    /**
     * Gets the snippet value for this ResultElement.
     * 
     * @return snippet
     */
    public java.lang.String getSnippet() {
        return snippet;
    }


    /**
     * Sets the snippet value for this ResultElement.
     * 
     * @param snippet
     */
    public void setSnippet(java.lang.String snippet) {
        this.snippet = snippet;
    }


    /**
     * Gets the title value for this ResultElement.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this ResultElement.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the cachedSize value for this ResultElement.
     * 
     * @return cachedSize
     */
    public java.lang.String getCachedSize() {
        return cachedSize;
    }


    /**
     * Sets the cachedSize value for this ResultElement.
     * 
     * @param cachedSize
     */
    public void setCachedSize(java.lang.String cachedSize) {
        this.cachedSize = cachedSize;
    }


    /**
     * Gets the relatedInformationPresent value for this ResultElement.
     * 
     * @return relatedInformationPresent
     */
    public boolean isRelatedInformationPresent() {
        return relatedInformationPresent;
    }


    /**
     * Sets the relatedInformationPresent value for this ResultElement.
     * 
     * @param relatedInformationPresent
     */
    public void setRelatedInformationPresent(boolean relatedInformationPresent) {
        this.relatedInformationPresent = relatedInformationPresent;
    }


    /**
     * Gets the hostName value for this ResultElement.
     * 
     * @return hostName
     */
    public java.lang.String getHostName() {
        return hostName;
    }


    /**
     * Sets the hostName value for this ResultElement.
     * 
     * @param hostName
     */
    public void setHostName(java.lang.String hostName) {
        this.hostName = hostName;
    }


    /**
     * Gets the directoryCategory value for this ResultElement.
     * 
     * @return directoryCategory
     */
    public com.google.soapapi.DirectoryCategory getDirectoryCategory() {
        return directoryCategory;
    }


    /**
     * Sets the directoryCategory value for this ResultElement.
     * 
     * @param directoryCategory
     */
    public void setDirectoryCategory(com.google.soapapi.DirectoryCategory directoryCategory) {
        this.directoryCategory = directoryCategory;
    }


    /**
     * Gets the directoryTitle value for this ResultElement.
     * 
     * @return directoryTitle
     */
    public java.lang.String getDirectoryTitle() {
        return directoryTitle;
    }


    /**
     * Sets the directoryTitle value for this ResultElement.
     * 
     * @param directoryTitle
     */
    public void setDirectoryTitle(java.lang.String directoryTitle) {
        this.directoryTitle = directoryTitle;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResultElement)) return false;
        ResultElement other = (ResultElement) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.summary==null && other.getSummary()==null) || 
             (this.summary!=null &&
              this.summary.equals(other.getSummary()))) &&
            ((this.URL==null && other.getURL()==null) || 
             (this.URL!=null &&
              this.URL.equals(other.getURL()))) &&
            ((this.snippet==null && other.getSnippet()==null) || 
             (this.snippet!=null &&
              this.snippet.equals(other.getSnippet()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.cachedSize==null && other.getCachedSize()==null) || 
             (this.cachedSize!=null &&
              this.cachedSize.equals(other.getCachedSize()))) &&
            this.relatedInformationPresent == other.isRelatedInformationPresent() &&
            ((this.hostName==null && other.getHostName()==null) || 
             (this.hostName!=null &&
              this.hostName.equals(other.getHostName()))) &&
            ((this.directoryCategory==null && other.getDirectoryCategory()==null) || 
             (this.directoryCategory!=null &&
              this.directoryCategory.equals(other.getDirectoryCategory()))) &&
            ((this.directoryTitle==null && other.getDirectoryTitle()==null) || 
             (this.directoryTitle!=null &&
              this.directoryTitle.equals(other.getDirectoryTitle())));
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
        if (getSummary() != null) {
            _hashCode += getSummary().hashCode();
        }
        if (getURL() != null) {
            _hashCode += getURL().hashCode();
        }
        if (getSnippet() != null) {
            _hashCode += getSnippet().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getCachedSize() != null) {
            _hashCode += getCachedSize().hashCode();
        }
        _hashCode += (isRelatedInformationPresent() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getHostName() != null) {
            _hashCode += getHostName().hashCode();
        }
        if (getDirectoryCategory() != null) {
            _hashCode += getDirectoryCategory().hashCode();
        }
        if (getDirectoryTitle() != null) {
            _hashCode += getDirectoryTitle().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResultElement.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:GoogleSearch", "ResultElement"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("summary");
        elemField.setXmlName(new javax.xml.namespace.QName("", "summary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("URL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "URL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("snippet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "snippet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cachedSize");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cachedSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("relatedInformationPresent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "relatedInformationPresent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hostName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directoryCategory");
        elemField.setXmlName(new javax.xml.namespace.QName("", "directoryCategory"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:GoogleSearch", "DirectoryCategory"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directoryTitle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "directoryTitle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
