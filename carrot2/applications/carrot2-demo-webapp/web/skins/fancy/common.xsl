<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:param name="divider" select="1" />
  
  <xsl:variable name="skinuri">
    <xsl:value-of select="normalize-space(processing-instruction('skin-uri'))" />
  </xsl:variable>

  <xsl:template match="/">
<html style="height: 100%" class="outside-back-color">
  <head>
    <title>Carrot Clustering Engine</title>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="{$skinuri}/css/common.css" />
    <xsl:call-template name="head-insert" />
  </head>

  <xsl:variable name="onload"><xsl:call-template name="on-load" /></xsl:variable>

  <body style="height: 100%">
    <xsl:if test="string-length($onload) > 0">
      <xsl:attribute name="onload"><xsl:value-of select="$onload" /></xsl:attribute>
    </xsl:if>

    <xsl:apply-templates />
  </body>
</html>
  </xsl:template>

  <xsl:template match="page">
    <xsl:apply-templates />
  </xsl:template>

  <!-- Empty onload -->
  <xsl:template name="on-load" />

  <!-- Certain HTML elements -->
  <xsl:template match="p|table|tr|td|a|b|ul|br|img|div|select|option|span|li|form|script">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
