<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />

  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8" />

  <!-- The query, if any -->
  <xsl:param name="query" select="/page/meta/query" />
  <xsl:variable name="tabElemName">tabElem</xsl:variable>
    
  <xsl:variable name="opts">
      <xsl:choose>
        <xsl:when test="/page/meta/request-arguments/arg[@name='opts']/value = 's'">s</xsl:when>
        <xsl:otherwise>h</xsl:otherwise>
      </xsl:choose>
  </xsl:variable>

  <xsl:variable name="optsHidden"><xsl:if test="$opts = 's'">display: none</xsl:if></xsl:variable>
  <xsl:variable name="optsShown"><xsl:if test="$opts = 'h'">display: none</xsl:if></xsl:variable>

  <xsl:template name="head-insert">
    <link rel="stylesheet" type="text/css" href="{$skinuri}/css/screen.css" />
    <script src="{$skinuri}/js/util.js" type="text/javascript" language="javascript" />
  </xsl:template>
  
  <xsl:template name="on-load">
    <xsl:text>javascript:initPage()</xsl:text>
  </xsl:template>

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
        <td>
          <div id="results-main-search" class="active-area" style="padding-top: 15px">
            <xsl:call-template name="search-area">
              <xsl:with-param name="table-style">margin-left: 130px</xsl:with-param>
            </xsl:call-template>
          </div>
        </td>
      </tr>
  
      <xsl:call-template name="iframes" />

      <tr>
        <td>
          <xsl:call-template name="footer" />
        </td>
      </tr>
    </table>
    <xsl:if test="$init-from-url = 'true'">
      <xsl:call-template name="init-from-url-script" />
    </xsl:if>
  </xsl:template>

  <!-- Search area -->
  <xsl:template name="search-area">
     <xsl:param name="table-style"></xsl:param>

     <form action="{$contextPath}{action-urls/new-search}" method="GET">
     <input type="hidden" id="{$tabElemName}" name="{tabs/@form-element}" value="{tabs/tab[@selected]/@id}" />
           
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
               <td class="c sb"><input type="submit" class="search-button" value="{strings/search}"
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
             <span id="sim-switch" style="{$optsShown}"><a href="javascript:hideAdvanced()">Hide<br/>options</a></span>
             <span id="adv-switch" style="{$optsHidden}"><a href="javascript:showAdvanced()">Show<br/>options</a></span>
           </td>
         </xsl:if>
       </tr>

       <xsl:if test="$show-options = 'true'">
         <tr id="adv-opts" style="{$optsShown}">
           <td style="text-align: right; padding-right: 1px; padding-top: 4px; padding-bottom: 4px;">
             <!-- search results size -->
             <label class="inline-cb-label" for="res-sel">Download</label> 
             <select id="res-sel" name="{query-sizes/@form-element}">
             <xsl:for-each select="query-sizes/size">
                 <option value="{@id}">
                     <xsl:if test="@selected"><xsl:copy-of select="@selected" /></xsl:if>
                     <xsl:value-of select="text()" /> results</option>
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
    <xsl:for-each select="/page/meta/tabs/tab">
      <table id="{concat(@id, '-tab')}">
        <xsl:attribute name="style"><xsl:if test="not(@selected)">display: none;</xsl:if></xsl:attribute>
        <xsl:variable name="tabId"><xsl:value-of select="@id" /></xsl:variable>
        <tr>
          <xsl:for-each select="/page/meta/tabs/tab">
              <xsl:variable name="status">
                  <xsl:choose><xsl:when test="$tabId = @id">active</xsl:when><xsl:otherwise>passive</xsl:otherwise></xsl:choose>
              </xsl:variable>
              <xsl:variable name="nextstatus">
                  <xsl:choose>
                      <xsl:when test="$tabId = following-sibling::tab[position() = 1]/@id">active</xsl:when>
                      <xsl:otherwise>passive</xsl:otherwise>
                  </xsl:choose>
              </xsl:variable>

              <xsl:if test="position() = 1">
                  <td class="tab-{$status}-lead-in"><img alt="" width="14" height="1" /></td>
              </xsl:if>

              <td class="tab-{$status}-body" style="white-space: nowrap;" onclick="javascript:switchTab('{$tabElemName}', '{@id}')"
                  title="{property[@key = 'tab.description']/@value}">
                  
                  <xsl:choose>
                    <xsl:when test="$tabId = @id">
                      <xsl:if test="property[@key = 'tab.icon']">
                          <img class="tab-img" src="{$skinuri}/inputs/{property[@key = 'tab.icon']/@value}" alt="{property[@key = 'tab.name']/@value}" />
                          <xsl:apply-templates select="short" />
                      </xsl:if>
                    </xsl:when>
                    
                    <xsl:otherwise>
                      <a href="javascript:switchTab('{$tabElemName}', '{@id}')" class="tab-link">
                        <xsl:if test="property[@key = 'tab.accel']">
                          <xsl:attribute name="accesskey"><xsl:value-of select="property[@key = 'tab.accel']/@value" /></xsl:attribute>
                        </xsl:if>
                        <xsl:if test="property[@key = 'tab.icon']">
                          <img class="tab-img" src="{$skinuri}/inputs/{property[@key = 'tab.icon']/@value}" alt="{property[@key = 'tab.name']/@value}" />
                        </xsl:if>
                        <xsl:apply-templates select="short" />
                      </a>
                    </xsl:otherwise>
                  </xsl:choose>
              </td>
              
              <xsl:if test="not(position() = last())">
                  <td class="tab-{$status}-{$nextstatus}-link"><img alt="" width="13" height="1" /></td>
              </xsl:if>

              <xsl:if test="position() = last()">
                  <td class="tab-{$status}-lead-out"><img alt="" width="17" height="1" /></td>
              </xsl:if>
          </xsl:for-each>
        </tr>
      </table>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="tab/short">
    <xsl:choose>
      <xsl:when test="../property[@key = 'tab.accel']">
        <xsl:variable name="accel"><xsl:value-of select="../property[@key = 'tab.accel']/@value" /></xsl:variable>
      
        <xsl:choose>
          <xsl:when test="contains(string(.), $accel)">
            <xsl:value-of select="substring-before(string(short), $accel)" />
            <u><xsl:value-of select="$accel" /></u>
            <xsl:value-of select="substring-after(string(.), $accel)" />
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
      <div id="{concat(@id, '-desc')}">
        <xsl:if test="not(@selected)">
          <xsl:attribute name="style">display: none;</xsl:attribute>
        </xsl:if>
        <div class="process-desc">
          <xsl:value-of select="property[@key = 'tab.description.startup']/@value" />
        </div>
       
        <xsl:if test="./example-queries and $show-example-queries = 'true'">
        <div class="example-queries">
          Example queries: 
          <xsl:for-each select="./example-queries/example-query">
            <a href="{$contextPath}{@url}"><xsl:value-of select="." /></a>
            <xsl:text> </xsl:text><xsl:if test="position() != last()">|</xsl:if><xsl:text> </xsl:text>
          </xsl:for-each>
        </div>
        </xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:template>
  
  <!-- Emit code for iframes -->
  <xsl:template name="iframes">
      <tr id="res-row">
        <td class="active-area" style="padding: 10px; height: 100%; width: 100%">
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
                    <td style="padding: 3px; width: 260px; border-right: 1px dotted #808080; height: 100%">
                      <xsl:if test="$show-progress = 'true'">
                        <div id="clusters-progress">
                          <img alt="..." src="{$skinuri}/img/progress.gif" style="position: relative; top: 0.5ex;"/> Loading...
                        </div>
                      </xsl:if>
                      <iframe id="clustersif" name="clusters" frameborder="no" height="100%" width="100%" style="border: 0">
                        <xsl:if test="$init-from-url != 'true'">
                          <xsl:attribute name="src"><xsl:value-of select="$contextPath" /><xsl:value-of select="/page/meta/action-urls/query-clusters" /></xsl:attribute>
                        </xsl:if>
                      </iframe>
                    </td>

                    <td style="padding: 3px; height: 100%">
                      <xsl:if test="$show-progress = 'true'">
                        <div id="docs-progress">
                          <img alt="..." src="{$skinuri}/img/progress.gif" style="position: relative; top: 0.5ex;"/> Loading...
                        </div>
                      </xsl:if>
                      <iframe id="documentsif" name="documents" frameborder="no" height="100%" width="100%" style="border: 0">
                        <xsl:if test="$init-from-url != 'true'">
                          <xsl:attribute name="src"><xsl:value-of select="$contextPath" /><xsl:value-of select="/page/meta/action-urls/query-docs" /></xsl:attribute>
                        </xsl:if>
                      </iframe>
                    </td>
                  </tr>
                  <xsl:if test="$display-status-line = 'true'">
                    <tr>
                      <td colspan="2" class="reshead">
                          Query: <b><xsl:value-of select="$query" /></b>
                          -- Input: <b><xsl:value-of select="/page/meta/tabs/tab[@selected = 'selected']/short" />
                                    (<xsl:value-of select="/page/meta/query-sizes/size[@selected = 'selected']" /> results)
                                    </b>
                          -- Clusterer: <b><xsl:value-of select="/page/meta/algorithms/alg[@selected = 'selected']/short" /></b>
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
                     "/img/tab-passive-passive-link.gif" ];
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
