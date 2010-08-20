<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="../common/page.xsl" />
  <xsl:import href="../common/source-cookies.xsl" />
  <xsl:import href="../common/util.xsl" />
  
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="UTF-8" />

  <xsl:strip-space elements="*"/>

  <xsl:template match="/page" mode="js">
    <!-- JavaScripts -->
    <xsl:apply-templates select="/page/asset-urls/js-urls/js-url" />
  
    <xsl:if test="/page/request/@view = 'visu'">
      <script type="text/javascript" src="{$skin-path}/common-dynamic/js/swfobject.js"><xsl:comment></xsl:comment></script>
    </xsl:if>
    
    <script type="text/javascript">
      jQuery.carrot2.build = "<xsl:value-of select="concat($version-number, 'b', $build-number)" />";
      jQuery.options.url = "<xsl:value-of select="$attributes-url" disable-output-escaping="no" />";
      <xsl:if test="string-length(/page/request/@query) > 0">
        jQuery.documents.url = "<xsl:value-of select="$documents-url" disable-output-escaping="no" />";
        jQuery.documents.source = "<xsl:value-of select="/page/request/@source" />";
        jQuery.documents.query = "<xsl:value-of select="/page/request/@query-escaped" disable-output-escaping="yes" />";
        
        <xsl:if test="/page/request/@view != 'visu'">
          jQuery.clusters.url = "<xsl:value-of select="$clusters-url" disable-output-escaping="no" />"; 
        </xsl:if>  

        <xsl:if test="/page/request/@view = 'visu'">
          jQuery.visualization.dataUrl = "<xsl:value-of select="$xml-url-encoded" />";
          jQuery.visualization.skinPath = "<xsl:value-of select="$skin-path" />";
          jQuery.visualization.logo = "<xsl:value-of select="$circles-logo" />";
        </xsl:if>
      </xsl:if>

<!-- Common initialization -->
$(document).ready(function() {
  $("body").trigger("carrot2-loaded");
});
    </script>
    
    <xsl:apply-imports />
  </xsl:template>
  
  <xsl:template match="page" mode="results">
    <xsl:apply-templates select=".." mode="results.area" />
  </xsl:template>
  
  <xsl:template match="page" mode="results.area">
    <div id="results-area" class="{/page/request/@view}">
      <xsl:if test="/page/request/@view != 'visu'">
        <div id="loading-clusters">Loading...</div>
      </xsl:if>
      <div id="loading-documents">Loading...</div>
      
      <xsl:apply-templates select=".." mode="results-area-extra" />
      
      <xsl:if test="count(/page/config/views/view) > 1">
        <ul id="views">
          <xsl:if test="/page/request/@view = /page/config/views/view[1]/@id">
            <xsl:attribute name="class">first-active</xsl:attribute>        
          </xsl:if>
          <xsl:apply-templates select="/page/config/views/view" />
        </ul>
      </xsl:if>
      
      <div id="clusters-panel">
        <xsl:if test="count(/page/config/views/view) &lt; 2">
          <xsl:attribute name="class">single-view</xsl:attribute>
        </xsl:if>
        <xsl:comment></xsl:comment>
        <xsl:if test="/page/request/@view = 'visu'">
          <div id="clusters-visu"><xsl:comment></xsl:comment></div>
        </xsl:if>
      </div>

      <div id="split-panel"><xsl:comment></xsl:comment></div>
      
      <div id="documents-panel">
        <div id="documents-status">
          <span id="documents-status-overall" class="hide">
            Top&#160;<span id="status-fetched-documents"><xsl:comment></xsl:comment></span> results 
            <span id="status-total" class="hide">of about <span id="status-total-documents"><xsl:comment></xsl:comment></span></span>&#160;for&#160;<span id="status-query"><xsl:comment></xsl:comment></span> 
          </span>
          <span id="documents-status-cluster" class="hide">
            Cluster <span id="status-cluster-label"><xsl:comment></xsl:comment></span>
            with <span id="status-cluster-size"><xsl:comment></xsl:comment></span> documents
          </span>
        </div>
        
        <xsl:apply-templates select="." mode="documents-panel-extra" />
      </div>

      <div id="status-bar">
        <div id="footer">
          <xsl:apply-templates select=".." mode="footer-content" />
        </div>
        Query: <b><xsl:value-of select="/page/request/@query" /></b> 
        -- 
        Source: <b><xsl:value-of select="/page/config/components/sources/source[@id = /page/request/@source]/label" /></b>
        <span class="hide"> (<span id="document-count"><xsl:comment></xsl:comment></span> results, <span id="source-time"><xsl:comment></xsl:comment></span> ms)</span>
        --
        Clusterer: <b><xsl:value-of select="/page/config/components/algorithms/algorithm[@id = /page/request/@algorithm]/label" /></b> 
        <span class="hide"> (<span id="algorithm-time"><xsl:comment></xsl:comment></span> ms)</span>
      </div>
    </div>
  </xsl:template>
  
  <xsl:template match="page" mode="results-area-extra">
    <xsl:apply-imports />
  </xsl:template>
  
  <xsl:template match="page" mode="documents-panel-extra">
    <xsl:apply-imports />
  </xsl:template>
  
  <xsl:template match="view">
    <xsl:variable name="view-pos" select="position()" />
    <li>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="@id = /page/request/@view">active <xsl:choose>
              <xsl:when test="$view-pos = count(/page/config/views/view)">active-last</xsl:when>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>passive <xsl:choose>
              <xsl:when test="$view-pos = count(/page/config/views/view)">passive-last</xsl:when>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      
        <xsl:if test="/page/request/@view = /page/config/views/view[$view-pos + 1]/@id"> before-active</xsl:if>
        <xsl:if test="$view-pos = 1"> first</xsl:if>
      </xsl:attribute>
      
      <xsl:variable name="view-url">
        <xsl:call-template name="replace-in-url">
          <xsl:with-param name="url" select="$request-url" />
          <xsl:with-param name="param" select="$view-param" />
          <xsl:with-param name="value" select="@id" />
        </xsl:call-template>
      </xsl:variable>
      <a href="{$view-url}"><xsl:value-of select="label" /></a>
      <span class="right"><xsl:comment></xsl:comment></span>
    </li>
  </xsl:template>
</xsl:stylesheet>
