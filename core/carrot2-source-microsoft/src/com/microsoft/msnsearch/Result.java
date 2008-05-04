/**
 * Result.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Sep 12, 2006 (01:08:05 CEST) WSDL2Java emitter.
 */

package com.microsoft.msnsearch;

public class Result  implements java.io.Serializable {
    private java.lang.String title;

    private java.lang.String description;

    private java.lang.String url;

    private java.lang.String displayUrl;

    private java.lang.String cacheUrl;

    private java.lang.String source;

    private java.lang.String searchTags;

    private java.lang.String phone;

    private com.microsoft.msnsearch.DateTime dateTime;

    private com.microsoft.msnsearch.Address address;

    private com.microsoft.msnsearch.Location location;

    private com.microsoft.msnsearch.SearchTag[] searchTagsArray;

    private java.lang.String summary;

    private java.lang.String resultType;

    private com.microsoft.msnsearch.Image image;

    private com.microsoft.msnsearch.Video video;

    public Result() {
    }

    public Result(
           java.lang.String title,
           java.lang.String description,
           java.lang.String url,
           java.lang.String displayUrl,
           java.lang.String cacheUrl,
           java.lang.String source,
           java.lang.String searchTags,
           java.lang.String phone,
           com.microsoft.msnsearch.DateTime dateTime,
           com.microsoft.msnsearch.Address address,
           com.microsoft.msnsearch.Location location,
           com.microsoft.msnsearch.SearchTag[] searchTagsArray,
           java.lang.String summary,
           java.lang.String resultType,
           com.microsoft.msnsearch.Image image,
           com.microsoft.msnsearch.Video video) {
           this.title = title;
           this.description = description;
           this.url = url;
           this.displayUrl = displayUrl;
           this.cacheUrl = cacheUrl;
           this.source = source;
           this.searchTags = searchTags;
           this.phone = phone;
           this.dateTime = dateTime;
           this.address = address;
           this.location = location;
           this.searchTagsArray = searchTagsArray;
           this.summary = summary;
           this.resultType = resultType;
           this.image = image;
           this.video = video;
    }


