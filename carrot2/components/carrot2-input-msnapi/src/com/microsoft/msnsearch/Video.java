/**
 * Video.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

public class Video  implements java.io.Serializable {
    private java.lang.String playUrl;

    private java.lang.String sourceTitle;

    private java.lang.String format;

    private java.lang.Integer runTime;

    private java.lang.Integer width;

    private java.lang.Integer height;

    private java.lang.Integer fileSize;

    private com.microsoft.msnsearch.StaticThumbnail staticThumbnail;

    private com.microsoft.msnsearch.MotionThumbnail motionThumbnail;

    public Video() {
    }

    public Video(
           java.lang.String playUrl,
           java.lang.String sourceTitle,
           java.lang.String format,
           java.lang.Integer runTime,
           java.lang.Integer width,
           java.lang.Integer height,
           java.lang.Integer fileSize,
           com.microsoft.msnsearch.StaticThumbnail staticThumbnail,
           com.microsoft.msnsearch.MotionThumbnail motionThumbnail) {
           this.playUrl = playUrl;
           this.sourceTitle = sourceTitle;
           this.format = format;
           this.runTime = runTime;
           this.width = width;
           this.height = height;
           this.fileSize = fileSize;
           this.staticThumbnail = staticThumbnail;
           this.motionThumbnail = motionThumbnail;
    }


    /**
     * Gets the playUrl value for this Video.
     * 
     * @return playUrl
     */
    public java.lang.String getPlayUrl() {
        return playUrl;
    }


    /**
     * Sets the playUrl value for this Video.
     * 
     * @param playUrl
     */
    public void setPlayUrl(java.lang.String playUrl) {
        this.playUrl = playUrl;
    }


    /**
     * Gets the sourceTitle value for this Video.
     * 
     * @return sourceTitle
     */
    public java.lang.String getSourceTitle() {
        return sourceTitle;
    }


    /**
     * Sets the sourceTitle value for this Video.
     * 
     * @param sourceTitle
     */
    public void setSourceTitle(java.lang.String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }


    /**
     * Gets the format value for this Video.
     * 
     * @return format
     */
    public java.lang.String getFormat() {
        return format;
    }


    /**
     * Sets the format value for this Video.
     * 
     * @param format
     */
    public void setFormat(java.lang.String format) {
        this.format = format;
    }


    /**
     * Gets the runTime value for this Video.
     * 
     * @return runTime
     */
    public java.lang.Integer getRunTime() {
        return runTime;
    }


    /**
     * Sets the runTime value for this Video.
     * 
     * @param runTime
     */
    public void setRunTime(java.lang.Integer runTime) {
        this.runTime = runTime;
    }


    /**
     * Gets the width value for this Video.
     * 
     * @return width
     */
    public java.lang.Integer getWidth() {
        return width;
    }


    /**
     * Sets the width value for this Video.
     * 
     * @param width
     */
    public void setWidth(java.lang.Integer width) {
        this.width = width;
    }


    /**
     * Gets the height value for this Video.
     * 
     * @return height
     */
    public java.lang.Integer getHeight() {
        return height;
    }


    /**
     * Sets the height value for this Video.
     * 
     * @param height
     */
    public void setHeight(java.lang.Integer height) {
        this.height = height;
    }


    /**
     * Gets the fileSize value for this Video.
     * 
     * @return fileSize
     */
    public java.lang.Integer getFileSize() {
        return fileSize;
    }


    /**
     * Sets the fileSize value for this Video.
     * 
     * @param fileSize
     */
    public void setFileSize(java.lang.Integer fileSize) {
        this.fileSize = fileSize;
    }


    /**
     * Gets the staticThumbnail value for this Video.
     * 
     * @return staticThumbnail
     */
    public com.microsoft.msnsearch.StaticThumbnail getStaticThumbnail() {
        return staticThumbnail;
    }


    /**
     * Sets the staticThumbnail value for this Video.
     * 
     * @param staticThumbnail
     */
    public void setStaticThumbnail(com.microsoft.msnsearch.StaticThumbnail staticThumbnail) {
        this.staticThumbnail = staticThumbnail;
    }


    /**
     * Gets the motionThumbnail value for this Video.
     * 
     * @return motionThumbnail
     */
    public com.microsoft.msnsearch.MotionThumbnail getMotionThumbnail() {
        return motionThumbnail;
    }


    /**
     * Sets the motionThumbnail value for this Video.
     * 
     * @param motionThumbnail
     */
    public void setMotionThumbnail(com.microsoft.msnsearch.MotionThumbnail motionThumbnail) {
        this.motionThumbnail = motionThumbnail;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Video)) return false;
        Video other = (Video) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.playUrl==null && other.getPlayUrl()==null) || 
             (this.playUrl!=null &&
              this.playUrl.equals(other.getPlayUrl()))) &&
            ((this.sourceTitle==null && other.getSourceTitle()==null) || 
             (this.sourceTitle!=null &&
              this.sourceTitle.equals(other.getSourceTitle()))) &&
            ((this.format==null && other.getFormat()==null) || 
             (this.format!=null &&
              this.format.equals(other.getFormat()))) &&
            ((this.runTime==null && other.getRunTime()==null) || 
             (this.runTime!=null &&
              this.runTime.equals(other.getRunTime()))) &&
            ((this.width==null && other.getWidth()==null) || 
             (this.width!=null &&
              this.width.equals(other.getWidth()))) &&
            ((this.height==null && other.getHeight()==null) || 
             (this.height!=null &&
              this.height.equals(other.getHeight()))) &&
            ((this.fileSize==null && other.getFileSize()==null) || 
             (this.fileSize!=null &&
              this.fileSize.equals(other.getFileSize()))) &&
            ((this.staticThumbnail==null && other.getStaticThumbnail()==null) || 
             (this.staticThumbnail!=null &&
              this.staticThumbnail.equals(other.getStaticThumbnail()))) &&
            ((this.motionThumbnail==null && other.getMotionThumbnail()==null) || 
             (this.motionThumbnail!=null &&
              this.motionThumbnail.equals(other.getMotionThumbnail())));
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
        if (getPlayUrl() != null) {
            _hashCode += getPlayUrl().hashCode();
        }
        if (getSourceTitle() != null) {
            _hashCode += getSourceTitle().hashCode();
        }
        if (getFormat() != null) {
            _hashCode += getFormat().hashCode();
        }
        if (getRunTime() != null) {
            _hashCode += getRunTime().hashCode();
        }
        if (getWidth() != null) {
            _hashCode += getWidth().hashCode();
        }
        if (getHeight() != null) {
            _hashCode += getHeight().hashCode();
        }
        if (getFileSize() != null) {
            _hashCode += getFileSize().hashCode();
        }
        if (getStaticThumbnail() != null) {
            _hashCode += getStaticThumbnail().hashCode();
        }
        if (getMotionThumbnail() != null) {
            _hashCode += getMotionThumbnail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Video.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Video"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("playUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "PlayUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceTitle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceTitle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("format");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Format"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("runTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "RunTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("width");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Width"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("height");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Height"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "FileSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("staticThumbnail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "StaticThumbnail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "StaticThumbnail"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("motionThumbnail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "MotionThumbnail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "MotionThumbnail"));
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
