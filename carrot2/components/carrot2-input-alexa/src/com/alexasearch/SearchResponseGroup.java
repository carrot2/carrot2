/**
 * SearchResponseGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.alexasearch;

public class SearchResponseGroup implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SearchResponseGroup(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Results = "Results";
    public static final java.lang.String _Context = "Context";
    public static final java.lang.String _QueryCorrection = "QueryCorrection";
    public static final java.lang.String _QueryExpansion = "QueryExpansion";
    public static final java.lang.String _TrafficRank = "TrafficRank";
    public static final SearchResponseGroup Results = new SearchResponseGroup(_Results);
    public static final SearchResponseGroup Context = new SearchResponseGroup(_Context);
    public static final SearchResponseGroup QueryCorrection = new SearchResponseGroup(_QueryCorrection);
    public static final SearchResponseGroup QueryExpansion = new SearchResponseGroup(_QueryExpansion);
    public static final SearchResponseGroup TrafficRank = new SearchResponseGroup(_TrafficRank);
    public java.lang.String getValue() { return _value_;}
    public static SearchResponseGroup fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SearchResponseGroup enumeration = (SearchResponseGroup)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SearchResponseGroup fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(SearchResponseGroup.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://wsearch.amazonaws.com/doc/2007-03-15/", ">>Search>ResponseGroup"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
