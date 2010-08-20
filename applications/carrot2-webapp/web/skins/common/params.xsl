<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="documents.xsl" />
  <xsl:import href="clusters.xsl" />

  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="UTF-8" />
              
  <xsl:strip-space elements="*" />
  
  <!--
       Templates and parameters to override when customizing the webapp. 
    -->
  
  <!-- Suffix appended to the head title after the current query -->
  <xsl:template match="page" mode="head-title-suffix">Carrot2 Clustering Engine</xsl:template>
  
  <!-- Main page title, hidden in most skins, but picked up by search engines -->
  <xsl:template match="page" mode="page-title">Carrot2 Search Results Clustering Engine</xsl:template>

  <!-- Introductory text shown below the query field on the startup screen -->
  <xsl:template match="page" mode="startup-text">
    Carrot<sup>2</sup> organizes your search results into topics. With
    an instant overview of what's available, you will quickly find what 
    you're looking for.  
  </xsl:template>

  <!-- Title of the About section -->
  <xsl:template match="page" mode="about-title">About Carrot<sup>2</sup>:</xsl:template>

  <!-- About text, hidden in most skins, but picked up by search engines -->
  <xsl:template match="page" mode="about-text">
    Carrot2 is an Open Source Search Results Clustering Engine. It can
    automatically organize (cluster) search results into thematic
    categories. For more information, please check the 
    <a href="http://project.carrot2.org">project website</a>.
  </xsl:template>

  <!-- List of about links, most skins show them -->
  <xsl:template match="page" mode="about-links">
    <ul class="util-links">
      <li><a href="http://project.carrot2.org">About</a><xsl:call-template name="pipe" /></li>
      <li class="hot"><a href="http://project.carrot2.org/release-3.4.0-notes.html">New features!</a><xsl:call-template name="pipe" /></li>
      <li class="main"><a href="{$context-path}/{$search-url}?{$type-param}=SOURCES">Search feeds</a><xsl:call-template name="pipe" /></li>
      <li><a href="http://project.carrot2.org/download-search-plugins.html">Search plugins</a><xsl:call-template name="pipe" /></li>
      <li><a href="http://project.carrot2.org/download.html">Download</a><xsl:call-template name="pipe" /></li>
      <li class="main"><a href="http://company.carrot-search.com">Carrot Search</a><xsl:call-template name="pipe" /></li>
      <li><a href="http://project.carrot2.org/contact.html">Contact</a></li>
    </ul>
  </xsl:template>

  <!-- Content of page footer -->
  <xsl:template match="page" mode="footer-content">
    <small id="version">
      v<xsl:value-of select="$version-number" /> |
      build <xsl:value-of select="$build-number" /> |
      <xsl:value-of select="$build-date" />
    </small>
    <small id="copyright">
      &#169; 2002-<xsl:value-of select="/page/@current-year" />&#160;<a href="http://stanislaw.osinski.name" target="_blank">Stanislaw Osinski</a>,
      <a href="http://www.cs.put.poznan.pl/dweiss" target="_blank">Dawid Weiss</a>
    </small>
  </xsl:template>
  
  <xsl:template name="pipe"><span class='pipe'> | </span></xsl:template>

  <!-- Error message text -->
  <xsl:template match="page" mode="error-text">
    Our apologies, the following processing error has occurred: 
    <span class="message"><xsl:value-of select="/page/@exception-message" /></span>
    If the error persists, <a href="http://project.carrot2.org/contact.html">contact us</a>.
  </xsl:template>
  
  <!-- Text to show when JavaScript is disabled -->
  <xsl:template match="page" mode="no-javascript-text">
    To use Carrot<sup>2</sup>, please enable JavaScript in your browser.
  </xsl:template>
  
  <!-- Message to show to old browser users -->
  <xsl:template match="page" mode="old-browser-text">
    <span id="use-modern">Use a <a href="http://browsehappy.com/">modern browser</a> for best experience!</span>
  </xsl:template>
  
  <!-- Introduction to show on the document sources page -->
  <xsl:template match="page" mode="document-sources-intro">
    <p id="source-descriptions-intro">
      Carrot<sup>2</sup> clusters results from the following search feeds:
    </p>
  </xsl:template>
  
  <!-- The whole about section, override if you need better control of it -->
  <xsl:template match="page" mode="about">
    <h3 class="hide"><xsl:apply-templates select=".." mode="about-title" /></h3>
    <p class="hide"><xsl:apply-templates select=".." mode="about-text" /></p>
    <xsl:apply-templates select=".." mode="about-links" />
  </xsl:template>
  
  <!-- The whole head title, override if you need better control of it -->
  <xsl:template match="page" mode="head-title">
    <xsl:if test="string-length(/page/request/@query) > 0">
      <xsl:value-of select="/page/request/@query" /> -
    </xsl:if>
    <xsl:apply-templates select=".." mode="head-title-suffix" />
  </xsl:template>
  
  <!-- The percentage of unique urls required for thumbnails to show -->
  <xsl:param name="unique-urls-for-thumbnails">0.5</xsl:param>
  
  <!-- Ids of document sources for which thumbnails should show -->
  <xsl:param name="document-source-ids-for-thumbnails"></xsl:param>
  
  <!-- Whether to always open links in a new window -->
  <xsl:param name="open-results-in-new-window">false</xsl:param>
  
  <!-- 
       Determines how basic search options should be handled. Allowed values:
       
         * always - basic search options always shown
         * hidden - basic search options shown after clicking "More options" link
         * never - basic search options never
    -->
  <xsl:param name="show-basic-options">hidden</xsl:param>
  
  <!-- 
       Determines how advanced search options should be handled. Allowed values:
       
         * always - advanced search options always shown
         * hidden - advanced search options shown after clicking "More advanced options"
         * never - advanced search options never shown
       
       NOTE: this setting takes effect only when 'show-basic-options' setting is
       set to either 'always' or 'hidden'.
    -->
  <xsl:param name="show-advanced-options">hidden</xsl:param>

  <!-- 
       The logo to display in the Circles visualization. Allowed values:
       
         * carrot2: Carrot2 logo
         * carrot-search: Carrot Search logo
         
       A fully-brandable version of the visualization is available from
       Carrot Search (http://carrotsearch.com/circles). 
    -->
  <xsl:param name="circles-logo">carrot2</xsl:param>
  
  
  <!--
       Google Analytics web property id.  
    -->
  <xsl:param name="google-analytics-web-property-id">UA-317750-3</xsl:param>
</xsl:stylesheet>
