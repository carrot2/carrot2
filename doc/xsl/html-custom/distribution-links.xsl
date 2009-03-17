<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:c2="http://www.carrot2.org"
                xmlns:d="http://docbook.org/ns/docbook"
                version="1.0" exclude-result-prefixes="c2">
                
  <xsl:param name="dist.url" />
  <xsl:param name="carrot2.version" />
  
  <xsl:param name="carrot2.java-api.base" />
  <xsl:param name="carrot2.dcs.base" />
  <xsl:param name="carrot2.webapp.base" />
  <xsl:param name="carrot2.workbench.base" />
  <xsl:param name="carrot2.manual.base" />
  <xsl:param name="carrot2.javadoc.url" />
  
  <xsl:template match="c2:java-api-download-link">
    <a href="{$dist.url}/{$carrot2.java-api.base}-{$carrot2.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>
    
  <xsl:template match="c2:dcs-download-link">
    <a href="{$dist.url}/{$carrot2.dcs.base}-{$carrot2.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="c2:webapp-download-link">
    <a href="{$dist.url}/{$carrot2.webapp.base}-{$carrot2.version}.war"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="c2:workbench-download-link">
    <a href="{$dist.url}/{$carrot2.workbench.base}-{@os}.{@wm}.x86.zip"><xsl:apply-templates /></a>
  </xsl:template>
  
  <xsl:template match="c2:carrot2-version"><xsl:value-of select="$carrot2.version" /></xsl:template>
  
  <xsl:template match="d:link[@role = 'javadoc']">
    <xsl:variable name="class-name">
      <xsl:call-template name="from-last-substring">
        <xsl:with-param name="string" select="@linkend" />
        <xsl:with-param name="substring" select="'.'" />
      </xsl:call-template>
    </xsl:variable>
    <a href="{$carrot2.javadoc.url}/{translate(@linkend, '.$', '/.')}.html"><xsl:value-of select="$class-name" /></a>
  </xsl:template>
  
  <xsl:template name="from-last-substring">
    <xsl:param name="string" />
    <xsl:param name="substring" />
    
    <xsl:choose>
      <xsl:when test="contains($string, $substring)">
        <xsl:call-template name="from-last-substring">
          <xsl:with-param name="string" select="substring-after($string, $substring)" />
          <xsl:with-param name="substring" select="$substring" />
        </xsl:call-template>
      </xsl:when>
      
      <xsl:otherwise><xsl:value-of select="$string" /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>

