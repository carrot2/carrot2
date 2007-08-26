<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="utf-8" />

  <!-- 
       Parameters passed by Carrot2 and corresponding to Solr -> Carrot2 field mappings.
   -->
  <xsl:param name="solr.id-field">id</xsl:param>
  <xsl:param name="solr.title-field">title</xsl:param>
  <xsl:param name="solr.snippet-field">description</xsl:param>
  <xsl:param name="solr.url-field">url</xsl:param>

  <xsl:template match="/">
    <searchresult>
      <query><xsl:value-of select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" /></query>
      <xsl:apply-templates select="/response/result/doc" />
    </searchresult>
  </xsl:template>

  <xsl:template match="doc">
    <document id="{*[@name=$solr.id-field]}">
      <title><xsl:value-of select="*[@name=$solr.title-field]" /></title>
      <snippet><xsl:value-of select="*[@name=$solr.snippet-field]" /></snippet>
      <url><xsl:value-of select="*[@name=$solr.url-field]" /></url>
    </document>
  </xsl:template>

</xsl:stylesheet>
