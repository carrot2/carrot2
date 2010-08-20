<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:d="http://docbook.org/ns/docbook" version="1.0">

  <xsl:import href="http://docbook.sourceforge.net/release/xsl-ns/current/html/docbook.xsl"/>
  <xsl:import href="distribution-links.xsl"/>
  <xsl:import href="customization.xsl"/>
  <xsl:import href="attributes.xsl"/>
  <xsl:import href="i10n.xsl"/>

  <xsl:output indent="yes" omit-xml-declaration="yes"
       media-type="text/html" encoding="UTF-8"
       doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
       doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <xsl:param name="title-logo-file">img/logo.gif</xsl:param>
  <xsl:param name="title-logo-alt">Carrot2 Clustering Engine</xsl:param>

  <xsl:template match="processing-instruction('linebreak')"><br /></xsl:template>

  <xsl:param name="section.autolabel.max.depth">2</xsl:param>
  <xsl:param name="section.autolabel">1</xsl:param>
  <xsl:param name="section.label.includes.component.label">1</xsl:param>
  <xsl:param name="xref.with.number.and.title">0</xsl:param>
  <xsl:param name="admon.style"></xsl:param>
  <xsl:param name="formal.object.break.after">0</xsl:param>
  <xsl:param name="runinhead.default.title.end.punct"></xsl:param>
  <xsl:param name="qanda.defaultlabel">none</xsl:param>
  <xsl:param name="callout.graphics.path">img/callouts/</xsl:param>

  <xsl:template name="user.head.content">
    <link rel="stylesheet" type="text/css" href="css/elegant-common.css" media="all" />
    <link rel="stylesheet" type="text/css" href="css/elegant-common-custom.css" media="all" />
    <link rel="stylesheet" type="text/css" href="css/elegant-screen.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="css/elegant-print.css" media="print" />
    <xsl:text disable-output-escaping="yes">&lt;!--[if IE]&gt;<![CDATA[<link rel="stylesheet" type="text/css" href="css/elegant-common-ie.css" />]]>&lt;![endif]--&gt;</xsl:text>
  </xsl:template>
  
  <xsl:template name="body.attributes">
  </xsl:template>

  <xsl:template name="book.titlepage.before.recto">
    <img id="title-logo" src="{$title-logo-file}">
      <xsl:attribute name="alt"><xsl:value-of select="$title-logo-alt" /></xsl:attribute>
    </img>
  </xsl:template>

  <xsl:param name="toc.section.depth">2</xsl:param>
  <xsl:param name="generate.toc">
