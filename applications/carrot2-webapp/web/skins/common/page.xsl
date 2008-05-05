<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="yes" omit-xml-declaration="yes"
       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
       media-type="text/html" encoding="utf-8" />

  <xsl:strip-space elements="*"/>

  <xsl:variable name="context-path" select="/page/@context-path" />
  <xsl:variable name="skin-path" select="/page/@skin-path" />
  
  <xsl:variable name="search-url" select="/page/config/@search-url" />
  <xsl:variable name="query-param" select="/page/config/@query-param" />
  <xsl:variable name="source-param" select="/page/config/@source-param" />
  
  <xsl:variable name="search-url-base" select="/page/@search-url-base" />
  
  <xsl:variable name="documents-url" select="concat($search-url-base, '&amp;type=DOCUMENTS')" />
  <xsl:variable name="clusters-url" select="concat($search-url-base, '&amp;type=CLUSTERS')" />

  <!-- HTML scaffolding -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="page[@full-html = 'true']">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><xsl:call-template name="page-title" /></title>
    
    <xsl:apply-templates select="page/asset-urls/css-urls/css-url" />
  </head>

  <body>
    <xsl:attribute name="id"><xsl:call-template name="page-body-id" /></xsl:attribute>
    <xsl:apply-templates />
    <xsl:apply-templates select="page/asset-urls/js-urls/js-url" />
    <xsl:call-template name="common-extra-js" />
  </body>
