<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="utf-8" />

  <xsl:template match="/">
    <searchresult>
      <query><xsl:value-of select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" /></query>
      <xsl:apply-templates select="/response/result/doc" />
    </searchresult>
  </xsl:template>

  <xsl:template match="doc">
    <document id="{str[@name='id']}">
      <title><xsl:value-of select="str[@name='name']" /></title>
      <snippet><xsl:value-of select="arr[@name='features']/str" /></snippet>
      <url>http://id-<xsl:value-of select="str[@name='id']" /></url>
    </document>
  </xsl:template>

</xsl:stylesheet>
