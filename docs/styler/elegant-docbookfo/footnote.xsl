<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:exsl="http://exslt.org/common"
                exclude-result-prefixes="exsl"
                version='1.0'>

  <xsl:template name="format.footnote.mark">
    <xsl:param name="mark" select="'?'"/>
    <fo:inline baseline-shift="{$superscript.baseline-shift}" font-size="{$superscript.font-size}">
      <xsl:copy-of select="$mark"/>
    </fo:inline>
  </xsl:template>

  <!-- Added start-indent and end-indent for the marginbar -->
  <xsl:template match="footnote">
    <xsl:choose>
      <xsl:when test="ancestor::tgroup">
        <xsl:call-template name="format.footnote.mark">
          <xsl:with-param name="mark">
            <xsl:apply-templates select="." mode="footnote.number"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <fo:footnote>
          <fo:inline>
            <xsl:call-template name="format.footnote.mark">
              <xsl:with-param name="mark">
                <xsl:apply-templates select="." mode="footnote.number"/>
              </xsl:with-param>
            </xsl:call-template>
          </fo:inline>
          <fo:footnote-body font-family="{$body.font.family}"
                            font-size="{$footnote.font.size}"
                            font-weight="normal"
                            font-style="normal"
                            start-indent="{$marginbar.indent-size}"
                            end-indent="{$marginbar.indent-size}">
            <xsl:apply-templates/>
          </fo:footnote-body>
        </fo:footnote>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Added margin between footnote mark and footnote text -->
  <xsl:template match="footnote/para[1]
                       |footnote/simpara[1]
                       |footnote/formalpara[1]"
                priority="2">
    <!-- this only works if the first thing in a footnote is a para, -->
    <!-- which is ok, because it usually is. -->
    <fo:block>
      <fo:inline margin-right="{$footnote.gap}">
        <xsl:call-template name="format.footnote.mark">
          <xsl:with-param name="mark">
            <xsl:apply-templates select="ancestor::footnote" mode="footnote.number"/>
          </xsl:with-param>
        </xsl:call-template>
        </fo:inline>
        <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>
