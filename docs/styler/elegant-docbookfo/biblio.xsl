<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>

  <!-- start-indent and end-indent added to fo:flow  -->
  <xsl:template match="bibliography">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="not(parent::*) or parent::book">
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
              <xsl:call-template name="bibliography.titlepage"/>
            </fo:block>
            <xsl:apply-templates/>
          </fo:flow>
        </fo:page-sequence>
      </xsl:when>
      <xsl:otherwise>
        <fo:block id="{$id}"
                  space-before.minimum="1em"
                  space-before.optimum="1.5em"
                  space-before.maximum="2em">
          <xsl:call-template name="bibliography.titlepage"/>
        </fo:block>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- changed start-indent, split into two blocks -->
  <!-- TODO: better layout for numbered bibliography -->
  <xsl:template match="biblioentry">
    <xsl:variable name="id"><xsl:call-template name="object.id"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="string(.) = ''">
        <xsl:variable name="bib" select="document($bibliography.collection)"/>
        <xsl:variable name="entry" select="$bib/bibliography/*[@id=$id][1]"/>
        <xsl:choose>
          <xsl:when test="$entry">
            <xsl:apply-templates select="$entry"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>No bibliography entry: </xsl:text>
              <xsl:value-of select="$id"/>
              <xsl:text> found in </xsl:text>
              <xsl:value-of select="$bibliography.collection"/>
            </xsl:message>
            <fo:block id="{$id}" xsl:use-attribute-sets="normal.para.spacing">
              <xsl:text>Error: no bibliography entry: </xsl:text>
              <xsl:value-of select="$id"/>
              <xsl:text> found in </xsl:text>
              <xsl:value-of select="$bibliography.collection"/>
            </fo:block>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <fo:block id="{$id}" xsl:use-attribute-sets="biblioentry.spacing" keep-with-next.within-page="always" font-weight="bold">
          <xsl:call-template name="biblioentry.label"/>
        </fo:block>
        <fo:block xsl:use-attribute-sets="biblioentry.spacing" space-after="{$biblioentry.space-after}"
                  start-indent="from-parent() + {$biblioentry.start-indent}">
          <xsl:apply-templates mode="bibliography.mode"/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template> 
</xsl:stylesheet>
