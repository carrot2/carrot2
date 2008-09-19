<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="no"
       encoding="utf-8" cdata-section-elements="programlisting" />
       
  <xsl:template match="/">
    <test>
      <xsl:apply-templates select="x" />
    </test>
  </xsl:template>

  <xsl:template match="ul">
    <li><xsl:apply-templates /></li>
  </xsl:template>
</xsl:stylesheet>
