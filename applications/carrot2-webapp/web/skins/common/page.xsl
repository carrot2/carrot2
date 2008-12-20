<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:include href="customization.xsl" />
  <xsl:include href="documents.xsl" />
  <xsl:include href="clusters.xsl" />
  <xsl:include href="variables.xsl" />

  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="utf-8" />
              
  <xsl:strip-space elements="*" />
  
  <xsl:variable name="context-path" select="/page/@context-path" />
  <xsl:variable name="skin-path" select="/page/@skin-path" />
  <xsl:variable name="search-url" select="/page/config/@search-url" />
  
  <xsl:variable name="query-param" select="/page/config/@query-param" />
  <xsl:variable name="source-param" select="/page/config/@source-param" />
  <xsl:variable name="algorithm-param" select="/page/config/@algorithm-param" />
  <xsl:variable name="results-param" select="/page/config/@results-param" />
  <xsl:variable name="view-param" select="/page/config/@view-param" />
  <xsl:variable name="type-param" select="/page/config/@type-param" />
  <xsl:variable name="skin-param" select="/page/config/@skin-param" />
  
  <xsl:variable name="request-url" select="/page/@request-url" />
  <xsl:variable name="xml-url-encoded" select="/page/@xml-url-encoded" />
  <xsl:variable name="documents-url" select="concat($request-url, '&amp;type=DOCUMENTS')" />
  <xsl:variable name="clusters-url" select="concat($request-url, '&amp;type=CLUSTERS')" />

  <!-- 
       Counts documents with unique url roots. For some reason xalan does not like this
       definition in documents.xsl, where it should really be.
   -->
  <xsl:key name="urls-by-root" match="document" use="substring-before(concat(url, '/'), '/')" />
  <xsl:variable name="unique-urls" select="count(/page/searchresult/document[generate-id(.) = generate-id(key('urls-by-root', substring-before(concat(substring-after(url, 'http://'), '/'), '/'))[1])])" />
  <xsl:variable name="document-count" select="count(/page/searchresult/document)" />

  <!-- HTML scaffolding -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="page[@full-html = 'true']">
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title><xsl:apply-templates select="page" mode="head-title" /></title>
            <xsl:apply-templates select="page/asset-urls/css-urls/css-url" />
          </head>
          
          <body>
            <xsl:attribute name="id"><xsl:call-template name="page-body-id" /></xsl:attribute>
    
            <!-- Page content -->
            <xsl:apply-templates />
    
            <!-- Custom in-line javascript -->
            <xsl:apply-templates select="/page" mode="js" />
            <script type="text/javascript"> var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www."); 
              document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
            </script>
            <script type="text/javascript">
              var pageTracker = _gat._getTracker("UA-317750-3");
              pageTracker._trackPageview();
            </script>
          </body>
        </html>
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Stylesheets -->
  <xsl:template match="css-url">
    <link rel="stylesheet" href="{.}" type="text/css" />
  </xsl:template>
  
  <!-- JS -->
  <xsl:template match="js-url">
    <script src="{.}" type="text/javascript"><xsl:comment></xsl:comment></script>
  </xsl:template>
  
  <xsl:template match="/page" mode="js" />
  
  <!-- Body tag id -->
  <xsl:template name="page-body-id">
    <xsl:choose>
      <xsl:when test="string-length(/page/request/@query) > 0">results</xsl:when>
      <xsl:otherwise>startup</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Error message -->
  <xsl:template match="page[@type = 'ERROR']">
    <div class="processing-error"><xsl:apply-templates select=".." mode="error-text" /></div>
  </xsl:template>

  <!-- Main page contents -->
  <xsl:template match="page[@type = 'PAGE' or @type = 'FULL' or @type = 'SOURCES']">
    <noscript>
      <div class="noscript"><xsl:apply-templates select=".." mode="no-javascript-text" /></div>
    </noscript>

    <div><!-- We need this extra div to fix IE7 bug: http://www.brunildo.org/test/IEWapie2.html -->
      <xsl:if test="/page/request/@modern = 'false'">
        <span id="use-modern">Use a <a href="http://browsehappy.com/">modern browser</a> for best experience!</span>
      </xsl:if>
                
      <div id="logo">
        <h1><a href="{$context-path}/{$search-url}"><span class="hide"><xsl:apply-templates select=".." mode="page-title" /></span></a></h1>
      </div>
  
      <xsl:if test="not(@type = 'SOURCES')">     
        <div id="main-info">
          <xsl:apply-templates select=".." mode="startup-text" />
        </div>
      </xsl:if>
    </div>
    
    <hr class="hide" />

    <div id="main-area">
      <div id="main-area-top"><xsl:comment></xsl:comment></div>
      <div id="main-area-inside">
        <xsl:choose>
          <xsl:when test="@type = 'SOURCES'">
            <xsl:apply-templates select=".." mode="source-descriptions" />
          </xsl:when>
          
          <xsl:otherwise>
            <xsl:apply-templates select=".." mode="search-area" />
          </xsl:otherwise>
        </xsl:choose>
      </div>
      <div id="main-area-bottom"><xsl:comment></xsl:comment></div>
    </div>

    <hr class="hide" />

    <div id="util-links">
      <xsl:apply-templates select=".." mode="about" />
    </div>

    <xsl:if test="string-length(/page/request/@query) = 0">
      <div id="footer">
        <xsl:apply-templates select=".." mode="footer-content" />
      </div>
    </xsl:if>

    <div id="loading">Loading...</div>
    
    <xsl:if test="string-length(/page/request/@query) > 0">
      <div id="template-document" class="hide">
        <xsl:apply-templates select="document('template-document.xml')" />
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="page" mode="search-area">
    <xsl:apply-templates select="/page" mode="sources" />
    
    <div id="search-area">
      <form action="{$context-path}/{$search-url}">
        <h3 class="hide">Type your query:</h3>

        <div id="required" class="clearfix">
          <xsl:variable name="active-source-id"><xsl:apply-templates select="/page" mode="active-source" /></xsl:variable>
          <input type="hidden" name="{$source-param}" id="source" value="{$active-source-id}" />
          <input type="hidden" name="{$view-param}" id="view" value="{/page/request/@view}" />
          <input type="hidden" name="{$skin-param}" value="{/page/request/@skin}" />
          
          <xsl:apply-templates select=".." mode="query" />
          <xsl:apply-templates select=".." mode="search" />
          
          <span id="show-options"><a href="#" accesskey="o">More options</a></span>
          <span id="hide-options" style="display: none"><a href="#" accesskey="o">Hide options</a></span>
        </div>

        <div id="options" class="hide">
          <xsl:if test="count(/page/config/sizes/size) > 1">
            <label>
              Download
              <select name="{$results-param}">
                <xsl:for-each select="/page/config/sizes/size">
                  <option value="{string(@size)}">
                    <xsl:if test="string(@size) = /page/request/@results">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@size" /> results
                  </option>
                </xsl:for-each>
              </select>
            </label>
          </xsl:if>
                      
          <xsl:if test="count(/page/config/components/algorithms/algorithm) > 1">
            <label>
              Cluster with
              <select name="{$algorithm-param}">
                <xsl:for-each select="/page/config/components/algorithms/algorithm">
                  <option value="{@id}">
                    <xsl:if test="@id = /page/request/@algorithm">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="label" />
                  </option>
                </xsl:for-each>
              </select>
            </label>
          </xsl:if>
        </div>
      </form>
      <xsl:if test="string-length(/page/request/@query) = 0">
        <div id="example-queries"><xsl:comment></xsl:comment></div>
      </xsl:if>
    </div>

    <xsl:if test="string-length(/page/request/@query) > 0">
      <xsl:apply-templates select="/page" mode="results" />
    </xsl:if>
  </xsl:template>

  <xsl:template match="page" mode="active-source"><xsl:value-of select="/page/request/@source" /></xsl:template>
    
  <xsl:template match="page" mode="sources">
    <div id="source-tabs">
      <xsl:variable name="is-first-source-active"><xsl:apply-templates select=".." mode="is-first-source-active" /></xsl:variable>
      <xsl:if test="$is-first-source-active = 'yes'">
        <xsl:attribute name="class">first-active</xsl:attribute>
      </xsl:if>

      <h3 class="hide">Choose where to search:</h3>
      
      <span id="tab-lead-in"><xsl:comment></xsl:comment></span>
      <ul class="tabs clearfix">
        <xsl:apply-templates select="." mode="sources-internal" />
      </ul>
    </div>
  </xsl:template>

  <xsl:template match="page" mode="sources-internal">
    <xsl:apply-templates select="config/components/sources/source" />
  </xsl:template>

  <xsl:template match="page" mode="is-first-source-active">
    <xsl:apply-templates select=".." mode="is-first-source-active-internal" />
  </xsl:template>

  <xsl:template match="page" mode="is-first-source-active-internal">
    <xsl:if test="/page/config/components/sources/source[1]/@id = /page/request/@source">yes</xsl:if>
  </xsl:template>

  <xsl:template match="page" mode="query">
    <xsl:apply-templates select=".." mode="query.field" />
  </xsl:template>

  <xsl:template match="page" mode="query.field">
    <input type="text" name="{$query-param}" id="query" value="{/page/request/@query}" />
  </xsl:template>

  <xsl:template match="page" mode="search">
    <xsl:apply-templates select=".." mode="search.field" />
  </xsl:template>

  <xsl:template match="page" mode="search.field">
    <button type="submit" id="search">Search</button>
  </xsl:template>

  <xsl:template match="source">
    <xsl:variable name="source-position" select="position()" />
    
    <xsl:call-template name="source">
      <xsl:with-param name="source-id" select="@id" />
      <xsl:with-param name="is-last" select="position() = count(/page/config/components/sources/source)" />
      <xsl:with-param name="is-active" select="@id = /page/request/@source" />
      <xsl:with-param name="is-before-active" select="/page/request/@source = /page/config/components/sources/source[$source-position + 1]/@id" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="source">
    <xsl:param name="source-id" />
    <xsl:param name="is-last" />
    <xsl:param name="is-active" />
    <xsl:param name="is-before-active" />
    
    <xsl:variable name="source" select="/page/config/components/sources/source[@id = $source-id]" />
    <xsl:variable name="mnemonic" select="string($source/mnemonic)" />
    <xsl:variable name="label" select="string($source/label)" />
    
    <li id="{$source/@id}">
      <xsl:attribute name="class">tab <xsl:choose>
          <xsl:when test="$is-active">active <xsl:choose>
              <xsl:when test="$is-last">active-last</xsl:when>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="$is-last">passive-last</xsl:when>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      
        <xsl:if test="$is-before-active">before-active</xsl:if>
      </xsl:attribute>

      <a class="label {$source/@id}" href="#" title="{$source/title}">
        <xsl:choose>
          <xsl:when test="string-length($mnemonic) > 0">
            <xsl:attribute name="accesskey"><xsl:value-of select="$mnemonic" /></xsl:attribute>
          
            <xsl:variable name="label-after" select="substring-after($label, $mnemonic)" />
            <xsl:choose>
              <xsl:when test="string-length($label-after) > 0">
                <xsl:value-of select="substring-before($label, $mnemonic)" /><u><xsl:value-of select="$mnemonic" /></u><xsl:value-of select="$label-after" />
              </xsl:when>
              
              <xsl:otherwise>
                <xsl:apply-templates select="$source/label" />
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          
          <xsl:otherwise>
            <xsl:apply-templates select="$source/label" />
          </xsl:otherwise>
        </xsl:choose>
      </a>
      <span class="hide">
        <span class="example-queries">Example queries: 
          <xsl:apply-templates select="$source/example-queries/example-query" />
        </span>
      </span>
      <span class="right"><xsl:comment></xsl:comment></span>
    </li>
  </xsl:template>

  <xsl:template match="source/example-queries/example-query">
    <a href="{$context-path}/{$search-url}?{concat($source-param, '=', string(../../@id), '&amp;', $query-param, '=', string(.))}"><xsl:apply-templates /></a>
  </xsl:template>
  
  <xsl:template match="page" mode="source-descriptions">
    <div id="search-area">
      <p id="source-descriptions-intro">
        Carrot<sup>2</sup> clusters results from the following search feeds:
      </p>
      <ul id="source-descriptions" class="tabs">
        <xsl:apply-templates select="//source" mode="source-descriptions" />
      </ul>
    </div>
  </xsl:template>
  
  <xsl:template match="source" mode="source-descriptions">
    <li class="tab">
      <a class="label {@id}" href="{$context-path}/{$search-url}?{concat($source-param, '=', @id)}"><xsl:value-of select="label" /></a>
      <span class="description"><xsl:apply-templates select="description" /></span>
    </li>
  </xsl:template>
</xsl:stylesheet>
