
// Generated file. Do not edit by hand.

package com.microsoft.msnsearch;

@SuppressWarnings({ "unchecked", "unused", "serial" })
public class Image  implements java.io.Serializable {
    private java.lang.String imageURL;

    private java.lang.Integer imageWidth;

    private java.lang.Integer imageHeight;

    private java.lang.Integer imageFileSize;

    private java.lang.String thumbnailURL;

    private java.lang.Integer thumbnailWidth;

    private java.lang.Integer thumbnailHeight;

    private java.lang.Integer thumbnailFileSize;

    public Image() {
    }

    public Image(
           java.lang.String imageURL,
           java.lang.Integer imageWidth,
           java.lang.Integer imageHeight,
           java.lang.Integer imageFileSize,
           java.lang.String thumbnailURL,
           java.lang.Integer thumbnailWidth,
           java.lang.Integer thumbnailHeight,
           java.lang.Integer thumbnailFileSize) {
           this.imageURL = imageURL;
           this.imageWidth = imageWidth;
           this.imageHeight = imageHeight;
           this.imageFileSize = imageFileSize;
           this.thumbnailURL = thumbnailURL;
           this.thumbnailWidth = thumbnailWidth;
           this.thumbnailHeight = thumbnailHeight;
           this.thumbnailFileSize = thumbnailFileSize;
    }


    /**
     * Gets the imageURL value for this Image.
     * 
     * @return imageURL
     */
    public java.lang.String getImageURL() {
        return imageURL;
    }


    /**
     * Sets the imageURL value for this Image.
     * 
     * @param imageURL
     */
    public void setImageURL(java.lang.String imageURL) {
        this.imageURL = imageURL;
    }


    /**
     * Gets the imageWidth value for this Image.
     * 
     * @return imageWidth
     */
    public java.lang.Integer getImageWidth() {
        return imageWidth;
    }


    /**
     * Sets the imageWidth value for this Image.
     * 
     * @param imageWidth
     */
    public void setImageWidth(java.lang.Integer imageWidth) {
        this.imageWidth = imageWidth;
    }


    /**
     * Gets the imageHeight value for this Image.
     * 
     * @return imageHeight
     */
    public java.lang.Integer getImageHeight() {
        return imageHeight;
    }


    /**
     * Sets the imageHeight value for this Image.
     * 
     * @param imageHeight
     */
    public void setImageHeight(java.lang.Integer imageHeight) {
        this.imageHeight = imageHeight;
    }


    /**
     * Gets the imageFileSize value for this Image.
     * 
     * @return imageFileSize
     */
    public java.lang.Integer getImageFileSize() {
        return imageFileSize;
    }


    /**
     * Sets the imageFileSize value for this Image.
     * 
     * @param imageFileSize
     */
    public void setImageFileSize(java.lang.Integer imageFileSize) {
        this.imageFileSize = imageFileSize;
    }


    /**
     * Gets the thumbnailURL value for this Image.
     * 
     * @return thumbnailURL
     */
    public java.lang.String getThumbnailURL() {
        return thumbnailURL;
    }


    /**
     * Sets the thumbnailURL value for this Image.
     * 
     * @param thumbnailURL
     */
    public void setThumbnailURL(java.lang.String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }


    /**
     * Gets the thumbnailWidth value for this Image.
     * 
     * @return thumbnailWidth
     */
    public java.lang.Integer getThumbnailWidth() {
        return thumbnailWidth;
    }


    /**
     * Sets the thumbnailWidth value for this Image.
     * 
     * @param thumbnailWidth
     */
    public void setThumbnailWidth(java.lang.Integer thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }


    /**
     * Gets the thumbnailHeight value for this Image.
     * 
     * @return thumbnailHeight
     */
    public java.lang.Integer getThumbnailHeight() {
        return thumbnailHeight;
    }


    /**
     * Sets the thumbnailHeight value for this Image.
     * 
     * @param thumbnailHeight
     */
    public void setThumbnailHeight(java.lang.Integer thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }


    /**
     * Gets the thumbnailFileSize value for this Image.
     * 
     * @return thumbnailFileSize
     */
    public java.lang.Integer getThumbnailFileSize() {
        return thumbnailFileSize;
    }


    /**
     * Sets the thumbnailFileSize value for this Image.
     * 
     * @param thumbnailFileSize
     */
    public void setThumbnailFileSize(java.lang.Integer thumbnailFileSize) {
        this.thumbnailFileSize = thumbnailFileSize;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Image)) return false;
        Image other = (Image) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.imageURL==null && other.getImageURL()==null) || 
             (this.imageURL!=null &&
              this.imageURL.equals(other.getImageURL()))) &&
            ((this.imageWidth==null && other.getImageWidth()==null) || 
             (this.imageWidth!=null &&
              this.imageWidth.equals(other.getImageWidth()))) &&
            ((this.imageHeight==null && other.getImageHeight()==null) || 
             (this.imageHeight!=null &&
              this.imageHeight.equals(other.getImageHeight()))) &&
            ((this.imageFileSize==null && other.getImageFileSize()==null) || 
             (this.imageFileSize!=null &&
              this.imageFileSize.equals(other.getImageFileSize()))) &&
            ((this.thumbnailURL==null && other.getThumbnailURL()==null) || 
             (this.thumbnailURL!=null &&
              this.thumbnailURL.equals(other.getThumbnailURL()))) &&
            ((this.thumbnailWidth==null && other.getThumbnailWidth()==null) || 
             (this.thumbnailWidth!=null &&
              this.thumbnailWidth.equals(other.getThumbnailWidth()))) &&
            ((this.thumbnailHeight==null && other.getThumbnailHeight()==null) || 
             (this.thumbnailHeight!=null &&
              this.thumbnailHeight.equals(other.getThumbnailHeight()))) &&
            ((this.thumbnailFileSize==null && other.getThumbnailFileSize()==null) || 
             (this.thumbnailFileSize!=null &&
              this.thumbnailFileSize.equals(other.getThumbnailFileSize())));
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
        if (getImageURL() != null) {
            _hashCode += getImageURL().hashCode();
        }
        if (getImageWidth() != null) {
            _hashCode += getImageWidth().hashCode();
        }
        if (getImageHeight() != null) {
            _hashCode += getImageHeight().hashCode();
        }
        if (getImageFileSize() != null) {
            _hashCode += getImageFileSize().hashCode();
        }
        if (getThumbnailURL() != null) {
            _hashCode += getThumbnailURL().hashCode();
        }
        if (getThumbnailWidth() != null) {
            _hashCode += getThumbnailWidth().hashCode();
        }
        if (getThumbnailHeight() != null) {
            _hashCode += getThumbnailHeight().hashCode();
        }
        if (getThumbnailFileSize() != null) {
            _hashCode += getThumbnailFileSize().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Image.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Image"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ImageURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageWidth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ImageWidth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ImageHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageFileSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ImageFileSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thumbnailURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ThumbnailURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thumbnailWidth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ThumbnailWidth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thumbnailHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ThumbnailHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thumbnailFileSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ThumbnailFileSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
