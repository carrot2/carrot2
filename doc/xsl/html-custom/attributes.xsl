<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:c2="http://www.carrot2.org"
                xmlns:d="http://docbook.org/ns/docbook"
                version="1.0" exclude-result-prefixes="c2">
                
  <xsl:template match="d:link[@role = 'attribute']">
    <xsl:variable name="section-id" select="concat('section.attribute.', @linkend)" />
    <xsl:call-template name="simple.xlink">
      <xsl:with-param name="node" select="."/>
      <xsl:with-param name="linkend" select="$section-id" />
      <xsl:with-param name="content">
        <xsl:choose>
          <xsl:when test="string-length(.) > 0">
            <xsl:value-of select="string(.)" />
          </xsl:when>
          
          <xsl:otherwise>
            <xsl:value-of select="string(//d:section[@xml:id = $section-id]/d:title)" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
</xsl:stylesheet>

