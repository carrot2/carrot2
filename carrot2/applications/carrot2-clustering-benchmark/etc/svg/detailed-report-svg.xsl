<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">
   
  <!--
    Transforms cluster labels into a simple SVG graphic
  
    TODO: 
      (04/07/25) is it at all possible to calculate the width of the graphic?
  -->
  
  <xsl:output indent="yes"/>
 
  <!-- Root element -->
  <xsl:template match="report">
    <svg xmlns="http://www.w3.org/2000/svg" width="1500" version="1.1">
      <xsl:attribute name="height"><xsl:value-of select="10+(count(raw-clusters/raw-cluster)+1)*90"/></xsl:attribute>
      <g xmlns="http://www.w3.org/2000/svg">
        <xsl:apply-templates select="raw-clusters/raw-cluster"/>
      </g>
    </svg>
  </xsl:template>

  <xsl:template match="raw-cluster">
    <g xmlns="http://www.w3.org/2000/svg" transform="translate(5, 5)" stroke="#606060" stroke-width="6">
      <xsl:attribute name="transform">translate(5, <xsl:value-of select="5+(position()-1)*90"/>)</xsl:attribute>
      <rect x="0" y="0" width="50" height="50" fill="#d0d0d0" stroke="#909090"/>
      <line x1="25" y1="10" x2="25" y2="40"/>
      <line x1="10" y1="25" x2="40" y2="25"/>
      <text x="80" y="55" font-size="80" stroke-width="0.5" font-family="Tahoma, sans-serif" fill="#0040a0">
        <xsl:apply-templates select="labels"/>
        <xsl:text> </xsl:text>
        (<xsl:value-of select="size"/>)
      </text>
    </g>
  </xsl:template>

  <xsl:template match="raw-cluster/labels">
    <xsl:if test="label"><xsl:value-of select="label[1]"/><xsl:for-each select="label[position() > 1]">,<xsl:text> </xsl:text><xsl:value-of select="."/></xsl:for-each></xsl:if>
  </xsl:template>

  <!-- Suppress the rest -->
  <xsl:template match="*"/>
</xsl:stylesheet>
