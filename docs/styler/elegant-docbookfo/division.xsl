<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version='1.0'>

  <!-- added start-indent and end-indent to fo:flow where appropriate -->
  <xsl:template match="book">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="preamble"
                  select="title|subtitle|titleabbrev|bookinfo"/>

    <xsl:variable name="content"
                  select="*[not(self::title or self::subtitle
                              or self::titleabbrev
                              or self::bookinfo)]"/>

    <xsl:variable name="titlepage-master-reference">
      <xsl:call-template name="select.pagemaster">
        <xsl:with-param name="pageclass" select="'titlepage'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="lot-master-reference">
      <xsl:call-template name="select.pagemaster">
        <xsl:with-param name="pageclass" select="'lot'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$preamble">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        master-reference="{$titlepage-master-reference}"
                        initial-page-number="1">
        <xsl:attribute name="language">
          <xsl:call-template name="l10n.language"/>
        </xsl:attribute>
        <xsl:attribute name="format">
          <xsl:call-template name="page.number.format"/>
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
          <xsl:with-param name="master-reference" select="$titlepage-master-reference"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$titlepage-master-reference"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <fo:block id="{$id}">
            <xsl:call-template name="book.titlepage"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:apply-templates select="dedication" mode="dedication"/>

    <xsl:variable name="toc.params">
      <xsl:call-template name="find.path.params">
        <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="contains($toc.params, 'toc')">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        format="i"
                        master-reference="{$lot-master-reference}">
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
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'TableofContents'"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'TableofContents'"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <xsl:call-template name="division.toc"/>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:if test="contains($toc.params,'figure') and .//figure">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        format="i"
                        master-reference="{$lot-master-reference}">
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
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofFigures'"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofFigures'"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <xsl:call-template name="list.of.titles">
            <xsl:with-param name="titles" select="'figure'"/>
            <xsl:with-param name="nodes" select=".//figure"/>
          </xsl:call-template>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:if test="contains($toc.params,'table') and .//table">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        format="i"
                        master-reference="{$lot-master-reference}">
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
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofTables'"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofTables'"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <xsl:call-template name="list.of.titles">
            <xsl:with-param name="titles" select="'table'"/>
            <xsl:with-param name="nodes" select=".//table"/>
          </xsl:call-template>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:if test="contains($toc.params,'example') and .//example">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        format="i"
                        master-reference="{$lot-master-reference}">
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
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofExamples'"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofExamples'"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <xsl:call-template name="list.of.titles">
            <xsl:with-param name="titles" select="'example'"/>
            <xsl:with-param name="nodes" select=".//example"/>
          </xsl:call-template>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:if test="contains($toc.params,'equation') and .//equation">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        format="i"
                        master-reference="{$lot-master-reference}">
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
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofEquations'"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofEquations'"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <xsl:call-template name="list.of.titles">
            <xsl:with-param name="titles" select="'equation'"/>
            <xsl:with-param name="nodes" select=".//equation[title]"/>
          </xsl:call-template>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:if test="contains($toc.params,'procedure') and .//procedure">
      <fo:page-sequence hyphenate="{$hyphenate}"
                        format="i"
                        master-reference="{$lot-master-reference}">
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
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofProcedures'"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="running.foot.mode">
          <xsl:with-param name="master-reference" select="$lot-master-reference"/>
          <xsl:with-param name="gentext-key" select="'ListofProcedures'"/>
        </xsl:apply-templates>

        <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.indent-size}" end-indent="{$marginbar.indent-size}">
          <xsl:call-template name="list.of.titles">
            <xsl:with-param name="titles" select="'procedure'"/>
            <xsl:with-param name="nodes" select=".//procedure[title]"/>
          </xsl:call-template>
        </fo:flow>
      </fo:page-sequence>
    </xsl:if>

    <xsl:apply-templates select="$content"/>
  </xsl:template>

</xsl:stylesheet>
