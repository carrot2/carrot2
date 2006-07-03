<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />
  <xsl:import href="customize.xsl" />

  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8" />

  <!-- The query, if any -->
  <xsl:param name="query" select="/page/meta/query" />
  <xsl:variable name="tabElemName">tabElem</xsl:variable>
    
  <xsl:variable name="opts">
      <xsl:choose>
        <xsl:when test="/page/meta/request-arguments/arg[@name='opts']/value = 'h'">h</xsl:when>
        <xsl:otherwise>s</xsl:otherwise>
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
    <xsl:apply-templates select="meta" />
  </xsl:template>

  <!-- Emit the top banner code (tabs, forms) -->
  <xsl:template match="meta">
    <table style="width: 100%; height: 100%">
      <tr>
        <td style="padding-top: 20px">
          <xsl:call-template name="custom-results-utils" />
          <xsl:call-template name="custom-logo" />

          <table id="results-main">
            <tr>
              <td id="startup-main-top-outer-left"><div style="width: 114px" /></td>

              <td>
                <xsl:for-each select="/page/meta/tabs/tab">
                <table class="all-tabs" id="{concat('tab-',@id)}">
                  <xsl:if test="not(@selected)">
                      <xsl:attribute name="style">display: none;</xsl:attribute>
                  </xsl:if>
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
                            <td class="tab-{$status}-lead-in" />
                        </xsl:if>
    
                        <td class="tab-{$status}-body" onclick="javascript:switchTab('{$tabElemName}', '{@id}')">
                            <xsl:if test="property[@key = 'tab.icon']">
                                <img class="tab-img" src="{$skinuri}/inputs/{property[@key = 'tab.icon']/@value}" />
                            </xsl:if>
                            <xsl:value-of select="short" />
                        </td>
                        
                        <xsl:if test="not(position() = last())">
                            <td class="tab-{$status}-{$nextstatus}-link" />
                        </xsl:if>

                        <xsl:if test="position() = last()">
                            <td class="tab-{$status}-lead-out" />
                        </xsl:if>
                    </xsl:for-each>
                  </tr>
                </table>
                </xsl:for-each>
              </td>

              <td id="startup-main-top-outer-right" />
            </tr>
          </table>
        </td>
      </tr>

      <tr id="inp-row">
        <td>
          <div id="results-main-search" class="active-area" style="padding-top: 15px">
             <form action="{action-urls/new-search}" method="GET">
             <table style="margin-left: 120px" id="search-area">
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
                       <td class="c sf"><input class="search-field" style="width: 400px" name="q" id="search-field" value="{$query}" /></td>
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
                 <td style="padding-left: 1px; font-size: 8pt; line-height: 115%">
                   <span id="sim-switch" style="{$optsShown}"><a href="javascript:hideAdvanced()">Hide<br/>options</a></span>
                   <span id="adv-switch" style="{$optsHidden}"><a href="javascript:showAdvanced()">Show<br/>options</a></span>
                 </td>
               </tr>

               <tr id="adv-opts" style="{$optsShown}">
                 <td style="text-align: right; padding-right: 1px; padding-top: 4px; padding-bottom: 4px;">
                   <!-- input -->
                   <input type="hidden" id="{$tabElemName}" name="{tabs/@form-element}" value="{tabs/tab[@selected]/@id}" />
                 
                   <!-- search results size -->
                   <label class="inline-cb-label" for="res-sel">Download</label> 
                   <select id="res-sel" name="{query-sizes/@form-element}">
                   <xsl:for-each select="query-sizes/size">
                       <option value="{position() - 1}">
                           <xsl:if test="@selected"><xsl:copy-of select="@selected" /></xsl:if>
                           <xsl:value-of select="text()" /> results</option>
                   </xsl:for-each>
                   </select>

                   <!-- algorithm -->
                   <label class="inline-cb-label" style="padding-left: 10px" for="alg-sel">Cluster with</label>
                   <select id="alg-sel" name="{algorithms/@form-element}">
                   <xsl:for-each select="algorithms/alg">
                       <option value="{@id}">
                           <xsl:if test="@selected"><xsl:copy-of select="@selected" /></xsl:if>
                           <xsl:value-of select="short" /></option>
                   </xsl:for-each>
                   </select>
                   
                   <!-- show/hide options -->
                   <input type="hidden" id="opts" name="opts" value="{$opts}" />
                 </td>
               </tr>
             </table>
             </form>
          </div>
        </td>
      </tr>

      <xsl:choose>
      <xsl:when test="string-length($query) &gt; 0">
        <xsl:call-template name="iframes" />
      </xsl:when>
      <xsl:otherwise>
      <tr id="res-row">
        <td class="active-area" style="padding: 10px; height: 100%; width: 100%">
        </td>
      </tr>
      </xsl:otherwise>
      </xsl:choose>

      <tr>
        <td>
          <div id="startup-main-bottom-outer" />
          <div class="copyright">
            © 2002-2006 <a href="http://www.carrot-search.com" target="_blank">Carrot Search</a>, Stanislaw Osinski, Dawid Weiss
          </div>
        </td>
      </tr>
    </table>
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
                      <iframe name="clusters" src="{/page/meta/action-urls/query-clusters}" frameborder="no" height="100%" width="100%" style="border: 0" />
                    </td>

                    <td style="padding: 3px; height: 100%">
                      <iframe name="documents" src="{/page/meta/action-urls/query-docs}" frameborder="no" height="100%" width="100%" style="border: 0" />
                    </td>
                  </tr>
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
</xsl:stylesheet>
