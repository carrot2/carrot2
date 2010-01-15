<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="UTF-8" />

  <xsl:param name="id-field">id</xsl:param>
  <xsl:param name="title-field">title</xsl:param>
  <xsl:param name="snippet-field">description</xsl:param>
  <xsl:param name="url-field">url</xsl:param>

  <xsl:template match="/">
    <searchresult>
      <query><xsl:value-of select="/response/query" /></query>
      <xsl:apply-templates select="/response/doc" />
    </searchresult>
  </xsl:template>

  <xsl:template match="doc">
    <document id="{*[@name=$id-field]}">
      <title><xsl:value-of select="*[@name=$title-field]" /></title>
      <snippet><xsl:value-of select="*[@name=$snippet-field]" /></snippet>
      <url><xsl:value-of select="*[@name=$url-field]" /></url>
    </document>
  </xsl:template>
</xsl:stylesheet>
