<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />
  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8"
       doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <xsl:param name="more-increment">8</xsl:param>

  <xsl:template name="on-load">
    <xsl:text>javascript:parent.setProgress('clusters-progress', false);</xsl:text>
  </xsl:template>

  <xsl:template name="head-insert">
    <link rel="stylesheet" href="{$skinuri}/css/clusters.css" />
    <script src="{$skinuri}/js/folding.js" language="javascript"></script>
    <script>
      var clusterDocs = new Array();
      <xsl:for-each select="//group">clusterDocs['<xsl:value-of select="generate-id(.)" />']=[<xsl:value-of select="@docs" />];</xsl:for-each>
    </script>
  </xsl:template>

  <xsl:template match="/searchresult[@type = 'clusters']">
    <xsl:if test="@insertPoweredBy">
      <div class="pb">
        Clustering powered by<br/>
        <a href="http://www.carrot2.org" target="_top">Carrot<sup>2</sup> Clustering Engine</a>
      </div>
    </xsl:if>
  
    <xsl:if test="count(group) > 0">
      <table class="cluster-tree">
        <tr>
          <td><img src="{$skinuri}/img/folder.gif" class="f" alt="..." /></td>
          <td class="text hl" style="padding-top: 0" colspan="2" id="ttop">
            <a class="group" href="javascript:hl('top'); hlw([]); showAll();"><span class="label"><span class="text">
            All results</span>&#160;<span class="size">(<xsl:value-of select="@totalResultsCount" />)</span></span></a>
          </td>
        </tr>
  
        <xsl:apply-templates select="group" />
      </table>
    </xsl:if>
    <xsl:if test="count(group) = 0">
      <div id="no-clusters">
        No clusters created
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="group">
    <xsl:param name="id"><xsl:value-of select="generate-id(.)" /></xsl:param>
    <xsl:param name="parent-id"><xsl:value-of select="generate-id(..)" /></xsl:param>
    <xsl:param name="parent-id-value"><xsl:value-of select="concat($parent-id, position())" /></xsl:param>
  
    <tr id="{$parent-id-value}">
      <xsl:if test="not(count(group) = 0)">
        <xsl:attribute name="onclick">javascript:fold('cld<xsl:value-of select="$id" />');sel(clusterDocs['<xsl:value-of select="$id" />']);hlw([<xsl:value-of select="@words" />])</xsl:attribute>
      </xsl:if>
      <xsl:if test="count(group) = 0">
        <xsl:attribute name="onclick">javascript:hl('<xsl:value-of select="$id" />');sel(clusterDocs['<xsl:value-of select="$id" />']);hlw([<xsl:value-of select="@words" />])</xsl:attribute>
      </xsl:if>
      
      <xsl:if test="position() &gt; $more-increment">
        <xsl:attribute name="style">display: none</xsl:attribute>
      </xsl:if>
      <td>
        <xsl:choose>
          <xsl:when test="position() = last() and count(group) = 0">
            <img class="fold-nt" src="{$skinuri}/img/b-no-tree.gif" alt="..." />
          </xsl:when>

          <xsl:when test="position() = last() and not(count(group) = 0)">
            <xsl:attribute name="class">tree-b</xsl:attribute>
            <img class="fold-nt-b" src="{$skinuri}/img/b-tree.gif" alt="..." />
          </xsl:when>

          <xsl:when test="position() != last() and not(count(group) = 0)">
            <xsl:attribute name="class">tree</xsl:attribute>
            <img class="fold" src="{$skinuri}/img/tree.gif" alt="..." />
          </xsl:when>

          <xsl:otherwise>
            <xsl:attribute name="class">tree</xsl:attribute>
            <img class="fold" src="{$skinuri}/img/no-tree.gif" alt="..." />
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td class="fold">
        <img src="{$skinuri}/img/folder.gif" class="f" alt="..." />
      </td>
      <td class="text" id="t{$id}">
        <a class="group" href="javascript:void(null)">
          <span class="label"><span class="text"><xsl:value-of select="title/phrase[1]" /></span>&#160;<span class="size">(<xsl:value-of select="count(descendant-or-self::*/document)" />)</span></span>
        </a>
      </td>
    </tr>

    <xsl:if test="not(count(group) = 0)">
      <tr style="display: none" id="cld{$id}">
        <td>
          <xsl:if test="position() != last()">
            <xsl:attribute name="class">tree</xsl:attribute>
          </xsl:if>
        </td>
        <td colspan="2">
          <table class="cluster-tree">
            <xsl:apply-templates select="group" />
          </table>
        </td>
      </tr>
    </xsl:if>

    <xsl:if test="position() mod $more-increment = 0 and not(position() = last())">
      <tr id="{concat($parent-id, 'more', position())}">
        <xsl:if test="position() div $more-increment &gt; 1">
          <xsl:attribute name="style">display: none</xsl:attribute>
        </xsl:if>

        <xsl:variable name="more-link">javascript:foldRange('<xsl:value-of select="$parent-id" />','more',<xsl:value-of select="position() + 1" />,<xsl:value-of select="position() + $more-increment" />)</xsl:variable>
        <td>
          <img class="fold-nt" src="{$skinuri}/img/b-no-tree.gif" alt="..." />
        </td>
        <td class="fold"><a href="{$more-link}"><img src="{$skinuri}/img/folder-more.gif" class="f" alt="..." /></a></td>
        <td class="text">
          <a class="group" href="{$more-link}">
            <span class="text">more...</span>
          </a> 
          <xsl:if test="not(local-name(..) = 'group')">
            | <a href="javascript:showAllClusters()">all clusters</a>
          </xsl:if>
        </td>
      </tr>
    </xsl:if>

  </xsl:template>

  <!-- Skip documents -->
  <xsl:template match="document" />

  <!-- Skip query -->
  <xsl:template match="query" />
</xsl:stylesheet>
