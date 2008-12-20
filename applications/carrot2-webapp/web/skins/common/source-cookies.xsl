<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    Overrides the default templates for outputting source tabs to use
    the data (order, active source) from the user's cookies.
    -->
  
  <!-- Value of the cookie containing the user's active source id-->
  <xsl:variable name="user-active-source" select="/page/request/cookie[@key = 'active-source']/value/@value" />
  
  <!-- Active source, if user cookie not present, default is used -->
  <xsl:variable name="active-source-id">
    <xsl:choose>
      <xsl:when test="string-length(/page/request/@query) = 0 and $user-active-source and /page/config/components/sources/source[@id = $user-active-source]">
        <xsl:value-of select="$user-active-source" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="/page/request/@source" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:template match="page" mode="active-source"><xsl:value-of select="$active-source-id" /></xsl:template>
  
  <!-- 
    Overrides the default template for tabs, puts tabs in the order 
    determined by the cookie from the user 
    -->  
  <xsl:template match="page" mode="sources-internal">
    <xsl:variable name="source-order" select="/page/request/cookie[@key = 'source-order']/value/@value" />
    <xsl:choose>
      <xsl:when test="string-length($source-order) > 0">
        <xsl:call-template name="user-order-source">
          <xsl:with-param name="source-order" select="$source-order" />
        </xsl:call-template>
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:call-template name="user-order-source">
          <xsl:with-param name="source-order"><xsl:apply-templates select="config/components/sources/source" mode="build-order" /></xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="source[position() = last()]" mode="build-order"><xsl:value-of select="@id" /></xsl:template>
  <xsl:template match="source" mode="build-order"><xsl:value-of select="@id" />*</xsl:template>
  
  <!-- 
    Overrides the default template determining if the first source is selected, 
    computes the value based on the cookie from the user. 
    -->  
  <xsl:template match="page" mode="is-first-source-active">
    <xsl:variable name="source-order" select="/page/request/cookie[@key = 'source-order']/value/@value" />
    <xsl:choose>
      <xsl:when test="contains($source-order, '*')">
        <xsl:if test="substring-before($source-order, '*') = $active-source-id">yes</xsl:if>    
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select=".." mode="is-first-source-active-internal" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- 
    A recursive template for parsing the ":"-separated cookie value.
    -->  
  <xsl:template name="user-order-source">
    <xsl:param name="source-order"/>
    <xsl:choose>
      <xsl:when test="contains($source-order,'*')">
        <xsl:variable name="source-id" select="substring-before($source-order,'*')" />
        <xsl:variable name="next-source-id">
          <xsl:choose>
            <xsl:when test="contains(substring-after($source-order, '*'), '*')"><xsl:value-of select="substring-before(substring-after($source-order, '*'), '*')" /></xsl:when>
            <xsl:otherwise><xsl:value-of select="substring-after($source-order, '*')" /></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
       
        <xsl:call-template name="source">
          <xsl:with-param name="source-id" select="$source-id" />
          <xsl:with-param name="is-active" select="$source-id = $active-source-id" />
          <xsl:with-param name="is-before-active" select="$next-source-id = $active-source-id" />
        </xsl:call-template>
        
        <xsl:call-template name="user-order-source">
          <xsl:with-param name="source-order" select="substring-after($source-order,'*')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="source">
          <xsl:with-param name="source-id" select="$source-order" />
          <xsl:with-param name="is-last" select="'true'" />
          <xsl:with-param name="is-active" select="$source-order = $active-source-id" />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
