<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>

  <xsl:template name="formal.object.heading">
    <xsl:param name="object" select="."/>
    <xsl:param name="placement" select="'before'"/>

    <fo:float float="left">
      <fo:block-container width="{$marginbar.width}"
                          space-before="{$marginbar.space-before}"
                          start-indent="0mm" end-indent="0mm">
        <fo:block font-weight="bold" font-style="italic" hyphenate="false"
                  text-align="right" line-height="1">
          <xsl:variable name="template">
            <xsl:call-template name="gentext.template">
              <xsl:with-param name="context" select="'xref'"/>
              <xsl:with-param name="name">
                <xsl:call-template name="xpath.location"/>
              </xsl:with-param>
            </xsl:call-template> 
          </xsl:variable>
          <xsl:call-template name="substitute-markup">
            <xsl:with-param name="allow-anchors" select="'1'"/>
            <xsl:with-param name="template">
              <xsl:value-of select="$template"/>
            </xsl:with-param>
          </xsl:call-template>          
        </fo:block>
        <fo:block font-style="italic" hyphenate="false"
                  text-align="right" line-height="1">
          <xsl:call-template name="substitute-markup">
            <xsl:with-param name="allow-anchors" select="'1'"/>
            <xsl:with-param name="template" select="'%t'"/>
          </xsl:call-template>
        </fo:block>
      </fo:block-container>
    </fo:float>
  </xsl:template>

</xsl:stylesheet>
