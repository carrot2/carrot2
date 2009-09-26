
// Generated file. Do not edit by hand.

package com.microsoft.msnsearch;

@SuppressWarnings({ "unchecked", "serial" })
public class SearchConstants  implements java.io.Serializable {
    private java.lang.String markBegin;

    private java.lang.String markEnd;

    public SearchConstants() {
    }

    public SearchConstants(
           java.lang.String markBegin,
           java.lang.String markEnd) {
           this.markBegin = markBegin;
           this.markEnd = markEnd;
    }


    /**
     * Gets the markBegin value for this SearchConstants.
     * 
     * @return markBegin
     */
    public java.lang.String getMarkBegin() {
        return markBegin;
    }


    /**
     * Sets the markBegin value for this SearchConstants.
     * 
     * @param markBegin
     */
    public void setMarkBegin(java.lang.String markBegin) {
        this.markBegin = markBegin;
    }


    /**
     * Gets the markEnd value for this SearchConstants.
     * 
     * @return markEnd
     */
    public java.lang.String getMarkEnd() {
        return markEnd;
    }


    /**
     * Sets the markEnd value for this SearchConstants.
     * 
     * @param markEnd
     */
    public void setMarkEnd(java.lang.String markEnd) {
        this.markEnd = markEnd;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchConstants)) return false;
        SearchConstants other = (SearchConstants) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.markBegin==null && other.getMarkBegin()==null) || 
             (this.markBegin!=null &&
              this.markBegin.equals(other.getMarkBegin()))) &&
            ((this.markEnd==null && other.getMarkEnd()==null) || 
             (this.markEnd!=null &&
              this.markEnd.equals(other.getMarkEnd())));
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
        if (getMarkBegin() != null) {
            _hashCode += getMarkBegin().hashCode();
        }
        if (getMarkEnd() != null) {
            _hashCode += getMarkEnd().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchConstants.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchConstants"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("markBegin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "MarkBegin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("markEnd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "MarkEnd"));
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
