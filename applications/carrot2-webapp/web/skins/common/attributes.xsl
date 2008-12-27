<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="utf-8" />
              
  <xsl:strip-space elements="*" />
  
  <!--
       Renders HTML form elements for editing attributes of a specific document source. 
    -->
  <xsl:template match="attribute-metadata">
    <xsl:for-each select="attribute">
      <xsl:variable name="key" select="@key" />
      <div>
        <label title="{metadata/title}">
        <xsl:value-of select="metadata/label" />
        
        <xsl:variable name="current-value"><xsl:call-template name="attribute-value"><xsl:with-param name="key" select="$key" /></xsl:call-template></xsl:variable>
        <xsl:choose>
          <xsl:when test="allowed-values">
            <select name="{@key}">
              <xsl:if test="not(@default)">
                <option value=""></option>
              </xsl:if>
              <xsl:for-each select="allowed-values/value">
                <xsl:variable name="value" select="string(.)" />
                <option value="{.}">
                  <xsl:if test="$current-value = $value">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@label" />
                </option>
              </xsl:for-each>
            </select>
          </xsl:when>
          <xsl:when test="@type = 'java.lang.Boolean'">
            <input name="{@key}" type="checkbox" value="true">
              <xsl:if test="$current-value = 'true'">
                <xsl:attribute name="checked">checked</xsl:attribute>
              </xsl:if>
            </input>
          </xsl:when>
          <xsl:otherwise>
            <input name="{@key}" type="text" value="{$current-value}" />
          </xsl:otherwise>
        </xsl:choose>
        </label>
      </div>
    </xsl:for-each>
    <xsl:if test="count(attribute) = 0">
      <div id="no-advanced-options">No advanced options</div>
    </xsl:if>
  </xsl:template>

  <!--
       Retrieves attribute value from request, if not present, returns the initialization
       time default. If initialization default is not set, returns the built-in default. 
    -->  
  <xsl:template name="attribute-value">
    <xsl:param name="key" />
    <xsl:choose>
      <xsl:when test="/page/request/parameter[@key = $key]/value/@value">
        <xsl:value-of select="/page/request/parameter[@key = $key]/value/@value" />
      </xsl:when>
      
      <xsl:when test="//attribute-metadata/init-value[@key = $key]/value/@value">
        <xsl:value-of select="//attribute-metadata/init-value[@key = $key]/value/@value" />
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:value-of select="//attribute-metadata/attribute[@key = $key]/@default" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
