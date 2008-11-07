
// Generated file. Do not edit by hand.

package com.microsoft.msnsearch;

@SuppressWarnings({ "unchecked", "unused", "serial" })
public class SourceRequest  implements java.io.Serializable {
    private com.microsoft.msnsearch.SourceType source;

    private int offset;

    private int count;

    private java.lang.String fileType;

    private java.lang.String[] sortBy;

    private java.lang.String[] resultFields;

    private java.lang.String[] searchTagFilters;

    public SourceRequest() {
    }

    public SourceRequest(
           com.microsoft.msnsearch.SourceType source,
           int offset,
           int count,
           java.lang.String fileType,
           java.lang.String[] sortBy,
           java.lang.String[] resultFields,
           java.lang.String[] searchTagFilters) {
           this.source = source;
           this.offset = offset;
           this.count = count;
           this.fileType = fileType;
           this.sortBy = sortBy;
           this.resultFields = resultFields;
           this.searchTagFilters = searchTagFilters;
    }


    /**
     * Gets the source value for this SourceRequest.
     * 
     * @return source
     */
    public com.microsoft.msnsearch.SourceType getSource() {
        return source;
    }


    /**
     * Sets the source value for this SourceRequest.
     * 
     * @param source
     */
    public void setSource(com.microsoft.msnsearch.SourceType source) {
        this.source = source;
    }


    /**
     * Gets the offset value for this SourceRequest.
     * 
     * @return offset
     */
    public int getOffset() {
        return offset;
    }


    /**
     * Sets the offset value for this SourceRequest.
     * 
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }


    /**
     * Gets the count value for this SourceRequest.
     * 
     * @return count
     */
    public int getCount() {
        return count;
    }


    /**
     * Sets the count value for this SourceRequest.
     * 
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }


    /**
     * Gets the fileType value for this SourceRequest.
     * 
     * @return fileType
     */
    public java.lang.String getFileType() {
        return fileType;
    }


    /**
     * Sets the fileType value for this SourceRequest.
     * 
     * @param fileType
     */
    public void setFileType(java.lang.String fileType) {
        this.fileType = fileType;
    }


    /**
     * Gets the sortBy value for this SourceRequest.
     * 
     * @return sortBy
     */
    public java.lang.String[] getSortBy() {
        return sortBy;
    }


    /**
     * Sets the sortBy value for this SourceRequest.
     * 
     * @param sortBy
     */
    public void setSortBy(java.lang.String[] sortBy) {
        this.sortBy = sortBy;
    }


    /**
     * Gets the resultFields value for this SourceRequest.
     * 
     * @return resultFields
     */
    public java.lang.String[] getResultFields() {
        return resultFields;
    }


    /**
     * Sets the resultFields value for this SourceRequest.
     * 
     * @param resultFields
     */
    public void setResultFields(java.lang.String[] resultFields) {
        this.resultFields = resultFields;
    }


    /**
     * Gets the searchTagFilters value for this SourceRequest.
     * 
     * @return searchTagFilters
     */
    public java.lang.String[] getSearchTagFilters() {
        return searchTagFilters;
    }


    /**
     * Sets the searchTagFilters value for this SourceRequest.
     * 
     * @param searchTagFilters
     */
    public void setSearchTagFilters(java.lang.String[] searchTagFilters) {
        this.searchTagFilters = searchTagFilters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SourceRequest)) return false;
        SourceRequest other = (SourceRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            this.offset == other.getOffset() &&
            this.count == other.getCount() &&
            ((this.fileType==null && other.getFileType()==null) || 
             (this.fileType!=null &&
              this.fileType.equals(other.getFileType()))) &&
            ((this.sortBy==null && other.getSortBy()==null) || 
             (this.sortBy!=null &&
              java.util.Arrays.equals(this.sortBy, other.getSortBy()))) &&
            ((this.resultFields==null && other.getResultFields()==null) || 
             (this.resultFields!=null &&
              java.util.Arrays.equals(this.resultFields, other.getResultFields()))) &&
            ((this.searchTagFilters==null && other.getSearchTagFilters()==null) || 
             (this.searchTagFilters!=null &&
              java.util.Arrays.equals(this.searchTagFilters, other.getSearchTagFilters())));
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
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        _hashCode += getOffset();
        _hashCode += getCount();
        if (getFileType() != null) {
            _hashCode += getFileType().hashCode();
        }
        if (getSortBy() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSortBy());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSortBy(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResultFields() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultFields());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultFields(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSearchTagFilters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSearchTagFilters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSearchTagFilters(), i);
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
        new org.apache.axis.description.TypeDesc(SourceRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offset");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Offset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("count");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "FileType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sortBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SortBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SortByType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultFields");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ResultFields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ResultFieldMask"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchTagFilters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchTagFilters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "string"));
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
