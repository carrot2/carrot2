/**
 * SearchFlagsNull.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

public class SearchFlagsNull implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SearchFlagsNull(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _None = "None";
    public static final java.lang.String _MarkQueryWords = "MarkQueryWords";
    public static final java.lang.String _DisableSpellCorrectForSpecialWords = "DisableSpellCorrectForSpecialWords";
    public static final java.lang.String _DisableHostCollapsing = "DisableHostCollapsing";
    public static final SearchFlagsNull None = new SearchFlagsNull(_None);
    public static final SearchFlagsNull MarkQueryWords = new SearchFlagsNull(_MarkQueryWords);
    public static final SearchFlagsNull DisableSpellCorrectForSpecialWords = new SearchFlagsNull(_DisableSpellCorrectForSpecialWords);
    public static final SearchFlagsNull DisableHostCollapsing = new SearchFlagsNull(_DisableHostCollapsing);
    public java.lang.String getValue() { return _value_;}
    public static SearchFlagsNull fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SearchFlagsNull enumeration = (SearchFlagsNull)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SearchFlagsNull fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchFlagsNull.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchFlags>null"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
