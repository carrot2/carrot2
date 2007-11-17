<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />

  <xsl:strip-space elements="*"/>

  <xsl:output indent="no" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8" />

  <!-- The query, if any -->
  <xsl:param name="query" select="/page/meta/query" />
  <xsl:param name="query-escaped" select="/page/meta/query-escaped" />
  <xsl:variable name="tabElemName">tabElem</xsl:variable>

  <xsl:variable name="opts">
      <xsl:choose>
        <xsl:when test="/page/meta/request-arguments/arg[@name='opts']/value = 's'">s</xsl:when>
        <xsl:otherwise>h</xsl:otherwise>
      </xsl:choose>
  </xsl:variable>

  <xsl:variable name="optsHidden"><xsl:if test="$opts = 's'">display: none</xsl:if></xsl:variable>
  <xsl:variable name="optsShown"><xsl:if test="$opts = 'h'">display: none</xsl:if></xsl:variable>

  <xsl:template name="on-load" />

  <xsl:template name="head-insert">
    <link rel="stylesheet" type="text/css" href="{$skinuri}/css/screen.css" />
  </xsl:template>

  <xsl:template name="body-insert">
    <script type="text/javascript" src="{$skinuri}/js/yui/yahoo-dom-event.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/yui/dragdrop-min.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/SearchTabController.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/Utils.js" ></script>

    <script type="text/javascript">
var query = "<xsl:value-of select="$query-escaped" />";
var userTabIds = "<xsl:apply-templates select="/page/meta/user-tabs/user-tab" mode="tab-ids" />";
var selectedTabId = "<xsl:value-of select="/page/meta/user-tabs/user-tab[@selected]/@id" />";
var allTabIds = "<xsl:apply-templates select="/page/meta/tabs/tab" mode="tab-ids" />";

var facetTabIds = "<xsl:apply-templates select="/page/meta/facet-tabs/facet-tab" mode="tab-ids" />";
var selectedFacetTabId = "<xsl:value-of select="/page/meta/facet-tabs/facet-tab[@selected]/@id" />";
var facetTabUris = new Array();
    <xsl:for-each select="/page/meta/facet-tabs/facet-tab">
facetTabUris["<xsl:value-of select="short" />"] = "<xsl:value-of select="concat($contextPath, $search-servlet, '?', @uri)" />"; 
    </xsl:for-each>
    </script>

    <script type="text/javascript" src="{$skinuri}/js/Carrot2App.js" ></script>

    <script type="text/javascript">
