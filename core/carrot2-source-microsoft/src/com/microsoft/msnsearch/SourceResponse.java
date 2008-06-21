/**
 * SourceResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

@SuppressWarnings({ "unchecked", "unused", "serial" })
public class SourceResponse  implements java.io.Serializable {
    private com.microsoft.msnsearch.SourceType source;

    private int offset;

    private int total;

    private java.lang.String recourseQuery;

    private com.microsoft.msnsearch.Result[] results;

    public SourceResponse() {
    }

    public SourceResponse(
           com.microsoft.msnsearch.SourceType source,
           int offset,
           int total,
           java.lang.String recourseQuery,
           com.microsoft.msnsearch.Result[] results) {
           this.source = source;
           this.offset = offset;
           this.total = total;
           this.recourseQuery = recourseQuery;
           this.results = results;
    }


    /**
     * Gets the source value for this SourceResponse.
     * 
     * @return source
     */
    public com.microsoft.msnsearch.SourceType getSource() {
        return source;
    }


    /**
     * Sets the source value for this SourceResponse.
     * 
     * @param source
     */
    public void setSource(com.microsoft.msnsearch.SourceType source) {
        this.source = source;
    }


    /**
     * Gets the offset value for this SourceResponse.
     * 
     * @return offset
     */
    public int getOffset() {
        return offset;
    }


    /**
     * Sets the offset value for this SourceResponse.
     * 
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }


    /**
     * Gets the total value for this SourceResponse.
     * 
     * @return total
     */
    public int getTotal() {
        return total;
    }


    /**
     * Sets the total value for this SourceResponse.
     * 
     * @param total
     */
    public void setTotal(int total) {
        this.total = total;
    }


    /**
     * Gets the recourseQuery value for this SourceResponse.
     * 
     * @return recourseQuery
     */
    public java.lang.String getRecourseQuery() {
        return recourseQuery;
    }


    /**
     * Sets the recourseQuery value for this SourceResponse.
     * 
     * @param recourseQuery
     */
    public void setRecourseQuery(java.lang.String recourseQuery) {
        this.recourseQuery = recourseQuery;
    }


    /**
     * Gets the results value for this SourceResponse.
     * 
     * @return results
     */
    public com.microsoft.msnsearch.Result[] getResults() {
        return results;
    }


    /**
     * Sets the results value for this SourceResponse.
     * 
     * @param results
     */
    public void setResults(com.microsoft.msnsearch.Result[] results) {
        this.results = results;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SourceResponse)) return false;
        SourceResponse other = (SourceResponse) obj;
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
            this.total == other.getTotal() &&
            ((this.recourseQuery==null && other.getRecourseQuery()==null) || 
             (this.recourseQuery!=null &&
              this.recourseQuery.equals(other.getRecourseQuery()))) &&
            ((this.results==null && other.getResults()==null) || 
             (this.results!=null &&
              java.util.Arrays.equals(this.results, other.getResults())));
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
        _hashCode += getTotal();
        if (getRecourseQuery() != null) {
            _hashCode += getRecourseQuery().hashCode();
        }
        if (getResults() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResults());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResults(), i);
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
        new org.apache.axis.description.TypeDesc(SourceResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceResponse"));
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
        elemField.setFieldName("total");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Total"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recourseQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "RecourseQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("results");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Results"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Result"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Result"));
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
