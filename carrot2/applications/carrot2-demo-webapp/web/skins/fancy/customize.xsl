<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:strip-space elements="*"/>

  <!-- Customize these templates by overriding their definitions -->

  <xsl:param name="search-servlet">/search</xsl:param>

  <xsl:param name="ga-code">UA-317750-3</xsl:param>
  <xsl:param name="display-status-line">true</xsl:param>
  <xsl:param name="show-input-descriptions">true</xsl:param>
  <xsl:param name="show-example-queries">true</xsl:param>
  <xsl:param name="show-options">true</xsl:param>
  <xsl:param name="show-progress">true</xsl:param>
  <xsl:param name="show-results-page-footer">false</xsl:param>
  <xsl:param name="init-from-url">false</xsl:param> <!-- Set to 'true' only for generating static HTMLs -->
  <xsl:param name="force-results-page">false</xsl:param> <!-- Set to 'true' only for generating static HTMLs (results page) -->

  <xsl:param name="carrot2.website.url">http://project.carrot2.org</xsl:param>
  <xsl:param name="carrot2.stable.url">http://demo.carrot2.org</xsl:param>
  <xsl:param name="carrot2.head.url">http://demo.carrot2.org/demo-head/</xsl:param>

  <xsl:variable name="beta"><xsl:if test="contains(/processing-instruction('release-info'), 'head')">true</xsl:if></xsl:variable>
  <xsl:variable name="logo-suffix"><xsl:if test="$beta = 'true'">-beta</xsl:if></xsl:variable>

  <xsl:template name="custom-results-utils">
    <div id="results-utils">
      <a href="{$carrot2.website.url}">About</a> |
      <xsl:choose>
        <xsl:when test="$beta = 'true'">
          <a href="{$carrot2.website.url}/release-3.0-rc1-notes.html" class="new">Release 3.0 preview!</a> |
        </xsl:when>
        <xsl:otherwise>
          <a href="{$carrot2.website.url}/release-3.0-rc1-notes.html" class="new">Release 3.0 preview!</a> |
        </xsl:otherwise>
      </xsl:choose>
      <a href="{$carrot2.website.url}/demos.html">More demos</a> |
      <a href="{$carrot2.website.url}/browser-plugins.html">Plugins</a> |
      <a href="{$carrot2.website.url}/download.html">Download</a> |
      <a href="http://sf.net/projects/carrot2">Carrot2 @ sf.net</a> |
      <a href="http://www.carrot-search.com">Carrot Search</a>
    </div>
  </xsl:template>

  <xsl:template name="custom-results-logo">
    <a href="{$contextPath}/"><img border="0" src="{$skinuri}/img/results-logo{$logo-suffix}.gif" id="results-logo" alt="Carrot2 logo" /></a>
  </xsl:template>

  <xsl:template name="custom-startup-logo">
    <a href="{$contextPath}/"><img border="0" src="{$skinuri}/img/startup-logo{$logo-suffix}.gif" id="startup-logo" alt="Carrot2 logo" /></a>
  </xsl:template>

  <xsl:template name="startup-extra-content">
    <div id="startup-extra-ruler" />

    <div id="startup-extra">
      <a href="{$carrot2.website.url}">About</a> |
      <xsl:choose>
        <xsl:when test="$beta = 'true'">
          <a href="{$carrot2.website.url}/release-3.0-rc1-notes.html" class="new">Release 3.0 preview!</a> |
        </xsl:when>
        <xsl:otherwise>
          <a href="{$carrot2.website.url}/release-3.0-rc1-notes.html" class="new">Release 3.0 preview!</a> |
        </xsl:otherwise>
      </xsl:choose>
      <a href="{$carrot2.website.url}/demos.html">More demos</a> |
      <a href="{$carrot2.website.url}/browser-plugins.html">Plugins</a> |
      <a href="{$carrot2.website.url}/download.html">Download</a> |
      <a href="{$carrot2.website.url}/faq.html">FAQ</a> |
      <a href="{$carrot2.website.url}/support.html">Contact</a>
    </div>
  </xsl:template>

  <xsl:template name="copyright-holder">
    © 2002-<xsl:value-of select="/page/@year" />&#160;<a href="http://stanislaw.osinski.name/">Stanislaw Osinski</a>, <a href="http://www.cs.put.poznan.pl/dweiss/">Dawid Weiss</a>
  </xsl:template>

  <xsl:template name="version-info">
  	<xsl:value-of select="normalize-space(/processing-instruction('release-info'))" />
  </xsl:template>
</xsl:stylesheet>
