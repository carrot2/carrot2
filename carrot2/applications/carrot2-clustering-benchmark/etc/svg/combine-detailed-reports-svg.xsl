<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">
   
  <!--
    Combines several detailed reports into one SVG figure
  
    TODO: 
      (04/07/26) calculate max-height, remove total-height attribute
  -->

  <xsl:import href="detailed-report-svg.xsl"/>

  <xsl:output indent="yes"/>
 
  <!-- Root element -->
  <xsl:template match="figure">
    <svg xmlns="http://www.w3.org/2000/svg" version="1.1">
      <xsl:variable name="part-width"><xsl:value-of select="@part-width"/></xsl:variable>
      <xsl:attribute name="width"><xsl:value-of select="count(reports/report)*$part-width"/></xsl:attribute>
      <xsl:attribute name="height"><xsl:value-of select="@total-height+160"/></xsl:attribute>

      <xsl:for-each select="reports/report">
        <g xmlns="http://www.w3.org/2000/svg">
          <xsl:attribute name="transform">translate(<xsl:value-of select="(position()-1)*$part-width"/>, 0)</xsl:attribute>
          <text x="0" y="81" font-size="80" stroke-width="0.5" font-family="Tahoma, sans-serif" font-weight="bold" fill="#0040a0">
            <xsl:value-of select="label"/>
          </text>
          <g xmlns="http://www.w3.org/2000/svg" transform="translate(0, 160)">
            <xsl:variable name="detailed-report"><xsl:value-of select="@file"/></xsl:variable>
            <xsl:apply-templates select="document($detailed-report)/report/raw-clusters/raw-cluster"/>
          </g>
        </g>
      </xsl:for-each>
    </svg>
  </xsl:template>
</xsl:stylesheet>
