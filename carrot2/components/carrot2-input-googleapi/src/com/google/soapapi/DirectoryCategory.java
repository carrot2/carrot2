/**
 * DirectoryCategory.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.google.soapapi;

public class DirectoryCategory  implements java.io.Serializable {
    private java.lang.String fullViewableName;

    private java.lang.String specialEncoding;

    public DirectoryCategory() {
    }

    public DirectoryCategory(
           java.lang.String fullViewableName,
           java.lang.String specialEncoding) {
           this.fullViewableName = fullViewableName;
           this.specialEncoding = specialEncoding;
    }


    /**
     * Gets the fullViewableName value for this DirectoryCategory.
     * 
     * @return fullViewableName
     */
    public java.lang.String getFullViewableName() {
        return fullViewableName;
    }


    /**
     * Sets the fullViewableName value for this DirectoryCategory.
     * 
     * @param fullViewableName
     */
    public void setFullViewableName(java.lang.String fullViewableName) {
        this.fullViewableName = fullViewableName;
    }


    /**
     * Gets the specialEncoding value for this DirectoryCategory.
     * 
     * @return specialEncoding
     */
    public java.lang.String getSpecialEncoding() {
        return specialEncoding;
    }


    /**
     * Sets the specialEncoding value for this DirectoryCategory.
     * 
     * @param specialEncoding
     */
    public void setSpecialEncoding(java.lang.String specialEncoding) {
        this.specialEncoding = specialEncoding;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DirectoryCategory)) return false;
        DirectoryCategory other = (DirectoryCategory) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.fullViewableName==null && other.getFullViewableName()==null) || 
             (this.fullViewableName!=null &&
              this.fullViewableName.equals(other.getFullViewableName()))) &&
            ((this.specialEncoding==null && other.getSpecialEncoding()==null) || 
             (this.specialEncoding!=null &&
              this.specialEncoding.equals(other.getSpecialEncoding())));
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
        if (getFullViewableName() != null) {
            _hashCode += getFullViewableName().hashCode();
        }
        if (getSpecialEncoding() != null) {
            _hashCode += getSpecialEncoding().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DirectoryCategory.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:GoogleSearch", "DirectoryCategory"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullViewableName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fullViewableName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialEncoding");
        elemField.setXmlName(new javax.xml.namespace.QName("", "specialEncoding"));
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
