<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>

  <!-- added start-indent and ent-indent to fo:flow -->
  <xsl:template match="toc">
    <xsl:variable name="master-reference">
      <xsl:call-template name="select.pagemaster"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="*">
        <xsl:if test="$process.source.toc != 0">
          <!-- if the toc isn't empty, process it -->
          <fo:page-sequence hyphenate="{$hyphenate}"
                            master-reference="{$master-reference}">
            <xsl:attribute name="language">
              <xsl:call-template name="l10n.language"/>
            </xsl:attribute>
            <xsl:attribute name="format">
              <xsl:call-template name="page.number.format">
                <xsl:with-param name="element" select="'toc'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:if test="$double.sided != 0">
              <xsl:attribute name="initial-page-number">auto-odd</xsl:attribute>
            </xsl:if>

            <xsl:attribute name="hyphenation-character">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'hyphenation-character'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="hyphenation-push-character-count">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'hyphenation-push-character-count'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="hyphenation-remain-character-count">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'hyphenation-remain-character-count'"/>
              </xsl:call-template>
            </xsl:attribute>

            <xsl:apply-templates select="." mode="running.head.mode">
              <xsl:with-param name="master-reference" select="$master-reference"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="." mode="running.foot.mode">
              <xsl:with-param name="master-reference" select="$master-reference"/>
            </xsl:apply-templates>

            <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
              <fo:block xsl:use-attribute-sets="toc.margin.properties">
                <xsl:call-template name="table.of.contents.titlepage"/>
                <xsl:apply-templates/>
              </fo:block>
            </fo:flow>
          </fo:page-sequence>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$process.empty.source.toc != 0">
          <fo:page-sequence hyphenate="{$hyphenate}"
                            master-reference="{$master-reference}">
            <xsl:attribute name="language">
              <xsl:call-template name="l10n.language"/>
            </xsl:attribute>
            <xsl:attribute name="format">
              <xsl:call-template name="page.number.format">
                <xsl:with-param name="element" select="'toc'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:if test="$double.sided != 0">
              <xsl:attribute name="initial-page-number">auto-odd</xsl:attribute>
            </xsl:if>

            <xsl:attribute name="hyphenation-character">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'hyphenation-character'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="hyphenation-push-character-count">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'hyphenation-push-character-count'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="hyphenation-remain-character-count">
              <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'hyphenation-remain-character-count'"/>
              </xsl:call-template>
            </xsl:attribute>

            <xsl:apply-templates select="." mode="running.head.mode">
              <xsl:with-param name="master-reference" select="$master-reference"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="." mode="running.foot.mode">
              <xsl:with-param name="master-reference" select="$master-reference"/>
            </xsl:apply-templates>

            <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
              <xsl:choose>
                <xsl:when test="parent::section
                                or parent::sect1
                                or parent::sect2
                                or parent::sect3
                                or parent::sect4
                                or parent::sect5">
                  <xsl:apply-templates select="parent::*"
                                       mode="toc.for.section"/>
                </xsl:when>
                <xsl:when test="parent::article">
                  <xsl:apply-templates select="parent::*"
                                       mode="toc.for.component"/>
                </xsl:when>
                <xsl:when test="parent::book
                                or parent::part">
                  <xsl:apply-templates select="parent::*"
                                       mode="toc.for.division"/>
                </xsl:when>
                <xsl:when test="parent::set">
                  <xsl:apply-templates select="parent::*"
                                       mode="toc.for.set"/>
                </xsl:when>
                <!-- there aren't any other contexts that allow toc -->
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>I don't know how to make a TOC in this context!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </fo:flow>
          </fo:page-sequence>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