YAHOO.util.Event.addListener(window, "load", c2AppInit, stc, true);
    </script>
  </xsl:template>

  <xsl:template match="user-tab" mode="tab-ids"><xsl:value-of select="@id" />:</xsl:template>
  <xsl:template match="tab" mode="tab-ids"><xsl:value-of select="@id" />:</xsl:template>
  <xsl:template match="facet-tab" mode="tab-ids"><xsl:value-of select="@id" />:</xsl:template>

  <!-- end of customization block -->

  <xsl:template match="page">
    <xsl:choose>
    <xsl:when test="string-length($query) &gt; 0 or $force-results-page = 'true'">
      <xsl:apply-templates select="meta" mode="query" />
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="meta" mode="no-query" />
    </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- Emit the main page -->
  <xsl:template match="meta" mode="no-query">

    <xsl:call-template name="preload-script" />
    <xsl:call-template name="custom-results-utils" />
    <div><!-- empty --></div>
    <table id="startup-main">
      <tr>
        <td id="startup-main-top-outer-left"><xsl:call-template name="custom-startup-logo" /><div style="width: 230px" /></td>
        <td><xsl:call-template name="tabs" /></td>
        <td id="startup-main-top-outer-right" />
      </tr>
    </table>

    <div id="startup-main-content" style="padding-left: 236px; padding-top: 15px">
      <xsl:call-template name="search-area">
        <xsl:with-param name="table-style">margin-bottom: 10px</xsl:with-param>
      </xsl:call-template>
      <xsl:if test="$show-input-descriptions = 'true'">
        <xsl:call-template name="input-descriptions" />
      </xsl:if>

      <xsl:call-template name="hidden-tabs" />

      <xsl:call-template name="startup-extra-content" />
    </div>

    <xsl:call-template name="footer" />
  </xsl:template>

  <!-- Emit the results page -->
  <xsl:template match="meta" mode="query">

    <xsl:call-template name="preload-script" />
    <table style="width: 100%; height: 100%">
      <tr>
        <td style="padding-top: 17px">
          <xsl:call-template name="custom-results-utils" />
          <xsl:call-template name="custom-results-logo" />

          <table id="results-main">
            <tr>
              <td id="startup-main-top-outer-left"><div style="width: 124px"></div></td>

              <td>
                <xsl:call-template name="tabs" />
              </td>

              <td id="startup-main-top-outer-right" />
            </tr>
          </table>
        </td>
      </tr>

      <tr id="inp-row">
        <td class="active-area">
          <div id="results-main-search" style="padding-top: 15px">
            <xsl:call-template name="search-area">
              <xsl:with-param name="table-style">margin-left: 130px</xsl:with-param>
            </xsl:call-template>
          </div>
        </td>
      </tr>

      <xsl:call-template name="iframes" />

      <tr id="more-row" style="display: none">
        <td class="active-area" style="height: 100%; width: 100%; vertical-align: top; padding-left: 130px">
          <xsl:call-template name="hidden-tabs" />
        </td>
      </tr>

      <xsl:if test="$show-results-page-footer = 'true'">
        <tr>
          <td>
            <xsl:call-template name="footer" />
          </td>
        </tr>
      </xsl:if>
    </table>
    <xsl:if test="$init-from-url = 'true'">
      <xsl:call-template name="init-from-url-script" />
    </xsl:if>
  </xsl:template>

  <!-- Search area -->
  <xsl:template name="search-area">
     <xsl:param name="table-style"></xsl:param>

     <form action="{$contextPath}{$search-servlet}" method="GET" id="search-area">
     <input type="hidden" id="{$tabElemName}" name="{tabs/@form-element}"
            value="{/page/meta/user-tabs/user-tab[@selected]/@id}" />

     <table style="{$table-style}" id="search-area">
       <tr>
         <td>
           <table class="glow glow-small" style="background-color: white">
             <tr>
               <td class="cs tl"></td>
               <td class="hb t"></td>
               <td class="cs tr"></td>
             </tr>
             <tr>
               <td class="vb l"></td>
               <td class="c sf">
                <input class="search-field" style="width: 400px" name="q" id="search-field">
                  <xsl:if test="$init-from-url != 'true'">
                    <xsl:attribute name="value"><xsl:value-of select="$query" /></xsl:attribute>
                  </xsl:if>
                </input>
               </td>
               <td class="vb r"></td>
             </tr>
             <tr>
               <td class="cs bl"></td>
               <td class="hb b"></td>
               <td class="cs br"></td>
             </tr>
           </table>
         </td>

         <td>
           <table class="glow glow-big">
             <tr>
               <td class="cs tl"></td>
               <td class="hb t"></td>
               <td class="cs tr"></td>
             </tr>
             <tr>
               <td class="vb l"></td>
               <td class="c sb"><input type="submit" class="search-button" value="{/page/meta/strings/search}"
                 onmouseover="this.className = 'search-button hl'"
                 onmouseout="this.className='search-button'" /></td>
               <td class="vb r"></td>
             </tr>
             <tr>
               <td class="cs bl"></td>
               <td class="hb b"></td>
               <td class="cs br"></td>
             </tr>
           </table>
         </td>
         <xsl:if test="$show-options = 'true'">
           <td style="padding-left: 1px; font-size: 8pt; line-height: 115%">
             <span id="sim-switch" style="{$optsShown}"><span class="blue link" id="hide-advanced"><xsl:value-of select="/page/meta/strings/hide-options.line-1" /><br/><xsl:value-of select="/page/meta/strings/hide-options.line-2" /></span></span>
             <span id="adv-switch" style="{$optsHidden}"><span class="blue link" id="show-advanced"><xsl:value-of select="/page/meta/strings/show-options.line-1" /><br/><xsl:value-of select="/page/meta/strings/show-options.line-2" /></span></span>
           </td>
         </xsl:if>
       </tr>

       <xsl:if test="$show-options = 'true'">
         <tr id="adv-opts" style="{$optsShown}">
           <td style="text-align: right; padding-right: 1px; padding-top: 4px; padding-bottom: 4px;">
             <!-- search results size -->
             <label class="inline-cb-label" for="res-sel"><xsl:value-of select="/page/meta/strings/download-results.prefix" /></label>
             <select id="res-sel" name="{query-sizes/@form-element}">
             <xsl:for-each select="query-sizes/size">
                 <option value="{@id}">
                     <xsl:if test="@selected"><xsl:copy-of select="@selected" /></xsl:if>
                     <xsl:value-of select="text()" />&#160;<xsl:value-of select="/page/meta/strings/download-results.suffix" /></option>
             </xsl:for-each>
             </select>

             <!-- algorithm -->
             <xsl:choose>
               <xsl:when test="count(algorithms/alg) > 1">
                 <label class="inline-cb-label" style="padding-left: 10px" for="alg-sel">Cluster with</label>
                 <select id="alg-sel" name="{algorithms/@form-element}">
                 <xsl:for-each select="algorithms/alg">
                     <option value="{@id}">
                         <xsl:if test="@selected"><xsl:copy-of select="@selected" /></xsl:if>
                         <xsl:value-of select="short" /></option>
                 </xsl:for-each>
                 </select>
               </xsl:when>
               <xsl:otherwise>
                 <input type="hidden" name="{algorithms/@form-element}"
                        value="{algorithms/alg[1]/@id}" />
               </xsl:otherwise>
             </xsl:choose>

             <!-- show/hide options -->
             <input type="hidden" id="opts" name="opts" value="{$opts}" />
           </td>
         </tr>
       </xsl:if>
     </table>
     </form>
  </xsl:template>

  <!-- Tabs -->
  <xsl:template name="tabs">
    <table>
      <tr id="main-tabs">
        <xsl:for-each select="/page/meta/user-tabs/user-tab">
          <xsl:variable name="id"><xsl:value-of select="@id" /></xsl:variable>
          <xsl:variable name="status">
            <xsl:choose><xsl:when test="@selected">active</xsl:when><xsl:otherwise>passive</xsl:otherwise></xsl:choose>
          </xsl:variable>
          <xsl:variable name="nextstatus">
            <xsl:choose>
              <xsl:when test="following-sibling::user-tab[position() = 1]/@selected">active</xsl:when>
              <xsl:otherwise>passive</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:if test="position() = 1">
            <td class="tab-{$status}-lead-in"><img alt="" width="14" height="1" /></td>
          </xsl:if>

          <td class="tab-{$status}-body" id="{@id}-td" title="{/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.description']}">
            <div id="{@id}" class="tab-content">
              <span class="arrow">&#160;</span>
              <a id="{@id}-link" class="tab-link active" href="#">
                <xsl:if test="/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.accel']">
                  <xsl:attribute name="accesskey"><xsl:value-of select="/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.accel']" /></xsl:attribute>
                </xsl:if>
                <xsl:choose>
                  <xsl:when test="/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.icon']">
                    <img class="tab-img" src="{$skinuri}/inputs/{/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.icon']}" alt="{/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.name']}" />
                  </xsl:when>
                  <xsl:otherwise>
                    <img class="tab-img" src="{$skinuri}/inputs/unknown.gif" alt="{/page/meta/tabs/tab[@id = $id]/property[@key = 'tab.name']}" />
                  </xsl:otherwise>
                </xsl:choose><span>&#160;&#160;</span><xsl:apply-templates select="/page/meta/tabs/tab[@id = $id]/short" /></a>
            </div>
          </td>

          <td class="tab-{$status}-{$nextstatus}-link"><img alt="" width="13" height="1" /></td>
        </xsl:for-each>

        <xsl:call-template name="more-tab" />
        <td class="tab-passive-lead-out"><img alt="" width="17" height="1" /></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="facet-tabs">
    <table style="position: absolute; margin-top: -0px; margin-left: -0px">
      <tr id="facet-tabs">
        <xsl:for-each select="/page/meta/facet-tabs/facet-tab">
          <xsl:variable name="id"><xsl:value-of select="@id" /></xsl:variable>
          <xsl:variable name="status">
            <xsl:choose><xsl:when test="@selected">active</xsl:when><xsl:otherwise>passive</xsl:otherwise></xsl:choose>
          </xsl:variable>
          
          <xsl:variable name="nextstatus">
            <xsl:choose>
              <xsl:when test="following-sibling::facet-tab[position() = 1]/@selected">active</xsl:when>
              <xsl:otherwise>passive</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:if test="position() = 1">
            <td class="ftab-{$status}-lead-in"><img alt="" width="6" height="1" /></td>
          </xsl:if>

          <td class="ftab-{$status}-body" title="{title}" id="{@id}-td">
            <div id="{@id}" class="tab-content"><xsl:apply-templates select="short" /></div>
          </td>

          <xsl:choose>
            <xsl:when test="position() = last()">
              <td class="ftab-{$status}-lead-out"><img alt="" width="10" height="1" /></td>
            </xsl:when>

            <xsl:otherwise>
              <td class="ftab-{$status}-{$nextstatus}-link"><img alt="" width="10" height="1" /></td>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="more-tab">
    <td class="tab-passive-body" id="-more-td" title="more...">
      <div id="-more" class="tab-content">
        <span class="arrow">&#160;</span>
        <a id="-more-link" class="tab-link active" accesskey="." href="#">
          <img class="tab-img" src="{$skinuri}/inputs/unknown.gif" alt="more search sources..." /><span>&#160;&#160;</span>more<u>.</u>..</a>
      </div>
    </td>
  </xsl:template>

  <xsl:template match="tab/short">
    <xsl:choose>
      <xsl:when test="../property[@key = 'tab.accel']">
        <xsl:variable name="accel"><xsl:value-of select="../property[@key = 'tab.accel']" /></xsl:variable>

        <xsl:choose>
          <xsl:when test="contains(string(.), $accel)">
            <span><xsl:value-of select="substring-before(string(.), $accel)" /></span>
            <u><xsl:value-of select="$accel" /></u>
            <span><xsl:value-of select="substring-after(string(.), $accel)" /></span>
          </xsl:when>
          <xsl:otherwise>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="." />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Input descriptions -->
  <xsl:template name="input-descriptions">
    <div id="process-instr">
      <xsl:if test="$opts = 's'">
        <xsl:attribute name="style">display: none</xsl:attribute>
      </xsl:if>
    <xsl:for-each select="/page/meta/tabs/tab">
      <xsl:variable name="id"><xsl:value-of select="@id" /></xsl:variable>
      <div id="{concat(@id, '-desc')}">
        <xsl:if test="not(/page/meta/user-tabs/user-tab[@id = $id]/@selected)">
          <xsl:attribute name="style">display: none;</xsl:attribute>
        </xsl:if>
        <div class="process-desc">
          <xsl:apply-templates select="property[@key = 'tab.description.startup']" />
        </div>

        <xsl:if test="./example-queries and $show-example-queries = 'true'">
        <div class="example-queries">
          Example queries:
          <xsl:for-each select="./example-queries/example-query">
            <a href="{$contextPath}{$search-servlet}?{@url}"><xsl:value-of select="." /></a>
            <xsl:text> </xsl:text><xsl:if test="position() != last()">|</xsl:if><xsl:text> </xsl:text>
          </xsl:for-each>
        </div>
        </xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:template>

  <!-- Hidden tabs -->
  <xsl:template name="hidden-tabs">
    <div id="-more-desc" style="display: none">
      <div id="dd-tip">
        Tip: you can drag &amp; drop<br />the tabs to change order
      </div>
      <xsl:for-each select="/page/meta/tabs/tab">
        <xsl:variable name="id"><xsl:value-of select="@id" /></xsl:variable>
        <div class="more-entry">
          <input id="{@id}-cb" type="checkbox">
            <xsl:if test="/page/meta/user-tabs/user-tab[@id = $id]">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input>
          <div id="{@id}-h-link">
            <div id="{@id}-h" class="tab-content">
              <span class="arrow">&#160;</span>
              <a class="tab-link active" href="#">
                <xsl:if test="property[@key = 'tab.accel']">
                  <xsl:attribute name="accesskey"><xsl:value-of select="property[@key = 'tab.accel']" /></xsl:attribute>
                </xsl:if>
                <xsl:choose>
                  <xsl:when test="property[@key = 'tab.icon']">
                    <img class="tab-img" src="{$skinuri}/inputs/{property[@key = 'tab.icon']}" alt="{property[@key = 'tab.name']}" />
                  </xsl:when>
                  <xsl:otherwise>
                    <img class="tab-img" src="{$skinuri}/inputs/unknown.gif" alt="{property[@key = 'tab.name']}" />
                  </xsl:otherwise>
                </xsl:choose>
                <span>&#160;&#160;</span><xsl:apply-templates select="short" /></a>
              <span class="hidden-long-desc"><xsl:apply-templates select="long" /></span>
            </div>
          </div>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>

  <!-- Emit code for iframes -->
  <xsl:template name="iframes">
    <tr id="res-row">
      <td class="active-area" style="padding: 10px; height: 100%; width: 100%">
        <xsl:call-template name="facet-tabs" />
        <xsl:if test="$show-progress = 'true'">
          <div id="clusters-progress">
            <img alt="..." src="{$skinuri}/img/progress.gif" style="position: relative; top: 0.5ex;"/>&#160;<xsl:value-of select="/page/meta/strings/loading" />
          </div>
        </xsl:if>
        <table class="glow glow-small" style="background-color: white; height: 100%; width: 100%">
          <tr>
            <td class="cs tl"></td>
            <td class="hb t"></td>
            <td class="cs tr"></td>
          </tr>
          <tr>
            <td class="vb l"></td>
            <td class="c" id="results-main-content" style="height: 100%">
              <table style="height: 100%; width: 100%">
                <tr>
                  <td id="clusterstd">
                    <xsl:if test="count(/page/meta/facet-tabs/facet-tab) &gt; 1">
                      <xsl:attribute name="style">padding-top: 25px</xsl:attribute>
                    </xsl:if>
                    <iframe id="clustersif" name="clusters" frameborder="no" height="100%" width="100%" style="border: 0;">
                      <xsl:if test="$init-from-url != 'true'">
                        <xsl:attribute name="src"><xsl:value-of select="concat($contextPath,$search-servlet,'?',/page/meta/action-urls/query-clusters)" /></xsl:attribute>
                      </xsl:if>
                    </iframe>
                  </td>

                  <td style="padding: 3px; height: 100%">
                    <xsl:if test="$show-progress = 'true'">
                      <div id="docs-progress">
                        <img alt="..." src="{$skinuri}/img/progress.gif" style="position: relative; top: 0.5ex;"/>&#160;<xsl:value-of select="/page/meta/strings/loading" />
                      </div>
                    </xsl:if>
                    <iframe id="documentsif" name="documents" frameborder="no" height="100%" width="100%" style="border: 0">
                      <xsl:if test="$init-from-url != 'true'">
                        <xsl:attribute name="src"><xsl:value-of select="concat($contextPath,$search-servlet,'?',/page/meta/action-urls/query-docs)" /></xsl:attribute>
                      </xsl:if>
                    </iframe>
                  </td>
                </tr>
                <xsl:if test="$display-status-line = 'true'">
                  <tr>
                    <td colspan="2" class="reshead">
                      <xsl:if test="not($show-results-page-footer = 'true')">
                        <span class="small-copy">
                          <xsl:call-template name="copyright-holder" />
                        </span>
                      </xsl:if>

                      Query: <b><xsl:value-of select="$query" /></b>
                      -- Source: <b><xsl:value-of select="/page/meta/tabs/tab[@id = /page/meta/user-tabs/user-tab[@selected]/@id]/short" /></b>
                                (<xsl:value-of select="/page/meta/query-sizes/size[@selected]" /> results<span style="display: none" id="itime">, <span id="itimec"></span> ms</span>)
                      -- Clusterer: <b><xsl:value-of select="/page/meta/algorithms/alg[@selected]/short" /></b>
                                &#160;<span style="display: none" id="ctime">(<span id="ctimec"></span> ms)</span>
                    </td>
                  </tr>
                </xsl:if>
              </table>

            </td>
            <td class="vb r"></td>
          </tr>
          <tr>
            <td class="cs bl"></td>
            <td class="hb b"></td>
            <td class="cs br"></td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="preload-script">
    <script type="text/javascript" language="javascript">&lt;!--
    var preload = new Array();
    if (document.images)
    {
      var skinuri = "<xsl:value-of select='$skinuri' />";
      var preload = ["/img/progress.gif",
                     "/img/tab-active-lead-in.gif",
                     "/img/tab-active-lead-out.gif",
                     "/img/tab-active-passive-link.gif",
                     "/img/tab-passive-active-link.gif",
                     "/img/tab-passive-lead-in.gif",
                     "/img/tab-passive-lead-out.gif",
                     "/img/tab-passive-passive-link.gif", 
                     "/img/ftab-active-body.gif",
                     "/img/ftab-active-lead-in.gif",
                     "/img/ftab-active-lead-out.gif",
                     "/img/ftab-active-passive-link.gif",
                     "/img/ftab-passive-active-link.gif",
                     "/img/ftab-passive-body.gif",
                     "/img/ftab-passive-lead-in.gif",
                     "/img/ftab-passive-lead-out.gif",
                     "/img/ftab-passive-passive-link.gif" 
                     ];
      for (i = 0; i &lt; preload.length; i++) {
          var img = new Image();
          img.src = skinuri + preload[i];
          preload[i] = img;
      }
    }
    //--> </script>
  </xsl:template>

  <xsl:template name="init-from-url-script">
    <script type="text/javascript" language="javascript">&lt;!--
      var input = getParam(document.location, 'in');
      document.getElementById('search-field').value = getParam(document.location, 'q');
      if (input != "") {
        switchTab('<xsl:value-of select='$tabElemName' />', input);

        var url = document.location.toString();
        var queryString = "&amp;" + url.substring(url.indexOf('?')+1, url.length);
        document.getElementById('clustersif').src = "<xsl:value-of select="/page/meta/action-urls/query-clusters" />" + queryString;
        document.getElementById('documentsif').src = "<xsl:value-of select="/page/meta/action-urls/query-docs" />" + queryString;
      }
    //--> </script>
  </xsl:template>

  <xsl:template name="footer">
    <div id="startup-main-bottom-outer" />
    <div class="versionInfo">
      <xsl:call-template name="version-info" />
    </div>
    <div class="copyright">
      <xsl:call-template name="copyright-holder" />
    </div>
  </xsl:template>
</xsl:stylesheet>
