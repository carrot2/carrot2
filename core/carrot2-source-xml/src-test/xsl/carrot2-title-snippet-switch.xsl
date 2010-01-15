<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="UTF-8" />

  <xsl:template match="/">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="document">
    <document id="{@id}">
      <title><xsl:value-of select="snippet" /></title>
      <snippet><xsl:value-of select="title" /></snippet>
    </document>
  </xsl:template>

  <!-- Certain elements -->
  <xsl:template match="searchresult|title|snippet|url|query">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
