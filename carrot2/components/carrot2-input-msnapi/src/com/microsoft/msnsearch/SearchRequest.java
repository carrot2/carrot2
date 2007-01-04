
// Generated file. Do not edit by hand.

package com.microsoft.msnsearch;

public class SearchRequest  implements java.io.Serializable {
    private java.lang.String appID;

    private java.lang.String query;

    private java.lang.String cultureInfo;

    private com.microsoft.msnsearch.SafeSearchOptions safeSearch;

    private java.lang.String[] flags;

    private com.microsoft.msnsearch.Location location;

    private com.microsoft.msnsearch.SourceRequest[] requests;

    public SearchRequest() {
    }

    public SearchRequest(
           java.lang.String appID,
           java.lang.String query,
           java.lang.String cultureInfo,
           com.microsoft.msnsearch.SafeSearchOptions safeSearch,
           java.lang.String[] flags,
           com.microsoft.msnsearch.Location location,
           com.microsoft.msnsearch.SourceRequest[] requests) {
           this.appID = appID;
           this.query = query;
           this.cultureInfo = cultureInfo;
           this.safeSearch = safeSearch;
           this.flags = flags;
           this.location = location;
           this.requests = requests;
    }


    /**
     * Gets the appID value for this SearchRequest.
     * 
     * @return appID
     */
    public java.lang.String getAppID() {
        return appID;
    }


    /**
     * Sets the appID value for this SearchRequest.
     * 
     * @param appID
     */
    public void setAppID(java.lang.String appID) {
        this.appID = appID;
    }


    /**
     * Gets the query value for this SearchRequest.
     * 
     * @return query
     */
    public java.lang.String getQuery() {
        return query;
    }


    /**
     * Sets the query value for this SearchRequest.
     * 
     * @param query
     */
    public void setQuery(java.lang.String query) {
        this.query = query;
    }


    /**
     * Gets the cultureInfo value for this SearchRequest.
     * 
     * @return cultureInfo
     */
    public java.lang.String getCultureInfo() {
        return cultureInfo;
    }


    /**
     * Sets the cultureInfo value for this SearchRequest.
     * 
     * @param cultureInfo
     */
    public void setCultureInfo(java.lang.String cultureInfo) {
        this.cultureInfo = cultureInfo;
    }


    /**
     * Gets the safeSearch value for this SearchRequest.
     * 
     * @return safeSearch
     */
    public com.microsoft.msnsearch.SafeSearchOptions getSafeSearch() {
        return safeSearch;
    }


    /**
     * Sets the safeSearch value for this SearchRequest.
     * 
     * @param safeSearch
     */
    public void setSafeSearch(com.microsoft.msnsearch.SafeSearchOptions safeSearch) {
        this.safeSearch = safeSearch;
    }


    /**
     * Gets the flags value for this SearchRequest.
     * 
     * @return flags
     */
    public java.lang.String[] getFlags() {
        return flags;
    }


    /**
     * Sets the flags value for this SearchRequest.
     * 
     * @param flags
     */
    public void setFlags(java.lang.String[] flags) {
        this.flags = flags;
    }


    /**
     * Gets the location value for this SearchRequest.
     * 
     * @return location
     */
    public com.microsoft.msnsearch.Location getLocation() {
        return location;
    }


    /**
     * Sets the location value for this SearchRequest.
     * 
     * @param location
     */
    public void setLocation(com.microsoft.msnsearch.Location location) {
        this.location = location;
    }


    /**
     * Gets the requests value for this SearchRequest.
     * 
     * @return requests
     */
    public com.microsoft.msnsearch.SourceRequest[] getRequests() {
        return requests;
    }


    /**
     * Sets the requests value for this SearchRequest.
     * 
     * @param requests
     */
    public void setRequests(com.microsoft.msnsearch.SourceRequest[] requests) {
        this.requests = requests;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchRequest)) return false;
        SearchRequest other = (SearchRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.appID==null && other.getAppID()==null) || 
             (this.appID!=null &&
              this.appID.equals(other.getAppID()))) &&
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery()))) &&
            ((this.cultureInfo==null && other.getCultureInfo()==null) || 
             (this.cultureInfo!=null &&
              this.cultureInfo.equals(other.getCultureInfo()))) &&
            ((this.safeSearch==null && other.getSafeSearch()==null) || 
             (this.safeSearch!=null &&
              this.safeSearch.equals(other.getSafeSearch()))) &&
            ((this.flags==null && other.getFlags()==null) || 
             (this.flags!=null &&
              java.util.Arrays.equals(this.flags, other.getFlags()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.requests==null && other.getRequests()==null) || 
             (this.requests!=null &&
              java.util.Arrays.equals(this.requests, other.getRequests())));
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
        if (getAppID() != null) {
            _hashCode += getAppID().hashCode();
        }
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        if (getCultureInfo() != null) {
            _hashCode += getCultureInfo().hashCode();
        }
        if (getSafeSearch() != null) {
            _hashCode += getSafeSearch().hashCode();
        }
        if (getFlags() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFlags());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFlags(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getRequests() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRequests());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRequests(), i);
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
        new org.apache.axis.description.TypeDesc(SearchRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("appID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "AppID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cultureInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "CultureInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("safeSearch");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SafeSearch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SafeSearchOptions"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flags");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Flags"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchFlags"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Location"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requests");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Requests"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceRequest"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceRequest"));
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
