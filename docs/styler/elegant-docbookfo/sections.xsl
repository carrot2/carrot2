<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version='1.0'>

  <!-- start-indent and end-indent added to fo:flow  -->
  <xsl:template match="/section">
    <xsl:variable name="id">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="ancestor::reference"/>
      </xsl:call-template>
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
        <fo:block id="{$id}">
          <xsl:call-template name="section.titlepage"/>
        </fo:block>

        <xsl:variable name="toc.params">
          <xsl:call-template name="find.path.params">
            <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:if test="contains($toc.params, 'toc')
                      and (count(ancestor::section)+1) &lt;= $generate.section.toc.level">
          <xsl:call-template name="section.toc"/>
          <xsl:call-template name="section.toc.separator"/>
        </xsl:if>

        <xsl:apply-templates/>
     </fo:flow>
    </fo:page-sequence>
  </xsl:template>

  <!-- start-indent and end-indent added to fo:flow  -->
  <xsl:template match="/sect1">
    <xsl:variable name="id">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="ancestor::reference"/>
      </xsl:call-template>
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

      <fo:flow flow-name="xsl-region-body" start-indent="{$marginbar.width + marginbar.margin}" end-indent="{$marginbar.width + marginbar.margin}">
        <fo:block id="{$id}">
          <xsl:call-template name="sect1.titlepage"/>
        </fo:block>

        <xsl:variable name="toc.params">
          <xsl:call-template name="find.path.params">
            <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:if test="contains($toc.params, 'toc')
                      and $generate.section.toc.level &gt;= 1">
          <xsl:call-template name="section.toc"/>
          <xsl:call-template name="section.toc.separator"/>
        </xsl:if>

        <xsl:apply-templates/>
     </fo:flow>
    </fo:page-sequence>
  </xsl:template>

  <!-- added customized sect titles -->
  <xsl:template match="section">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <fo:block id="{$id}">
      <xsl:variable name="sectlevel">
        <xsl:value-of select="count(ancestor::section)+1"/>
      </xsl:variable>

      <xsl:choose>
        <xsl:when test="$sectlevel = 1">
          <xsl:call-template name="sect1.titlepage"/>
        </xsl:when>
        
        <xsl:when test="$sectlevel = 2">
          <xsl:call-template name="sect2.titlepage"/>
        </xsl:when>
        
        <xsl:when test="$sectlevel = 3">
          <xsl:call-template name="sect3.titlepage"/>
        </xsl:when>
        
        <xsl:otherwise>
          <xsl:call-template name="section.titlepage"/>
        </xsl:otherwise>
      </xsl:choose>
      

      <xsl:variable name="toc.params">
        <xsl:call-template name="find.path.params">
          <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
        </xsl:call-template>
      </xsl:variable>

      <xsl:if test="contains($toc.params, 'toc')
                    and (count(ancestor::section)+1) &lt;= $generate.section.toc.level">
        <xsl:call-template name="section.toc">
          <xsl:with-param name="toc.title.p" select="contains($toc.params, 'title')"/>
        </xsl:call-template>
       <xsl:call-template name="section.toc.separator"/>
      </xsl:if>

      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
</xsl:stylesheet>
