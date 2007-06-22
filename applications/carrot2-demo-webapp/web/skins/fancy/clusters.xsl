<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />
  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8"
       doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <xsl:variable name="more-increment-internal"><xsl:value-of select="normalize-space(processing-instruction('more-increment'))" /></xsl:variable>
  <xsl:param name="more-increment"><xsl:choose><xsl:when test="$more-increment-internal"><xsl:value-of select="$more-increment-internal" /></xsl:when><xsl:otherwise>10</xsl:otherwise></xsl:choose></xsl:param>

  <xsl:template name="head-insert">
    <link rel="stylesheet" href="{$skinuri}/css/clusters.css" />
<xsl:text disable-output-escaping="yes">&lt;!--[if IE]&gt;</xsl:text>
    <link rel="stylesheet" href="{$skinuri}/css/clusters-ie.css" />
<xsl:text disable-output-escaping="yes">&lt;![endif]--&gt;</xsl:text>
    <script type="text/javascript" src="{$skinuri}/js/yui/yahoo-dom-event.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/yui/history-experimental-min.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/DOM.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/Cookies.js" ></script>
    <script type="text/javascript" src="{$skinuri}/js/Clusters.js"></script>
    <script type="text/javascript">
initHistory();
    </script>
  </xsl:template>

  <xsl:template name="body-insert">
    <script type="text/javascript">
YAHOO.util.History.initialize("<xsl:value-of select="$contextPath" />/blank.html");

var clusterDocs = new Array();
<xsl:for-each select="//group">clusterDocs['<xsl:value-of select="@hash" />']=[<xsl:value-of select="@docs" />];</xsl:for-each>
var clusterWords = new Array();
<xsl:for-each select="//group">clusterWords['<xsl:value-of select="@hash" />']=[<xsl:value-of select="@words" />];</xsl:for-each>
var docClusters = computeDocClusters(clusterDocs);

YAHOO.util.Event.addListener(window, "load", init);
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
            <span class="group" id="top-link"><span class="label"><span class="text">
            All results</span>&#160;<span class="size">(<xsl:value-of select="@totalResultsCount" />)</span></span></span>
          </td>
        </tr>

        <xsl:apply-templates select="group" />
      </table>
    </xsl:if>

    <div id="always-all">
      <span class="link" id="always-all-link">Always show all clusters</span>
      <span id="always-all-done"> (saved)</span>
    </div>

    <xsl:if test="count(group) = 0">
      <div id="no-clusters">
        <xsl:value-of select="/searchresult/strings/no-clusters-created" />
      </div>
    </xsl:if>

    <span id="ctime" style="display: none"><xsl:value-of select="time/@clustering" /></span>
  </xsl:template>

  <xsl:template match="group">
    <xsl:param name="id"><xsl:value-of select="@hash" /></xsl:param>
    <xsl:param name="parent-id"><xsl:value-of select="../@hash" /></xsl:param>
    <xsl:param name="parent-id-position"><xsl:value-of select="concat($parent-id, '|', position())" /></xsl:param>

    <tr id="{$parent-id-position}">
      <xsl:if test="not(count(group) = 0)">
        <xsl:attribute name="class">ngr<xsl:value-of select="$id" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="count(group) = 0">
        <xsl:attribute name="class">lgr<xsl:value-of select="$id" /></xsl:attribute>
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
        <xsl:variable name="label"><xsl:choose><xsl:when test="@junk"><xsl:value-of select="/searchresult/strings/other-topics" /></xsl:when><xsl:otherwise><xsl:value-of select="title/phrase[1]" /></xsl:otherwise> </xsl:choose></xsl:variable>
        <span class="group">
          <a name="{$id}"><span class="label"><span class="text"><xsl:value-of select="$label" /></span>&#160;<span class="size">(<xsl:value-of select="@unique-docs" />)</span></span></a></span>
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
      <tr id="{concat($parent-id, 'more', position())}" class="more{position() + 1}-{position() + $more-increment}">
        <xsl:if test="position() div $more-increment &gt; 1">
          <xsl:attribute name="style">display: none</xsl:attribute>
        </xsl:if>

        <td>
          <img class="fold-nt" src="{$skinuri}/img/b-no-tree.gif" alt="..." />
        </td>
        <td class="fold"><img src="{$skinuri}/img/folder-more.gif" class="f" alt="..." /></td>
        <td class="text">
          <span class="group">
            <span class="text"><xsl:value-of select="/searchresult/strings/more-clusters" /></span>
          </span>
          | <span class="sac group"><xsl:value-of select="/searchresult/strings/show-all-clusters" /></span>
        </td>
      </tr>
    </xsl:if>

  </xsl:template>

  <!-- Skip documents -->
  <xsl:template match="document" />

  <!-- Skip query -->
  <xsl:template match="query" />
</xsl:stylesheet>
