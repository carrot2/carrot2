<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version='1.0'>

  <!-- start-indent and end-indent added to fo:flow  -->
  <xsl:template match="chapter">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="master-reference">
      <xsl:call-template name="select.pagemaster"/>
    </xsl:variable>

    <fo:page-sequence hyphenate="{$hyphenate}"
                      master-reference="{$master-reference}">
      <xsl:attribute name="language">
        <xsl:call-template name="l10n.language"/>
      </xsl:attribute>
      <xsl:attribute name="format">
        <xsl:call-template name="page.number.format"/>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="not(preceding::chapter
                            or preceding::appendix
                            or preceding::article
                            or preceding::dedication
                            or parent::part
                            or parent::reference)">
          <!-- if there is a preceding component or we're in a part, the -->
          <!-- page numbering will already be adjusted -->
          <xsl:attribute name="initial-page-number">1</xsl:attribute>
        </xsl:when>
        <xsl:when test="$double.sided != 0">
          <xsl:attribute name="initial-page-number">auto-odd</xsl:attribute>
        </xsl:when>
      </xsl:choose>

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
        <fo:block id="{$id}">
          <xsl:call-template name="chapter.titlepage"/>
        </fo:block>

        <xsl:variable name="toc.params">
          <xsl:call-template name="find.path.params">
            <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="contains($toc.params, 'toc')">
          <xsl:call-template name="component.toc"/>
          <xsl:call-template name="component.toc.separator"/>
        </xsl:if>
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
  </xsl:template>

  <!-- start-indent and end-indent added to fo:flow  -->
  <xsl:template match="appendix">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="master-reference">
      <xsl:call-template name="select.pagemaster"/>
    </xsl:variable>

    <fo:page-sequence hyphenate="{$hyphenate}"
                      master-reference="{$master-reference}">
      <xsl:attribute name="language">
        <xsl:call-template name="l10n.language"/>
      </xsl:attribute>
      <xsl:attribute name="format">
        <xsl:call-template name="page.number.format"/>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="not(preceding::chapter
                            or preceding::appendix
                            or preceding::article
                            or preceding::dedication
                            or parent::part
                            or parent::reference)">
          <!-- if there is a preceding component or we're in a part, the -->
          <!-- page numbering will already be adjusted -->
          <xsl:attribute name="initial-page-number">1</xsl:attribute>
        </xsl:when>
        <xsl:when test="$double.sided != 0">
          <xsl:attribute name="initial-page-number">auto-odd</xsl:attribute>
        </xsl:when>
      </xsl:choose>

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
        <fo:block id="{$id}">
          <xsl:call-template name="appendix.titlepage"/>
        </fo:block>

        <xsl:variable name="toc.params">
          <xsl:call-template name="find.path.params">
            <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:if test="contains($toc.params, 'toc')">
          <xsl:call-template name="component.toc"/>
          <xsl:call-template name="component.toc.separator"/>
        </xsl:if>
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
  </xsl:template>

</xsl:stylesheet>
