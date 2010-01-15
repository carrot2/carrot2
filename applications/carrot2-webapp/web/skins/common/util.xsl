<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="UTF-8" />
              
  <xsl:strip-space elements="*" />
  
  <!-- Replaces one parameter in the provided url -->
  <xsl:template name="replace-in-url">
    <xsl:param name="url" />
    <xsl:param name="param" />
    <xsl:param name="value" />
    
    <xsl:variable name="left"><xsl:value-of select="substring-before($url, concat($param, '='))" /></xsl:variable>
    <xsl:variable name="after-param"><xsl:value-of select="substring-after($url, concat($param, '='))" /></xsl:variable>
    <xsl:variable name="right"><xsl:value-of select="substring-after(substring($after-param, 1), '&amp;')" /></xsl:variable>
    
    <xsl:value-of select="concat($left, $param, '=', $value)" /><xsl:if test="string-length($right) > 0">
      <xsl:value-of select="concat('&amp;', $right)" />
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