</html>
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- HTML head title -->
  <xsl:template name="page-title">
    <xsl:if test="/page/request/@query">
      <xsl:value-of select="/page/request/@query" />
      -
    </xsl:if>
    Carrot2 Clustering Engine 
  </xsl:template>
  
  <!-- Stylesheets -->
  <xsl:template match="css-url">
    <link rel="stylesheet" href="{.}" type="text/css" />
  </xsl:template>
  
  <!-- JS -->
  <xsl:template match="js-url">
    <script src="{.}" type="text/javascript"><xsl:comment></xsl:comment></script>
  </xsl:template>
  
  <!-- Body tag id -->
  <xsl:template name="page-body-id">
    <xsl:choose>
      <xsl:when test="string-length(/page/request/@query) > 0">results</xsl:when>
      <xsl:otherwise>startup</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Main page contents -->
  <xsl:template match="page[@type = 'PAGE' or @type = 'FULL']">
    <noscript>
      <div class="noscript"><xsl:call-template name="no-javascript-message" /></div>
    </noscript>

    <a href="{$context-path}/{$search-url}">    
      <div id="logo">
        <h1 class="hide"><xsl:call-template name="main-title" /></h1>
      </div>
    </a>
    <p class="hide">
      <xsl:call-template name="main-intro" />
    </p>

    <hr class="hide" />

    <div id="main-area">
      <div id="main-area-top"><xsl:comment></xsl:comment></div>
      <div id="main-area-inside">
        <xsl:apply-templates select="/page" mode="sources" />
        
        <div id="search-area" class="disabled-ui">
          <form action="{$context-path}/{$search-url}">
            <h3 class="hide">Type your query:</h3>

            <input type="text" name="{$query-param}" id="query" value="{/page/request/@query}" />
            <input type="hidden" name="{$source-param}" id="source" value="{/page/request/@source}" />
            <input type="submit" value="Search" id="search" />
          </form>
        </div>

        <xsl:if test="string-length(/page/request/@query) > 0">
          <xsl:apply-templates select="/page" mode="results" />
        </xsl:if>
      </div>
      <div id="main-area-bottom"><xsl:comment></xsl:comment></div>
    </div>

    <hr class="hide" />

    <div id="util-links" class="disabled-ui">
      <h3 class="hide">About Carrot<sup>2</sup>:</h3>
      <ul class="util-links">
        <li><a href="#">About</a></li>
        <li class="hot"><a href="#">New features!</a></li>
        <li class="main"><a href="#">Beta</a></li>
        <li><a href="#">More demos</a></li>
        <li><a href="#">Plugins</a></li>
        <li><a href="#">Download</a></li>
        <li><a href="#">FAQ</a></li>
        <li class="main"><a href="#">Carrot Search</a></li>
        <li><a href="#">Contact</a></li>
      </ul>
    </div>

    <div id="loading">Loading...</div>
  </xsl:template>

  <xsl:template match="page" mode="sources">
    <div id="source-tabs" class="disabled-ui">
      <h3 class="hide">Choose where to search:</h3>

      <ul class="tabs clearfix">
        <xsl:apply-templates select="config/components/sources/source" />
      </ul>
    </div>
  </xsl:template>

  <xsl:template match="source">
    <xsl:variable name="request-source" select="/page/request/@source" />
    
    <li class="tab" id="{@id}">
      <xsl:choose>
        <xsl:when test="@id = $request-source">
          <xsl:attribute name="class">tab active</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">tab</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>

      <a class="label {@id}" href="#" title="{title}"><xsl:apply-templates select="label" /></a>
      <span class="hide">
        <span class="tab-info"><xsl:value-of select="description" /></span><br />
        <span class="example-queries">Example queries: 
          <xsl:apply-templates select="example-queries/example-query" />
        </span>
      </span>
    </li>
  </xsl:template>

  <xsl:template match="source/example-queries/example-query">
    <a href="#"><xsl:apply-templates /></a>
  </xsl:template>

  <!-- Overridable elements -->
  <xsl:template name="no-javascript-message">
    For browsers with no JavaScript support, please use the <a href="#">mobile version of Carrot<sup>2</sup></a>.
  </xsl:template>

  <xsl:template name="main-title">
    Carrot2 Search Results Clustering Engine
  </xsl:template>

  <xsl:template name="main-intro">
    Carrot2 is an Open Source Search Results Clustering Engine. It can
    automatically organize (cluster) search results into thematic
    categories.
  </xsl:template>
  
  <!-- Documents -->
  <xsl:template match="page[@type = 'DOCUMENTS']">
    <div id="documents">
      <xsl:apply-templates select="searchresult/document" />
    </div>
  </xsl:template>

  <xsl:template match="document">
    <div id="d{@id}" class="document">
      <div class="title">
        <h3>
          <span class="rank"><xsl:value-of select="number(@id) + 1" /></span>
          <a href="{url}"><xsl:apply-templates select="title" /></a>
          <a href="#" class="in-clusters" title="Show in clusters"><small>Show in clusters</small></a>
          <a href="#" class="in-new-window" title="Open in new window"><small>Open in new window</small></a>
          <a href="#" class="show-preview" title="Show preview"><small>Show preview</small></a>
        </h3>
      </div>
      <xsl:if test="string-length(snippet) &gt; 0">
        <div class="snippet"><xsl:apply-templates select="snippet" /></div>
      </xsl:if>
      <div class="url"><xsl:apply-templates select="url" /></div>
    </div>
  </xsl:template>
  
  <!-- Clusters -->
  <xsl:template match="page[@type = 'CLUSTERS']">
    <div id="clusters">
      <a id="tree-top" href="#"><span>All Topics</span></a>
      <ul>
        <xsl:apply-templates select="searchresult/group" />
      </ul>
  
      <script>
        var documentCount = <xsl:value-of select="count(searchresult/document)" />;
        var documents = {
          <xsl:apply-templates select="searchresult/group" mode="json" />
        };
      </script>
    </div>
  </xsl:template>

  <xsl:template match="group">
    <li class="folded" id="{generate-id(.)}">
      <a href="#"><span><xsl:apply-templates select="title" /></span></a>
      <xsl:if test="group">
        <ul>
          <xsl:apply-templates select="group" />
        </ul>
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template match="group/title">
    <xsl:choose>
      <xsl:when test="../attribute[@key = 'other-topics']">
        Other topics
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="phrase[1]" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="group" mode="json">
    '<xsl:value-of select="generate-id(.)" />': {
      <xsl:if test="document">
        d: [<xsl:apply-templates mode="json" select="document" />]<xsl:if test="group">,</xsl:if>
      </xsl:if>
      <xsl:if test="group">
        c: { 
          <xsl:apply-templates select="group" mode="json" /> 
        }
      </xsl:if>
    }<xsl:if test="not(position() = last())">,</xsl:if>
  </xsl:template>

  <xsl:template match="group/document" mode="json">
    <xsl:value-of select="@refid" /><xsl:if test="not(position() = last())">,</xsl:if>
  </xsl:template>
</xsl:stylesheet>
