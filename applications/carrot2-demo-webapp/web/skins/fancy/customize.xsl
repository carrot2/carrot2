<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:strip-space elements="*"/>

  <!-- Customize these templates by overriding their definitions -->

  <xsl:param name="ga-code">UA-317750-3</xsl:param>
  <xsl:param name="display-status-line">true</xsl:param>
  
  <xsl:template name="custom-results-utils">
    <div id="results-utils">
      <a href="{$carrot2.website.url}">About</a> |
      <a href="{$carrot2.website.url}/demos.html">More demos</a> |
      <a href="{$carrot2.website.url}/download.html">Download</a> |
      <a href="http://sf.net/projects/carrot2">Carrot2 @ sf.net</a> |
      <a href="http://www.carrot-search.com">Carrot Search</a>
    </div>
  </xsl:template>
    
  <xsl:template name="custom-results-logo">
    <a href="{$contextPath}/"><img border="0" src="{$skinuri}/img/results-logo.gif" id="results-logo" alt="Carrot2 logo" /></a>
  </xsl:template>
    
  <xsl:template name="custom-startup-logo">
    <a href="{$contextPath}/"><img border="0" src="{$skinuri}/img/startup-logo.gif" id="startup-logo" alt="Carrot2 logo" /></a>
  </xsl:template>
  
  <xsl:param name="carrot2.website.url">http://project.carrot2.org</xsl:param>
  
  <xsl:template name="startup-extra-content">
    <div id="startup-extra-ruler" />

    <div id="startup-extra">
      <a href="{$carrot2.website.url}">About</a> | <a href="{$carrot2.website.url}/demos.html">More demos</a> | <a href="{$carrot2.website.url}/applications.html">Applications</a> | <a href="{$carrot2.website.url}/download.html">Download</a> | <a href="{$carrot2.website.url}/faq.html">FAQ</a> | <a href="{$carrot2.website.url}/support.html">Contact</a>
    </div>
  </xsl:template>
  
  <xsl:template name="copyright-holder">
    <a href="http://www.man.poznan.pl/~stachoo/">Stanislaw Osinski</a>, <a href="http://www.cs.put.poznan.pl/dweiss/">Dawid Weiss</a>
  </xsl:template>
</xsl:stylesheet>
