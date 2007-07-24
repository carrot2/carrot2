
// Generated file. Do not edit by hand.

package com.alexasearch;

public class Search  implements java.io.Serializable {
    private com.alexasearch.SearchResponseGroup[] responseGroup;

    private java.lang.String query;

    private java.math.BigInteger maxTime;

    private java.lang.String unique;

    private java.math.BigInteger pageNumber;

    private java.math.BigInteger maxNumberOfDocumentsPerPage;

    private java.lang.String signature;

    private java.lang.String AWSAccessKeyId;

    private java.lang.String timestamp;

    public Search() {
    }

    public Search(
           com.alexasearch.SearchResponseGroup[] responseGroup,
           java.lang.String query,
           java.math.BigInteger maxTime,
           java.lang.String unique,
           java.math.BigInteger pageNumber,
           java.math.BigInteger maxNumberOfDocumentsPerPage,
           java.lang.String signature,
           java.lang.String AWSAccessKeyId,
           java.lang.String timestamp) {
           this.responseGroup = responseGroup;
           this.query = query;
           this.maxTime = maxTime;
           this.unique = unique;
           this.pageNumber = pageNumber;
           this.maxNumberOfDocumentsPerPage = maxNumberOfDocumentsPerPage;
           this.signature = signature;
           this.AWSAccessKeyId = AWSAccessKeyId;
           this.timestamp = timestamp;
    }


    /**
     * Gets the responseGroup value for this Search.
     * 
     * @return responseGroup
     */
    public com.alexasearch.SearchResponseGroup[] getResponseGroup() {
        return responseGroup;
    }


    /**
     * Sets the responseGroup value for this Search.
     * 
     * @param responseGroup
     */
    public void setResponseGroup(com.alexasearch.SearchResponseGroup[] responseGroup) {
        this.responseGroup = responseGroup;
    }

    public com.alexasearch.SearchResponseGroup getResponseGroup(int i) {
        return this.responseGroup[i];
    }

    public void setResponseGroup(int i, com.alexasearch.SearchResponseGroup _value) {
        this.responseGroup[i] = _value;
    }


    /**
     * Gets the query value for this Search.
     * 
     * @return query
     */
    public java.lang.String getQuery() {
        return query;
    }


    /**
     * Sets the query value for this Search.
     * 
     * @param query
     */
    public void setQuery(java.lang.String query) {
        this.query = query;
    }


    /**
     * Gets the maxTime value for this Search.
     * 
     * @return maxTime
     */
    public java.math.BigInteger getMaxTime() {
        return maxTime;
    }


    /**
     * Sets the maxTime value for this Search.
     * 
     * @param maxTime
     */
    public void setMaxTime(java.math.BigInteger maxTime) {
        this.maxTime = maxTime;
    }


    /**
     * Gets the unique value for this Search.
     * 
     * @return unique
     */
    public java.lang.String getUnique() {
        return unique;
    }


    /**
     * Sets the unique value for this Search.
     * 
     * @param unique
     */
    public void setUnique(java.lang.String unique) {
        this.unique = unique;
    }


    /**
     * Gets the pageNumber value for this Search.
     * 
     * @return pageNumber
     */
    public java.math.BigInteger getPageNumber() {
        return pageNumber;
    }


    /**
     * Sets the pageNumber value for this Search.
     * 
     * @param pageNumber
     */
    public void setPageNumber(java.math.BigInteger pageNumber) {
        this.pageNumber = pageNumber;
    }


    /**
     * Gets the maxNumberOfDocumentsPerPage value for this Search.
     * 
     * @return maxNumberOfDocumentsPerPage
     */
    public java.math.BigInteger getMaxNumberOfDocumentsPerPage() {
        return maxNumberOfDocumentsPerPage;
    }


    /**
     * Sets the maxNumberOfDocumentsPerPage value for this Search.
     * 
     * @param maxNumberOfDocumentsPerPage
     */
    public void setMaxNumberOfDocumentsPerPage(java.math.BigInteger maxNumberOfDocumentsPerPage) {
        this.maxNumberOfDocumentsPerPage = maxNumberOfDocumentsPerPage;
    }


    /**
     * Gets the signature value for this Search.
     * 
     * @return signature
     */
    public java.lang.String getSignature() {
        return signature;
    }


    /**
     * Sets the signature value for this Search.
     * 
     * @param signature
     */
    public void setSignature(java.lang.String signature) {
        this.signature = signature;
    }


    /**
     * Gets the AWSAccessKeyId value for this Search.
     * 
     * @return AWSAccessKeyId
     */
    public java.lang.String getAWSAccessKeyId() {
        return AWSAccessKeyId;
    }


    /**
     * Sets the AWSAccessKeyId value for this Search.
     * 
     * @param AWSAccessKeyId
     */
    public void setAWSAccessKeyId(java.lang.String AWSAccessKeyId) {
        this.AWSAccessKeyId = AWSAccessKeyId;
    }


    /**
     * Gets the timestamp value for this Search.
     * 
     * @return timestamp
     */
    public java.lang.String getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this Search.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.lang.String timestamp) {
        this.timestamp = timestamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Search)) return false;
        Search other = (Search) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.responseGroup==null && other.getResponseGroup()==null) || 
             (this.responseGroup!=null &&
              java.util.Arrays.equals(this.responseGroup, other.getResponseGroup()))) &&
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery()))) &&
            ((this.maxTime==null && other.getMaxTime()==null) || 
             (this.maxTime!=null &&
              this.maxTime.equals(other.getMaxTime()))) &&
            ((this.unique==null && other.getUnique()==null) || 
             (this.unique!=null &&
              this.unique.equals(other.getUnique()))) &&
            ((this.pageNumber==null && other.getPageNumber()==null) || 
             (this.pageNumber!=null &&
              this.pageNumber.equals(other.getPageNumber()))) &&
            ((this.maxNumberOfDocumentsPerPage==null && other.getMaxNumberOfDocumentsPerPage()==null) || 
             (this.maxNumberOfDocumentsPerPage!=null &&
              this.maxNumberOfDocumentsPerPage.equals(other.getMaxNumberOfDocumentsPerPage()))) &&
            ((this.signature==null && other.getSignature()==null) || 
             (this.signature!=null &&
              this.signature.equals(other.getSignature()))) &&
            ((this.AWSAccessKeyId==null && other.getAWSAccessKeyId()==null) || 
             (this.AWSAccessKeyId!=null &&
              this.AWSAccessKeyId.equals(other.getAWSAccessKeyId()))) &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp())));
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
        if (getResponseGroup() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResponseGroup());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResponseGroup(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        if (getMaxTime() != null) {
            _hashCode += getMaxTime().hashCode();
        }
        if (getUnique() != null) {
            _hashCode += getUnique().hashCode();
        }
        if (getPageNumber() != null) {
            _hashCode += getPageNumber().hashCode();
        }
        if (getMaxNumberOfDocumentsPerPage() != null) {
            _hashCode += getMaxNumberOfDocumentsPerPage().hashCode();
        }
        if (getSignature() != null) {
            _hashCode += getSignature().hashCode();
        }
        if (getAWSAccessKeyId() != null) {
            _hashCode += getAWSAccessKeyId().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Search.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">Search"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "ResponseGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">>Search>ResponseGroup"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "MaxTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unique");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Unique"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pageNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "PageNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxNumberOfDocumentsPerPage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "MaxNumberOfDocumentsPerPage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signature");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Signature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AWSAccessKeyId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "AWSAccessKeyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", "Timestamp"));
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
