<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="UTF-8" />

  <xsl:template match="/">
    <searchresult>
      <xsl:apply-templates select="/results/result" />
    </searchresult>
  </xsl:template>

  <xsl:template match="result">
    <document>
      <title><xsl:value-of select="title" /></title>
      <snippet><xsl:value-of select="snippet" /></snippet>
      <url><xsl:value-of select="cache_url" /></url>
    </document>
  </xsl:template>
</xsl:stylesheet>
