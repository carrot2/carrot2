<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

  <xsl:template match="accel">
    <fo:inline font-family="SansSerif" font-size="90%">
      <xsl:call-template name="inline.charseq"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="guilabel">
    <fo:inline font-family="SansSerif" font-size="90%">
      <xsl:call-template name="inline.charseq"/>
    </fo:inline>
  </xsl:template>

  <!-- Customizable baseline-shift and font-size -->
  <xsl:template name="inline.subscriptseq">
    <xsl:param name="content">
      <xsl:apply-templates/>
    </xsl:param>
    <fo:inline baseline-shift="{$subscript.baseline-shift}" font-size="{$subscript.font-size}"><xsl:copy-of select="$content"/></fo:inline>
  </xsl:template>

  <!-- Customizable baseline-shift and font-size -->
  <xsl:template name="inline.superscriptseq">
    <xsl:param name="content">
      <xsl:apply-templates/>
    </xsl:param>
    <fo:inline baseline-shift="{$superscript.baseline-shift}" font-size="{$superscript.font-size}"><xsl:copy-of select="$content"/></fo:inline>
  </xsl:template>

  <!-- Glossterms/firstterms made bold -->
  <xsl:template match="glossterm" name="glossterm">
    <xsl:param name="firstterm" select="0"/>

    <xsl:choose>
      <xsl:when test="($firstterm.only.link = 0 or $firstterm = 1) and @linkend">
        <fo:basic-link internal-destination="{@linkend}"
                       xsl:use-attribute-sets="xref.properties">
          <xsl:call-template name="inline.charseq"/>
        </fo:basic-link>
      </xsl:when>

      <xsl:when test="not(@linkend)
                      and ($firstterm.only.link = 0 or $firstterm = 1)
                      and $glossary.collection != ''">
        <xsl:variable name="term">
          <xsl:choose>
            <xsl:when test="@baseform"><xsl:value-of select="@baseform"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="cterm"
             select="(document($glossary.collection,.)//glossentry[glossterm=$term])[1]"/>

        <xsl:choose>
          <xsl:when test="not($cterm)">
            <xsl:message>
              <xsl:text>There's no entry for </xsl:text>
              <xsl:value-of select="$term"/>
              <xsl:text> in </xsl:text>
              <xsl:value-of select="$glossary.collection"/>
            </xsl:message>
            <xsl:call-template name="inline.boldseq"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="id">
              <xsl:text>gl.</xsl:text>
              <xsl:choose>
                <xsl:when test="$cterm/@id">
                  <xsl:value-of select="$cterm/@id"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="generate-id($cterm)"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <fo:basic-link internal-destination="{$id}"
                           xsl:use-attribute-sets="xref.properties">
              <xsl:call-template name="inline.boldseq"/>
            </fo:basic-link>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:when test="not(@linkend)
                      and ($firstterm.only.link = 0 or $firstterm = 1)
                      and $glossterm.auto.link != 0">
        <xsl:variable name="term">
          <xsl:choose>
            <xsl:when test="@baseform">
              <xsl:value-of select="@baseform"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="targets"
                      select="//glossentry[glossterm=$term or glossterm/@baseform=$term]"/>

        <xsl:variable name="target" select="$targets[1]"/>

        <xsl:choose>
          <xsl:when test="count($targets)=0">
            <xsl:message>
              <xsl:text>Error: no glossentry for glossterm: </xsl:text>
              <xsl:value-of select="."/>
              <xsl:text>.</xsl:text>
            </xsl:message>
            <xsl:call-template name="inline.boldseq"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="termid">
              <xsl:call-template name="object.id">
                <xsl:with-param name="object" select="$target"/>
              </xsl:call-template>
            </xsl:variable>

            <fo:basic-link internal-destination="{$termid}"
                           xsl:use-attribute-sets="xref.properties">
              <xsl:call-template name="inline.charseq"/>
            </fo:basic-link>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="inline.boldseq"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template> 


</xsl:stylesheet>
