/**
 * GoogleSearchResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.google.soapapi;

public class GoogleSearchResult  implements java.io.Serializable {
    private boolean documentFiltering;

    private java.lang.String searchComments;

    private int estimatedTotalResultsCount;

    private boolean estimateIsExact;

    private com.google.soapapi.ResultElement[] resultElements;

    private java.lang.String searchQuery;

    private int startIndex;

    private int endIndex;

    private java.lang.String searchTips;

    private com.google.soapapi.DirectoryCategory[] directoryCategories;

    private double searchTime;

    public GoogleSearchResult() {
    }

    public GoogleSearchResult(
           boolean documentFiltering,
           java.lang.String searchComments,
           int estimatedTotalResultsCount,
           boolean estimateIsExact,
           com.google.soapapi.ResultElement[] resultElements,
           java.lang.String searchQuery,
           int startIndex,
           int endIndex,
           java.lang.String searchTips,
           com.google.soapapi.DirectoryCategory[] directoryCategories,
           double searchTime) {
           this.documentFiltering = documentFiltering;
           this.searchComments = searchComments;
           this.estimatedTotalResultsCount = estimatedTotalResultsCount;
           this.estimateIsExact = estimateIsExact;
           this.resultElements = resultElements;
           this.searchQuery = searchQuery;
           this.startIndex = startIndex;
           this.endIndex = endIndex;
           this.searchTips = searchTips;
           this.directoryCategories = directoryCategories;
           this.searchTime = searchTime;
    }


    /**
     * Gets the documentFiltering value for this GoogleSearchResult.
     * 
     * @return documentFiltering
     */
    public boolean isDocumentFiltering() {
        return documentFiltering;
    }


    /**
     * Sets the documentFiltering value for this GoogleSearchResult.
     * 
     * @param documentFiltering
     */
    public void setDocumentFiltering(boolean documentFiltering) {
        this.documentFiltering = documentFiltering;
    }


    /**
     * Gets the searchComments value for this GoogleSearchResult.
     * 
     * @return searchComments
     */
    public java.lang.String getSearchComments() {
        return searchComments;
    }


    /**
     * Sets the searchComments value for this GoogleSearchResult.
     * 
     * @param searchComments
     */
    public void setSearchComments(java.lang.String searchComments) {
        this.searchComments = searchComments;
    }


    /**
     * Gets the estimatedTotalResultsCount value for this GoogleSearchResult.
     * 
     * @return estimatedTotalResultsCount
     */
    public int getEstimatedTotalResultsCount() {
        return estimatedTotalResultsCount;
    }


    /**
     * Sets the estimatedTotalResultsCount value for this GoogleSearchResult.
     * 
     * @param estimatedTotalResultsCount
     */
    public void setEstimatedTotalResultsCount(int estimatedTotalResultsCount) {
        this.estimatedTotalResultsCount = estimatedTotalResultsCount;
    }


    /**
     * Gets the estimateIsExact value for this GoogleSearchResult.
     * 
     * @return estimateIsExact
     */
    public boolean isEstimateIsExact() {
        return estimateIsExact;
    }


    /**
     * Sets the estimateIsExact value for this GoogleSearchResult.
     * 
     * @param estimateIsExact
     */
    public void setEstimateIsExact(boolean estimateIsExact) {
        this.estimateIsExact = estimateIsExact;
    }


    /**
     * Gets the resultElements value for this GoogleSearchResult.
     * 
     * @return resultElements
     */
    public com.google.soapapi.ResultElement[] getResultElements() {
        return resultElements;
    }


    /**
     * Sets the resultElements value for this GoogleSearchResult.
     * 
     * @param resultElements
     */
    public void setResultElements(com.google.soapapi.ResultElement[] resultElements) {
        this.resultElements = resultElements;
    }


    /**
     * Gets the searchQuery value for this GoogleSearchResult.
     * 
     * @return searchQuery
     */
    public java.lang.String getSearchQuery() {
        return searchQuery;
    }


    /**
     * Sets the searchQuery value for this GoogleSearchResult.
     * 
     * @param searchQuery
     */
    public void setSearchQuery(java.lang.String searchQuery) {
        this.searchQuery = searchQuery;
    }


    /**
     * Gets the startIndex value for this GoogleSearchResult.
     * 
     * @return startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }


    /**
     * Sets the startIndex value for this GoogleSearchResult.
     * 
     * @param startIndex
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }


    /**
     * Gets the endIndex value for this GoogleSearchResult.
     * 
     * @return endIndex
     */
    public int getEndIndex() {
        return endIndex;
    }


    /**
     * Sets the endIndex value for this GoogleSearchResult.
     * 
     * @param endIndex
     */
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }


    /**
     * Gets the searchTips value for this GoogleSearchResult.
     * 
     * @return searchTips
     */
    public java.lang.String getSearchTips() {
        return searchTips;
    }


    /**
     * Sets the searchTips value for this GoogleSearchResult.
     * 
     * @param searchTips
     */
    public void setSearchTips(java.lang.String searchTips) {
        this.searchTips = searchTips;
    }


    /**
     * Gets the directoryCategories value for this GoogleSearchResult.
     * 
     * @return directoryCategories
     */
    public com.google.soapapi.DirectoryCategory[] getDirectoryCategories() {
        return directoryCategories;
    }


    /**
     * Sets the directoryCategories value for this GoogleSearchResult.
     * 
     * @param directoryCategories
     */
    public void setDirectoryCategories(com.google.soapapi.DirectoryCategory[] directoryCategories) {
        this.directoryCategories = directoryCategories;
    }


    /**
     * Gets the searchTime value for this GoogleSearchResult.
     * 
     * @return searchTime
     */
    public double getSearchTime() {
        return searchTime;
    }


    /**
     * Sets the searchTime value for this GoogleSearchResult.
     * 
     * @param searchTime
     */
    public void setSearchTime(double searchTime) {
        this.searchTime = searchTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GoogleSearchResult)) return false;
        GoogleSearchResult other = (GoogleSearchResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.documentFiltering == other.isDocumentFiltering() &&
            ((this.searchComments==null && other.getSearchComments()==null) || 
             (this.searchComments!=null &&
              this.searchComments.equals(other.getSearchComments()))) &&
            this.estimatedTotalResultsCount == other.getEstimatedTotalResultsCount() &&
            this.estimateIsExact == other.isEstimateIsExact() &&
            ((this.resultElements==null && other.getResultElements()==null) || 
             (this.resultElements!=null &&
              java.util.Arrays.equals(this.resultElements, other.getResultElements()))) &&
            ((this.searchQuery==null && other.getSearchQuery()==null) || 
             (this.searchQuery!=null &&
              this.searchQuery.equals(other.getSearchQuery()))) &&
            this.startIndex == other.getStartIndex() &&
            this.endIndex == other.getEndIndex() &&
            ((this.searchTips==null && other.getSearchTips()==null) || 
             (this.searchTips!=null &&
              this.searchTips.equals(other.getSearchTips()))) &&
            ((this.directoryCategories==null && other.getDirectoryCategories()==null) || 
             (this.directoryCategories!=null &&
              java.util.Arrays.equals(this.directoryCategories, other.getDirectoryCategories()))) &&
            this.searchTime == other.getSearchTime();
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
        _hashCode += (isDocumentFiltering() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getSearchComments() != null) {
            _hashCode += getSearchComments().hashCode();
        }
        _hashCode += getEstimatedTotalResultsCount();
        _hashCode += (isEstimateIsExact() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getResultElements() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultElements());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultElements(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSearchQuery() != null) {
            _hashCode += getSearchQuery().hashCode();
        }
        _hashCode += getStartIndex();
        _hashCode += getEndIndex();
        if (getSearchTips() != null) {
            _hashCode += getSearchTips().hashCode();
        }
        if (getDirectoryCategories() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDirectoryCategories());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDirectoryCategories(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Double(getSearchTime()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GoogleSearchResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:GoogleSearch", "GoogleSearchResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentFiltering");
        elemField.setXmlName(new javax.xml.namespace.QName("", "documentFiltering"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchComments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "searchComments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estimatedTotalResultsCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "estimatedTotalResultsCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estimateIsExact");
        elemField.setXmlName(new javax.xml.namespace.QName("", "estimateIsExact"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultElements");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultElements"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:GoogleSearch", "ResultElement"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("", "searchQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startIndex");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startIndex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endIndex");
        elemField.setXmlName(new javax.xml.namespace.QName("", "endIndex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchTips");
        elemField.setXmlName(new javax.xml.namespace.QName("", "searchTips"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directoryCategories");
        elemField.setXmlName(new javax.xml.namespace.QName("", "directoryCategories"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:GoogleSearch", "DirectoryCategory"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "searchTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
