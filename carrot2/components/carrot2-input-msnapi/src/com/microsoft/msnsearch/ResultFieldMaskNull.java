/**
 * ResultFieldMaskNull.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

public class ResultFieldMaskNull implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ResultFieldMaskNull(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _All = "All";
    public static final java.lang.String _Title = "Title";
    public static final java.lang.String _Description = "Description";
    public static final java.lang.String _Url = "Url";
    public static final java.lang.String _DisplayUrl = "DisplayUrl";
    public static final java.lang.String _CacheUrl = "CacheUrl";
    public static final java.lang.String _Source = "Source";
    public static final java.lang.String _SearchTags = "SearchTags";
    public static final java.lang.String _Phone = "Phone";
    public static final java.lang.String _DateTime = "DateTime";
    public static final java.lang.String _Address = "Address";
    public static final java.lang.String _Location = "Location";
    public static final java.lang.String _SearchTagsArray = "SearchTagsArray";
    public static final java.lang.String _Summary = "Summary";
    public static final java.lang.String _ResultType = "ResultType";
    public static final ResultFieldMaskNull All = new ResultFieldMaskNull(_All);
    public static final ResultFieldMaskNull Title = new ResultFieldMaskNull(_Title);
    public static final ResultFieldMaskNull Description = new ResultFieldMaskNull(_Description);
    public static final ResultFieldMaskNull Url = new ResultFieldMaskNull(_Url);
    public static final ResultFieldMaskNull DisplayUrl = new ResultFieldMaskNull(_DisplayUrl);
    public static final ResultFieldMaskNull CacheUrl = new ResultFieldMaskNull(_CacheUrl);
    public static final ResultFieldMaskNull Source = new ResultFieldMaskNull(_Source);
    public static final ResultFieldMaskNull SearchTags = new ResultFieldMaskNull(_SearchTags);
    public static final ResultFieldMaskNull Phone = new ResultFieldMaskNull(_Phone);
    public static final ResultFieldMaskNull DateTime = new ResultFieldMaskNull(_DateTime);
    public static final ResultFieldMaskNull Address = new ResultFieldMaskNull(_Address);
    public static final ResultFieldMaskNull Location = new ResultFieldMaskNull(_Location);
    public static final ResultFieldMaskNull SearchTagsArray = new ResultFieldMaskNull(_SearchTagsArray);
    public static final ResultFieldMaskNull Summary = new ResultFieldMaskNull(_Summary);
    public static final ResultFieldMaskNull ResultType = new ResultFieldMaskNull(_ResultType);
    public java.lang.String getValue() { return _value_;}
    public static ResultFieldMaskNull fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ResultFieldMaskNull enumeration = (ResultFieldMaskNull)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ResultFieldMaskNull fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ResultFieldMaskNull.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ResultFieldMask>null"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
