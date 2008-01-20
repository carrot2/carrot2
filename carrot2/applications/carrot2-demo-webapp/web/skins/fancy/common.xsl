<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="customize.xsl" />

  <xsl:variable name="contextPath">
    <xsl:value-of select="normalize-space(processing-instruction('context-path'))" />
  </xsl:variable>

  <xsl:variable name="skinuri">
    <xsl:value-of select="normalize-space(processing-instruction('skin-uri'))" />
  </xsl:variable>

  <xsl:template match="/">
<html>
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

    <xsl:call-template name="body-insert" />

    <xsl:apply-templates />

    <xsl:call-template name="body-end-insert" />

    <xsl:if test="string-length($ga-code)">
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("<xsl:value-of select="$ga-code" />");
pageTracker._initData();
pageTracker._trackPageview();
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

  <!-- Empty head insert -->
  <xsl:template name="head-insert" />

  <!-- Empty body insert -->
  <xsl:template name="body-insert" />
  <xsl:template name="body-end-insert" />

  <!-- Certain HTML elements -->
  <xsl:template match="p|table|tr|td|a|b|ul|br|img|div|select|option|span|li|form|script">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
