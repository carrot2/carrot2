<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="customize.xsl" />
  <xsl:param name="divider" select="1" />

  <xsl:variable name="contextPath">
    <xsl:value-of select="normalize-space(processing-instruction('context-path'))" />
  </xsl:variable>

  <xsl:variable name="skinuri">
    <xsl:value-of select="normalize-space(processing-instruction('skin-uri'))" />
  </xsl:variable>

  <xsl:template match="/">
<html style="height: 100%" class="outside-back-color">
  <head>
    <title>Carrot Clustering Engine</title>
    
    <link rel="stylesheet" type="text/css" href="{$skinuri}/css/common.css" />
    <xsl:call-template name="head-insert" />
  </head>

  <xsl:variable name="onload"><xsl:call-template name="on-load" /></xsl:variable>

  <body style="height: 100%">
    <xsl:if test="string-length($onload) > 0">
      <xsl:attribute name="onload"><xsl:value-of select="$onload" /></xsl:attribute>
    </xsl:if>

    <xsl:apply-templates />
    
    <xsl:if test="string-length($ga-code)">
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "<xsl:value-of select="$ga-code" />";
urchinTracker();
</script>
    </xsl:if>
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
