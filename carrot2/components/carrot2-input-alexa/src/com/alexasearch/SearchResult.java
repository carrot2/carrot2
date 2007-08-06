
// Generated file. Do not edit by hand.

package com.alexasearch;

public class SearchResult  implements java.io.Serializable {
    /* Your search term(s) */
    private java.lang.String searchTerms;

    /* Number of results matching query */
    private java.lang.String estimatedNumberOfDocuments;

    /* This element exists and is set to 'true' if results are truncated
     * because of insufficient MaxTime */
    private java.lang.Boolean maxTimeReached;

    /* Query expansion suggestions, comes with the QueryExpansion
     * result group */
    private java.lang.String[] queryExpansion;

    /* Query correction hint, comes with the QueryCorrection response
     * group */
    private java.lang.String queryCorrection;

    /* Matching document with result fields */
    private com.alexasearch.Document[] document;

    public SearchResult() {
    }

    public SearchResult(
           java.lang.String searchTerms,
           java.lang.String estimatedNumberOfDocuments,
           java.lang.Boolean maxTimeReached,
           java.lang.String[] queryExpansion,
           java.lang.String queryCorrection,
           com.alexasearch.Document[] document) {
           this.searchTerms = searchTerms;
           this.estimatedNumberOfDocuments = estimatedNumberOfDocuments;
           this.maxTimeReached = maxTimeReached;
           this.queryExpansion = queryExpansion;
           this.queryCorrection = queryCorrection;
           this.document = document;
    }


    /**
     * Gets the searchTerms value for this SearchResult.
     * 
     * @return searchTerms   * Your search term(s)
     */
    public java.lang.String getSearchTerms() {
        return searchTerms;
    }


    /**
     * Sets the searchTerms value for this SearchResult.
     * 
     * @param searchTerms   * Your search term(s)
     */
    public void setSearchTerms(java.lang.String searchTerms) {
        this.searchTerms = searchTerms;
    }


    /**
     * Gets the estimatedNumberOfDocuments value for this SearchResult.
     * 
     * @return estimatedNumberOfDocuments   * Number of results matching query
     */
    public java.lang.String getEstimatedNumberOfDocuments() {
        return estimatedNumberOfDocuments;
    }


    /**
     * Sets the estimatedNumberOfDocuments value for this SearchResult.
     * 
     * @param estimatedNumberOfDocuments   * Number of results matching query
     */
    public void setEstimatedNumberOfDocuments(java.lang.String estimatedNumberOfDocuments) {
        this.estimatedNumberOfDocuments = estimatedNumberOfDocuments;
    }


    /**
     * Gets the maxTimeReached value for this SearchResult.
     * 
     * @return maxTimeReached   * This element exists and is set to 'true' if results are truncated
     * because of insufficient MaxTime
     */
    public java.lang.Boolean getMaxTimeReached() {
        return maxTimeReached;
    }


    /**
     * Sets the maxTimeReached value for this SearchResult.
     * 
     * @param maxTimeReached   * This element exists and is set to 'true' if results are truncated
     * because of insufficient MaxTime
     */
    public void setMaxTimeReached(java.lang.Boolean maxTimeReached) {
        this.maxTimeReached = maxTimeReached;
    }


    /**
     * Gets the queryExpansion value for this SearchResult.
     * 
     * @return queryExpansion   * Query expansion suggestions, comes with the QueryExpansion
     * result group
     */
    public java.lang.String[] getQueryExpansion() {
        return queryExpansion;
    }


    /**
     * Sets the queryExpansion value for this SearchResult.
     * 
     * @param queryExpansion   * Query expansion suggestions, comes with the QueryExpansion
     * result group
     */
    public void setQueryExpansion(java.lang.String[] queryExpansion) {
        this.queryExpansion = queryExpansion;
    }


    /**
     * Gets the queryCorrection value for this SearchResult.
     * 
     * @return queryCorrection   * Query correction hint, comes with the QueryCorrection response
     * group
     */
    public java.lang.String getQueryCorrection() {
        return queryCorrection;
    }


    /**
     * Sets the queryCorrection value for this SearchResult.
     * 
     * @param queryCorrection   * Query correction hint, comes with the QueryCorrection response
     * group
     */
    public void setQueryCorrection(java.lang.String queryCorrection) {
        this.queryCorrection = queryCorrection;
    }


    /**
     * Gets the document value for this SearchResult.
     * 
     * @return document   * Matching document with result fields
     */
    public com.alexasearch.Document[] getDocument() {
        return document;
    }


    /**
     * Sets the document value for this SearchResult.
     * 
     * @param document   * Matching document with result fields
     */
    public void setDocument(com.alexasearch.Document[] document) {
        this.document = document;
    }

    public com.alexasearch.Document getDocument(int i) {
        return this.document[i];
    }

    public void setDocument(int i, com.alexasearch.Document _value) {
        this.document[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchResult)) return false;
        SearchResult other = (SearchResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.searchTerms==null && other.getSearchTerms()==null) || 
             (this.searchTerms!=null &&
              this.searchTerms.equals(other.getSearchTerms()))) &&
            ((this.estimatedNumberOfDocuments==null && other.getEstimatedNumberOfDocuments()==null) || 
             (this.estimatedNumberOfDocuments!=null &&
              this.estimatedNumberOfDocuments.equals(other.getEstimatedNumberOfDocuments()))) &&
            ((this.maxTimeReached==null && other.getMaxTimeReached()==null) || 
             (this.maxTimeReached!=null &&
              this.maxTimeReached.equals(other.getMaxTimeReached()))) &&
            ((this.queryExpansion==null && other.getQueryExpansion()==null) || 
             (this.queryExpansion!=null &&
              java.util.Arrays.equals(this.queryExpansion, other.getQueryExpansion()))) &&
            ((this.queryCorrection==null && other.getQueryCorrection()==null) || 
             (this.queryCorrection!=null &&
              this.queryCorrection.equals(other.getQueryCorrection()))) &&
            ((this.document==null && other.getDocument()==null) || 
             (this.document!=null &&
              java.util.Arrays.equals(this.document, other.getDocument())));
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
        if (getSearchTerms() != null) {
            _hashCode += getSearchTerms().hashCode();
        }
        if (getEstimatedNumberOfDocuments() != null) {
            _hashCode += getEstimatedNumberOfDocuments().hashCode();
        }
        if (getMaxTimeReached() != null) {
            _hashCode += getMaxTimeReached().hashCode();
        }
        if (getQueryExpansion() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQueryExpansion());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQueryExpansion(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getQueryCorrection() != null) {
            _hashCode += getQueryCorrection().hashCode();
        }
        if (getDocument() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocument());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocument(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">SearchResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchTerms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "SearchTerms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estimatedNumberOfDocuments");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "EstimatedNumberOfDocuments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxTimeReached");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "MaxTimeReached"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryExpansion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "QueryExpansion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Expansion"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryCorrection");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "QueryCorrection"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Document"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
