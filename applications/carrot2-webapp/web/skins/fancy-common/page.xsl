<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:include href="../common/page.xsl" />
  <xsl:include href="../common/source-cookies.xsl" />
  
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
       doctype-public="-//W3C//DTD XHTML 1.1//EN"
       doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
       media-type="text/html" encoding="utf-8" />

  <xsl:strip-space elements="*"/>

  <xsl:template name="common-extra-js">
    <xsl:if test="string-length(/page/request/@query) > 0">
      <script type="text/javascript">
$(document).ready(function() {
  $.get($.unescape("<xsl:value-of select="$documents-url" disable-output-escaping="no" />"), {}, function(data) {
    $("#documents-panel").prepend(data);
    $("#documents-panel").trigger("carrot2.documents.loaded");
  });

<xsl:if test="/page/request/@view != 'visu'">
  $.get($.unescape("<xsl:value-of select="$clusters-url" disable-output-escaping="no" />"), {}, function(data) {
    $("#clusters-panel").prepend(data);
    $("#clusters-panel").trigger("carrot2.clusters.loaded");
  });
</xsl:if>  

<xsl:if test="/page/request/@view = 'visu'">
var flashvars = {
  data_sourceURL: "<xsl:value-of select="$xml-url-encoded" />",
  callback_onGroupClick: "groupClicked"
};
var params = {};
var attributes = {};

swfobject.embedSWF("<xsl:value-of select="$skin-path" />/common/swf/org.carrotsearch.vis.circles.swf", "clusters-visu", "100%", "100%", "9.0.0", "<xsl:value-of select="$skin-path" />/common/swf/expressInstall.swf",
    flashvars, params, attributes);
</xsl:if>
});

function groupClicked(clusterId, docList) {
  var documentIndexes = docList.split(",");
  $.documents.select(documentIndexes);
}  
</script>
    </xsl:if>
    
    <script type="text/javascript">
$(document).ready(function() {
  $("body").trigger("carrot2.loaded");
});      
    </script>
  </xsl:template>
  
  <xsl:template match="page" mode="results">
    <div class="glow-small">
      <div id="results-area" class="{/page/request/@view}">
        <xsl:if test="/page/request/@view != 'visu'">
          <div id="loading-clusters">Loading...</div>
        </xsl:if>
        <div id="loading-documents">Loading...</div>
        
        <ul id="views">
          <xsl:if test="/page/request/@view = /page/config/views/view[1]/@id">
            <xsl:attribute name="class">first-active</xsl:attribute>        
          </xsl:if>
          <xsl:apply-templates select="/page/config/views/view" />
        </ul>
        

        
        <div id="clusters-panel">
          <xsl:comment></xsl:comment>
          <xsl:if test="/page/request/@view = 'visu'">
            <div id="clusters-visu"><xsl:comment></xsl:comment></div>
          </xsl:if>
        </div>
  
        <div id="split-panel"><xsl:comment></xsl:comment></div>
        
        <div id="documents-panel"><xsl:comment></xsl:comment></div>
  
        <div id="status-bar">
          Query: <b><xsl:value-of select="/page/request/@query" /></b> 
          -- 
          Source: <b><xsl:value-of select="/page/config/components/sources/source[@id = /page/request/@source]/label" /></b>
          <span class="hide"> (<span id="document-count"><xsl:comment></xsl:comment></span> results, <span id="source-time"><xsl:comment></xsl:comment></span> ms)</span>
          --
          Clusterer: <b><xsl:value-of select="/page/config/components/algorithms/algorithm[@id = /page/request/@algorithm]/label" /></b> 
          <span class="hide"> (<span id="algorithm-time"><xsl:comment></xsl:comment></span> ms)</span>
        </div>
      </div>
      
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

  <xsl:template match="view">
    <xsl:variable name="view-pos" select="position()" />
    <li>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="@id = /page/request/@view">active <xsl:choose>
              <xsl:when test="$view-pos = count(/page/config/views/view)">active-last</xsl:when>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="$view-pos = count(/page/config/views/view)">passive-last</xsl:when>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      
        <xsl:if test="/page/request/@view = /page/config/views/view[$view-pos + 1]/@id">before-active</xsl:if>
      </xsl:attribute>
      
      <a href="{concat($view-url-base, '&amp;', $view-param, '=', @id)}"><xsl:value-of select="label" /></a>
      <span class="right"><xsl:comment></xsl:comment></span>
    </li>
  </xsl:template>
</xsl:stylesheet>
