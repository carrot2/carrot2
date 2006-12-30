/**
 * Address.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

public class Address  implements java.io.Serializable {
    private java.lang.String addressLine;

    private java.lang.String primaryCity;

    private java.lang.String secondaryCity;

    private java.lang.String subdivision;

    private java.lang.String postalCode;

    private java.lang.String countryRegion;

    private java.lang.String formattedAddress;

    public Address() {
    }

    public Address(
           java.lang.String addressLine,
           java.lang.String primaryCity,
           java.lang.String secondaryCity,
           java.lang.String subdivision,
           java.lang.String postalCode,
           java.lang.String countryRegion,
           java.lang.String formattedAddress) {
           this.addressLine = addressLine;
           this.primaryCity = primaryCity;
           this.secondaryCity = secondaryCity;
           this.subdivision = subdivision;
           this.postalCode = postalCode;
           this.countryRegion = countryRegion;
           this.formattedAddress = formattedAddress;
    }


    /**
     * Gets the addressLine value for this Address.
     * 
     * @return addressLine
     */
    public java.lang.String getAddressLine() {
        return addressLine;
    }


    /**
     * Sets the addressLine value for this Address.
     * 
     * @param addressLine
     */
    public void setAddressLine(java.lang.String addressLine) {
        this.addressLine = addressLine;
    }


    /**
     * Gets the primaryCity value for this Address.
     * 
     * @return primaryCity
     */
    public java.lang.String getPrimaryCity() {
        return primaryCity;
    }


    /**
     * Sets the primaryCity value for this Address.
     * 
     * @param primaryCity
     */
    public void setPrimaryCity(java.lang.String primaryCity) {
        this.primaryCity = primaryCity;
    }


    /**
     * Gets the secondaryCity value for this Address.
     * 
     * @return secondaryCity
     */
    public java.lang.String getSecondaryCity() {
        return secondaryCity;
    }


    /**
     * Sets the secondaryCity value for this Address.
     * 
     * @param secondaryCity
     */
    public void setSecondaryCity(java.lang.String secondaryCity) {
        this.secondaryCity = secondaryCity;
    }


    /**
     * Gets the subdivision value for this Address.
     * 
     * @return subdivision
     */
    public java.lang.String getSubdivision() {
        return subdivision;
    }


    /**
     * Sets the subdivision value for this Address.
     * 
     * @param subdivision
     */
    public void setSubdivision(java.lang.String subdivision) {
        this.subdivision = subdivision;
    }


    /**
     * Gets the postalCode value for this Address.
     * 
     * @return postalCode
     */
    public java.lang.String getPostalCode() {
        return postalCode;
    }


    /**
     * Sets the postalCode value for this Address.
     * 
     * @param postalCode
     */
    public void setPostalCode(java.lang.String postalCode) {
        this.postalCode = postalCode;
    }


    /**
     * Gets the countryRegion value for this Address.
     * 
     * @return countryRegion
     */
    public java.lang.String getCountryRegion() {
        return countryRegion;
    }


    /**
     * Sets the countryRegion value for this Address.
     * 
     * @param countryRegion
     */
    public void setCountryRegion(java.lang.String countryRegion) {
        this.countryRegion = countryRegion;
    }


    /**
     * Gets the formattedAddress value for this Address.
     * 
     * @return formattedAddress
     */
    public java.lang.String getFormattedAddress() {
        return formattedAddress;
    }


    /**
     * Sets the formattedAddress value for this Address.
     * 
     * @param formattedAddress
     */
    public void setFormattedAddress(java.lang.String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Address)) return false;
        Address other = (Address) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.addressLine==null && other.getAddressLine()==null) || 
             (this.addressLine!=null &&
              this.addressLine.equals(other.getAddressLine()))) &&
            ((this.primaryCity==null && other.getPrimaryCity()==null) || 
             (this.primaryCity!=null &&
              this.primaryCity.equals(other.getPrimaryCity()))) &&
            ((this.secondaryCity==null && other.getSecondaryCity()==null) || 
             (this.secondaryCity!=null &&
              this.secondaryCity.equals(other.getSecondaryCity()))) &&
            ((this.subdivision==null && other.getSubdivision()==null) || 
             (this.subdivision!=null &&
              this.subdivision.equals(other.getSubdivision()))) &&
            ((this.postalCode==null && other.getPostalCode()==null) || 
             (this.postalCode!=null &&
              this.postalCode.equals(other.getPostalCode()))) &&
            ((this.countryRegion==null && other.getCountryRegion()==null) || 
             (this.countryRegion!=null &&
              this.countryRegion.equals(other.getCountryRegion()))) &&
            ((this.formattedAddress==null && other.getFormattedAddress()==null) || 
             (this.formattedAddress!=null &&
              this.formattedAddress.equals(other.getFormattedAddress())));
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
        if (getAddressLine() != null) {
            _hashCode += getAddressLine().hashCode();
        }
        if (getPrimaryCity() != null) {
            _hashCode += getPrimaryCity().hashCode();
        }
        if (getSecondaryCity() != null) {
            _hashCode += getSecondaryCity().hashCode();
        }
        if (getSubdivision() != null) {
            _hashCode += getSubdivision().hashCode();
        }
        if (getPostalCode() != null) {
            _hashCode += getPostalCode().hashCode();
        }
        if (getCountryRegion() != null) {
            _hashCode += getCountryRegion().hashCode();
        }
        if (getFormattedAddress() != null) {
            _hashCode += getFormattedAddress().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Address.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Address"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "AddressLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryCity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "PrimaryCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryCity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SecondaryCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subdivision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Subdivision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "PostalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryRegion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "CountryRegion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formattedAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "FormattedAddress"));
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
