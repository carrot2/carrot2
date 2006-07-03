<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:strip-space elements="*"/>

  <!-- Customize these templates by overriding their definitions -->

  <xsl:param name="ga-code">UA-317750-3</xsl:param>
  
  <xsl:template name="custom-results-utils">
    <div id="results-utils">
      <a href="http://www.carrot2.org">Carrot2 project homepage</a> |
      <a href="http://sf.net/projects/carrot2">Carrot2 @ sf.net</a> |
      <a href="http://www.carrot-search.com">Carrot Search</a>
    </div>
  </xsl:template>
    
  <xsl:template name="custom-results-logo">
    <img src="{$skinuri}/img/results-logo.gif" id="results-logo" />
  </xsl:template>
    
  <xsl:template name="custom-startup-logo">
    <img src="{$skinuri}/img/startup-logo.gif" id="startup-logo" />
  </xsl:template>
  
</xsl:stylesheet>
