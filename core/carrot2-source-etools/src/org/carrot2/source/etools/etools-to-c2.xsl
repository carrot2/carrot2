<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="UTF-8" />

  <xsl:template match="/">
    <searchresult>
      <query><xsl:value-of select="/result/meta/request/@query" /></query>
      <xsl:apply-templates select="/result/records" />
    </searchresult>
  </xsl:template>

  <xsl:template match="record">
    <document>
      <title><xsl:value-of select="title" /></title>
      <snippet><xsl:value-of select="text" /></snippet>
      <url><xsl:value-of select="url" /></url>
      <sources><xsl:apply-templates select="sources" /></sources>
    </document>
  </xsl:template>
  
  <xsl:template match="record/sources/source">
  	<source><xsl:apply-templates /></source>
  </xsl:template>

</xsl:stylesheet>
