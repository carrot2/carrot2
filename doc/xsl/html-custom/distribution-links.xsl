<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:c2="http://www.carrot2.org"
                version="1.0" exclude-result-prefixes="c2">
                
  <xsl:param name="dist.url" />
  <xsl:param name="carrot2.version" />
  
  <xsl:param name="carrot2.java-api.base" />
  <xsl:param name="carrot2.dcs.base" />
  <xsl:param name="carrot2.webapp.base" />
  <xsl:param name="carrot2.workbench.base" />
  <xsl:param name="carrot2.manual.base" />
  
  <xsl:template match="c2:java-api-download-link">
    <a href="{$dist.url}/{$carrot2.java-api.base}-{$carrot2.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>
    
  <xsl:template match="c2:dcs-download-link">
    <a href="{$dist.url}/{$carrot2.dcs.base}-{$carrot2.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="c2:webapp-download-link">
    <a href="{$dist.url}/{$carrot2.dcs.base}-{$carrot2.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="c2:workbench-download-link">
    <a href="{$dist.url}/{$carrot2.workbench.base}-{@os}.{@wm}.x86.zip"><xsl:apply-templates /></a>
  </xsl:template>
  
  <xsl:template match="c2:carrot2-version"><xsl:value-of select="$carrot2.version" /></xsl:template>
</xsl:stylesheet>

