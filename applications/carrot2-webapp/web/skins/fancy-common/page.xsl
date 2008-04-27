<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:include href="../common/page.xsl" />
  
  <xsl:output indent="yes" omit-xml-declaration="yes"
       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
       media-type="text/html" encoding="utf-8" />

  <xsl:template name="common-extra-css">
    <link rel="stylesheet" href="{$skin-path}/fancy-common/css/style.css" type="text/css" />
    <xsl:call-template name="fancy-extra-css" />
  </xsl:template>

  <xsl:template name="common-extra-js">
    <xsl:if test="/page/request/@query">
      <script>
$(document).ready(function() {
  $("#documents-panel").load("<xsl:value-of select="$documents-url" disable-output-escaping="yes" />");
  
  $.get("<xsl:value-of select="$clusters-url" disable-output-escaping="yes" />", {}, function(data) {
    $("#clusters-panel").prepend(data);
    $("#clusters-panel").trigger("carrot2.clusters.loaded");
  });
});</script>
    </xsl:if>
    
    <script>
$(document).ready(function() {
  $("body").trigger("carrot2.loaded");
});      
    </script>
  </xsl:template>
  
  <xsl:template match="page" mode="results">
    <div id="results-area" class="disabled-ui">
      <div id="clusters-panel">
        <div id="loading-clusters">Loading...</div>
      </div>

      <div id="split-panel"><xsl:comment></xsl:comment></div>
      
      <div id="documents-panel"><xsl:comment></xsl:comment></div>

      <div id="status-bar">
        Status bar
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
