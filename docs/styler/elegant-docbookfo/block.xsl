<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

  <!-- keep-with-next added -->
  <xsl:template match="simpara">
    <fo:block text-align="justify">
      <xsl:if test="@keep-with-next">
        <xsl:attribute name="keep-with-next.within-page">always</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <!-- big left margin added -->
  <xsl:template match="epigraph">
    <fo:block font-style="italic" text-align="justify" 
              margin-left="50mm" margin-right="0mm" margin-bottom="5mm">
      <xsl:call-template name="anchor"/>
      <xsl:apply-templates select="para|simpara|formalpara|literallayout"/>
      <fo:block text-align="right">
        <fo:inline>
          <xsl:text>--</xsl:text>
          <xsl:apply-templates select="attribution"/>
        </fo:inline>
      </fo:block>
    </fo:block>
  </xsl:template> 

  <xsl:template match="blockquote">
    <!-- workaround: use different attribute set when inside a listitem -->
    <xsl:choose>
      <xsl:when test="ancestor::listitem[1]">
        <fo:block xsl:use-attribute-sets="blockquote.properties.in-list-item-body">
          <xsl:call-template name="anchor"/>
          <fo:block>
            <xsl:if test="title">
              <fo:block xsl:use-attribute-sets="formal.title.properties">
                <xsl:apply-templates select="." mode="object.title.markup"/>
              </fo:block>
            </xsl:if>
            <xsl:apply-templates select="*[local-name(.) != 'title'
                                         and local-name(.) != 'attribution']"/>
          </fo:block>
          <xsl:if test="attribution">
            <fo:block text-align="right">
              <!-- mdash -->
              <xsl:text>&#x2014;</xsl:text>
              <xsl:apply-templates select="attribution"/>
            </fo:block>
          </xsl:if>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <fo:block xsl:use-attribute-sets="blockquote.properties">
          <xsl:call-template name="anchor"/>
          <fo:block>
            <xsl:if test="title">
              <fo:block xsl:use-attribute-sets="formal.title.properties">
                <xsl:apply-templates select="." mode="object.title.markup"/>
              </fo:block>
            </xsl:if>
            <xsl:apply-templates select="*[local-name(.) != 'title'
                                         and local-name(.) != 'attribution']"/>
          </fo:block>
          <xsl:if test="attribution">
            <fo:block text-align="right">
              <!-- mdash -->
              <xsl:text>&#x2014;</xsl:text>
              <xsl:apply-templates select="attribution"/>
            </fo:block>
          </xsl:if>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>



</xsl:stylesheet>