    /**
     * Gets the title value for this Result.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this Result.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the description value for this Result.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Result.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the url value for this Result.
     * 
     * @return url
     */
    public java.lang.String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this Result.
     * 
     * @param url
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }


    /**
     * Gets the displayUrl value for this Result.
     * 
     * @return displayUrl
     */
    public java.lang.String getDisplayUrl() {
        return displayUrl;
    }


    /**
     * Sets the displayUrl value for this Result.
     * 
     * @param displayUrl
     */
    public void setDisplayUrl(java.lang.String displayUrl) {
        this.displayUrl = displayUrl;
    }


    /**
     * Gets the cacheUrl value for this Result.
     * 
     * @return cacheUrl
     */
    public java.lang.String getCacheUrl() {
        return cacheUrl;
    }


    /**
     * Sets the cacheUrl value for this Result.
     * 
     * @param cacheUrl
     */
    public void setCacheUrl(java.lang.String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }


    /**
     * Gets the source value for this Result.
     * 
     * @return source
     */
    public java.lang.String getSource() {
        return source;
    }


    /**
     * Sets the source value for this Result.
     * 
     * @param source
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }


    /**
     * Gets the searchTags value for this Result.
     * 
     * @return searchTags
     */
    public java.lang.String getSearchTags() {
        return searchTags;
    }


    /**
     * Sets the searchTags value for this Result.
     * 
     * @param searchTags
     */
    public void setSearchTags(java.lang.String searchTags) {
        this.searchTags = searchTags;
    }


    /**
     * Gets the phone value for this Result.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this Result.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the dateTime value for this Result.
     * 
     * @return dateTime
     */
    public com.microsoft.msnsearch.DateTime getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this Result.
     * 
     * @param dateTime
     */
    public void setDateTime(com.microsoft.msnsearch.DateTime dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the address value for this Result.
     * 
     * @return address
     */
    public com.microsoft.msnsearch.Address getAddress() {
        return address;
    }


    /**
     * Sets the address value for this Result.
     * 
     * @param address
     */
    public void setAddress(com.microsoft.msnsearch.Address address) {
        this.address = address;
    }


    /**
     * Gets the location value for this Result.
     * 
     * @return location
     */
    public com.microsoft.msnsearch.Location getLocation() {
        return location;
    }


    /**
     * Sets the location value for this Result.
     * 
     * @param location
     */
    public void setLocation(com.microsoft.msnsearch.Location location) {
        this.location = location;
    }


    /**
     * Gets the searchTagsArray value for this Result.
     * 
     * @return searchTagsArray
     */
    public com.microsoft.msnsearch.SearchTag[] getSearchTagsArray() {
        return searchTagsArray;
    }


    /**
     * Sets the searchTagsArray value for this Result.
     * 
     * @param searchTagsArray
     */
    public void setSearchTagsArray(com.microsoft.msnsearch.SearchTag[] searchTagsArray) {
        this.searchTagsArray = searchTagsArray;
    }


    /**
     * Gets the summary value for this Result.
     * 
     * @return summary
     */
    public java.lang.String getSummary() {
        return summary;
    }


    /**
     * Sets the summary value for this Result.
     * 
     * @param summary
     */
    public void setSummary(java.lang.String summary) {
        this.summary = summary;
    }


    /**
     * Gets the resultType value for this Result.
     * 
     * @return resultType
     */
    public java.lang.String getResultType() {
        return resultType;
    }


    /**
     * Sets the resultType value for this Result.
     * 
     * @param resultType
     */
    public void setResultType(java.lang.String resultType) {
        this.resultType = resultType;
    }


    /**
     * Gets the image value for this Result.
     * 
     * @return image
     */
    public com.microsoft.msnsearch.Image getImage() {
        return image;
    }


    /**
     * Sets the image value for this Result.
     * 
     * @param image
     */
    public void setImage(com.microsoft.msnsearch.Image image) {
        this.image = image;
    }


    /**
     * Gets the video value for this Result.
     * 
     * @return video
     */
    public com.microsoft.msnsearch.Video getVideo() {
        return video;
    }


    /**
     * Sets the video value for this Result.
     * 
     * @param video
     */
    public void setVideo(com.microsoft.msnsearch.Video video) {
        this.video = video;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Result)) return false;
        Result other = (Result) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.url==null && other.getUrl()==null) || 
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.displayUrl==null && other.getDisplayUrl()==null) || 
             (this.displayUrl!=null &&
              this.displayUrl.equals(other.getDisplayUrl()))) &&
            ((this.cacheUrl==null && other.getCacheUrl()==null) || 
             (this.cacheUrl!=null &&
              this.cacheUrl.equals(other.getCacheUrl()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.searchTags==null && other.getSearchTags()==null) || 
             (this.searchTags!=null &&
              this.searchTags.equals(other.getSearchTags()))) &&
            ((this.phone==null && other.getPhone()==null) || 
             (this.phone!=null &&
              this.phone.equals(other.getPhone()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.searchTagsArray==null && other.getSearchTagsArray()==null) || 
             (this.searchTagsArray!=null &&
              java.util.Arrays.equals(this.searchTagsArray, other.getSearchTagsArray()))) &&
            ((this.summary==null && other.getSummary()==null) || 
             (this.summary!=null &&
              this.summary.equals(other.getSummary()))) &&
            ((this.resultType==null && other.getResultType()==null) || 
             (this.resultType!=null &&
              this.resultType.equals(other.getResultType()))) &&
            ((this.image==null && other.getImage()==null) || 
             (this.image!=null &&
              this.image.equals(other.getImage()))) &&
            ((this.video==null && other.getVideo()==null) || 
             (this.video!=null &&
              this.video.equals(other.getVideo())));
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
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getDisplayUrl() != null) {
            _hashCode += getDisplayUrl().hashCode();
        }
        if (getCacheUrl() != null) {
            _hashCode += getCacheUrl().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getSearchTags() != null) {
            _hashCode += getSearchTags().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getSearchTagsArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSearchTagsArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSearchTagsArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSummary() != null) {
            _hashCode += getSummary().hashCode();
        }
        if (getResultType() != null) {
            _hashCode += getResultType().hashCode();
        }
        if (getImage() != null) {
            _hashCode += getImage().hashCode();
        }
        if (getVideo() != null) {
            _hashCode += getVideo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Result.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Result"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "DisplayUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cacheUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "CacheUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchTags");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchTags"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "DateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "DateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Address"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Location"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchTagsArray");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchTagsArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchTag"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SearchTag"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("summary");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Summary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ResultType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("image");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Image"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Image"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("video");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Video"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Video"));
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
