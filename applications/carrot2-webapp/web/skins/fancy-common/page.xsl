<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="../common-dynamic/page.xsl" />
  
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="UTF-8" />

  <xsl:strip-space elements="*"/>

  <xsl:template match="page" mode="results">
    <div class="glow-small">
      <xsl:apply-templates select=".." mode="results.area" />
      <xsl:call-template name="glow-spans" />
    </div>
  </xsl:template>
  
  <xsl:template match="page" mode="query">
    <span id="query-glow" class="glow-small">
      <xsl:apply-templates select=".." mode="query.field" />
      <xsl:call-template name="glow-spans" />
    </span>
  </xsl:template>
  
  <xsl:template match="page" mode="search">
    <span id="search-glow" class="glow-big">
      <xsl:apply-templates select=".." mode="search.field" />
      <xsl:call-template name="glow-spans" />
    </span>
  </xsl:template>

  <xsl:template name="glow-spans">
    <span class="t"><xsl:comment></xsl:comment></span>
    <span class="l"><xsl:comment></xsl:comment></span>
    <span class="r"><xsl:comment></xsl:comment></span>
    <span class="b"><xsl:comment></xsl:comment></span>
    <span class="tl"><xsl:comment></xsl:comment></span>
    <span class="bl"><xsl:comment></xsl:comment></span>
    <span class="tr"><xsl:comment></xsl:comment></span>
    <span class="br"><xsl:comment></xsl:comment></span>
  </xsl:template>
</xsl:stylesheet>