appendix  toc,title
article/appendix  nop
article   toc,title
book      toc,title,figure,table,example,equation
chapter   title
part      toc,title
preface   toc,title
qandadiv  toc
qandaset  nop
reference toc,title
sect1     nop
sect2     nop
sect3     nop
sect4     nop
sect5     nop
section   nop
set       toc,title
  </xsl:param>

  <xsl:template match="d:section[@role = 'notoc']"  mode="toc" />
  
  <xsl:template name="user.footer.content">
    <xsl:param name="node" select="."/>
    
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-317750-1");
pageTracker._trackPageview();
</script>
  </xsl:template>

  <!--
    Overridden to introduce additional tags around gentext templates
  -->
  <xsl:template name="substitute-markup">
    <xsl:param name="template" select="''"/>
    <xsl:param name="allow-anchors" select="'0'"/>
    <xsl:param name="title" select="''"/>
    <xsl:param name="subtitle" select="''"/>
    <xsl:param name="docname" select="''"/>
    <xsl:param name="label" select="''"/>
    <xsl:param name="pagenumber" select="''"/>
    <xsl:param name="purpose"/>
    <xsl:param name="xrefstyle"/>
    <xsl:param name="referrer"/>
    <xsl:param name="verbose"/>

    <xsl:choose>
      <xsl:when test="contains($template, '%')">
        <xsl:variable name="candidate"
               select="substring(substring-after($template, '%'), 1, 1)"/>
        <span class="{$candidate}">
        <xsl:value-of select="substring-before($template, '%')"/>
        <xsl:choose>
          <xsl:when test="$candidate = 't'">
            <xsl:apply-templates select="." mode="insert.title.markup">
              <xsl:with-param name="purpose" select="$purpose"/>
              <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
              <xsl:with-param name="title">
                <xsl:choose>
                  <xsl:when test="$title != ''">
                    <xsl:copy-of select="$title"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:apply-templates select="." mode="title.markup">
                      <xsl:with-param name="allow-anchors" select="$allow-anchors"/>
                      <xsl:with-param name="verbose" select="$verbose"/>
                    </xsl:apply-templates>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$candidate = 's'">
            <xsl:apply-templates select="." mode="insert.subtitle.markup">
              <xsl:with-param name="purpose" select="$purpose"/>
              <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
              <xsl:with-param name="subtitle">
                <xsl:choose>
                  <xsl:when test="$subtitle != ''">
                    <xsl:copy-of select="$subtitle"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:apply-templates select="." mode="subtitle.markup">
                      <xsl:with-param name="allow-anchors" select="$allow-anchors"/>
                    </xsl:apply-templates>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$candidate = 'n'">
            <xsl:apply-templates select="." mode="insert.label.markup">
              <xsl:with-param name="purpose" select="$purpose"/>
              <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
              <xsl:with-param name="label">
                <xsl:choose>
                  <xsl:when test="$label != ''">
                    <xsl:copy-of select="$label"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:apply-templates select="." mode="label.markup"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$candidate = 'p'">
            <xsl:apply-templates select="." mode="insert.pagenumber.markup">
              <xsl:with-param name="purpose" select="$purpose"/>
              <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
              <xsl:with-param name="pagenumber">
                <xsl:choose>
                  <xsl:when test="$pagenumber != ''">
                    <xsl:copy-of select="$pagenumber"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:apply-templates select="." mode="pagenumber.markup"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$candidate = 'o'">
            <!-- olink target document title -->
            <xsl:apply-templates select="." mode="insert.olink.docname.markup">
              <xsl:with-param name="purpose" select="$purpose"/>
              <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
              <xsl:with-param name="docname">
                <xsl:choose>
                  <xsl:when test="$docname != ''">
                    <xsl:copy-of select="$docname"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:apply-templates select="." mode="olink.docname.markup"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$candidate = 'd'">
            <xsl:apply-templates select="." mode="insert.direction.markup">
              <xsl:with-param name="purpose" select="$purpose"/>
              <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
              <xsl:with-param name="direction">
                <xsl:choose>
                  <xsl:when test="$referrer">
                    <xsl:variable name="referent-is-below">
                      <xsl:for-each select="preceding::xref">
                        <xsl:if test="generate-id(.) = generate-id($referrer)">1</xsl:if>
                      </xsl:for-each>
                    </xsl:variable>
                    <xsl:choose>
                      <xsl:when test="$referent-is-below = ''">
                        <xsl:call-template name="gentext">
                          <xsl:with-param name="key" select="'above'"/>
                        </xsl:call-template>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:call-template name="gentext">
                          <xsl:with-param name="key" select="'below'"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>Attempt to use %d in gentext with no referrer!</xsl:message>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$candidate = '%' ">
            <xsl:text>%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>%</xsl:text><xsl:value-of select="$candidate"/>
          </xsl:otherwise>
        </xsl:choose>
        </span>
        <!-- recurse with the rest of the template string -->
        <xsl:variable name="rest"
              select="substring($template,
              string-length(substring-before($template, '%'))+3)"/>
        <xsl:call-template name="substitute-markup">
          <xsl:with-param name="template" select="$rest"/>
          <xsl:with-param name="allow-anchors" select="$allow-anchors"/>
          <xsl:with-param name="title" select="$title"/>
          <xsl:with-param name="subtitle" select="$subtitle"/>
          <xsl:with-param name="docname" select="$docname"/>
          <xsl:with-param name="label" select="$label"/>
          <xsl:with-param name="pagenumber" select="$pagenumber"/>
          <xsl:with-param name="purpose" select="$purpose"/>
          <xsl:with-param name="xrefstyle" select="$xrefstyle"/>
          <xsl:with-param name="referrer" select="$referrer"/>
          <xsl:with-param name="verbose" select="$verbose"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$template"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>

