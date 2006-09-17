
// Generated file. Do not edit by hand.

package com.microsoft.msnsearch;

public class SourceType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SourceType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Web = "Web";
    public static final java.lang.String _News = "News";
    public static final java.lang.String _Ads = "Ads";
    public static final java.lang.String _InlineAnswers = "InlineAnswers";
    public static final java.lang.String _PhoneBook = "PhoneBook";
    public static final java.lang.String _WordBreaker = "WordBreaker";
    public static final java.lang.String _Spelling = "Spelling";
    public static final SourceType Web = new SourceType(_Web);
    public static final SourceType News = new SourceType(_News);
    public static final SourceType Ads = new SourceType(_Ads);
    public static final SourceType InlineAnswers = new SourceType(_InlineAnswers);
    public static final SourceType PhoneBook = new SourceType(_PhoneBook);
    public static final SourceType WordBreaker = new SourceType(_WordBreaker);
    public static final SourceType Spelling = new SourceType(_Spelling);
    public java.lang.String getValue() { return _value_;}
    public static SourceType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SourceType enumeration = (SourceType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SourceType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(SourceType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
