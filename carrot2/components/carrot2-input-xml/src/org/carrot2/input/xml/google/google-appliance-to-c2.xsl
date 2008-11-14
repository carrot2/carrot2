<?xml version="1.0" encoding="UTF-8" ?>

<!--
	Converts Google Appliance XML format to C2 XML format.
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="utf-8" />

  <xsl:template match="/">
    <searchresult>
      <query><xsl:value-of select="/GSP/Q" /></query>
      <xsl:apply-templates select="/GSP/RES/R" />
    </searchresult>
  </xsl:template>

  <xsl:template match="R">
    <document id="{@N}">
      <url><xsl:value-of select="U" /></url>
      <title><xsl:value-of select="T" /></title>
      <snippet><xsl:value-of select="S" /></snippet>
    </document>
  </xsl:template>

</xsl:stylesheet>
