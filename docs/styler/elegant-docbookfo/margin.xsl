<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">
                
  <xsl:template match="marginnote">
    <fo:float float="left">
      <fo:block-container width="{$marginbar.width}"
                          space-before="{$marginbar.space-before}"
                          start-indent="0mm" end-indent="0mm">
        <fo:block font-weight="bold" font-style="italic" hyphenate="false"
                  text-align="right" line-height="1">
          <xsl:apply-templates/>
        </fo:block>
      </fo:block-container>
    </fo:float>
  </xsl:template>

</xsl:stylesheet>


