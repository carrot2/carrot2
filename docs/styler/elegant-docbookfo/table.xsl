<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

  <xsl:template match="thead">
    <xsl:variable name="tgroup" select="parent::*"/>

    <fo:table-header border-after-color="black" border-after-style="solid" border-after-width="0.1mm" font-family="SansSerif-Bold" font-size="90%">
      <xsl:apply-templates select="row[1]">
        <xsl:with-param name="spans">
          <xsl:call-template name="blank.spans">
            <xsl:with-param name="cols" select="../@cols"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:apply-templates>
    </fo:table-header>
  </xsl:template>

  <xsl:template name="table.frame">
    <xsl:variable name="frame">
      <xsl:choose>
        <xsl:when test="@frame">
          <xsl:value-of select="@frame"/>
        </xsl:when>
        <xsl:otherwise>topbot</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$frame='all'">
        <xsl:attribute name="border-left-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-right-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-left-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-right-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-left-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
        <xsl:attribute name="border-right-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$frame='bottom'">
        <xsl:attribute name="border-left-style">none</xsl:attribute>
        <xsl:attribute name="border-right-style">none</xsl:attribute>
        <xsl:attribute name="border-top-style">none</xsl:attribute>
        <xsl:attribute name="border-bottom-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$frame='sides'">
        <xsl:attribute name="border-left-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-right-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-style">none</xsl:attribute>
        <xsl:attribute name="border-bottom-style">none</xsl:attribute>
        <xsl:attribute name="border-left-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-right-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-left-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
        <xsl:attribute name="border-right-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$frame='top'">
        <xsl:attribute name="border-left-style">none</xsl:attribute>
        <xsl:attribute name="border-right-style">none</xsl:attribute>
        <xsl:attribute name="border-top-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-style">none</xsl:attribute>
        <xsl:attribute name="border-top-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$frame='topbot'">
        <xsl:attribute name="border-left-style">none</xsl:attribute>
        <xsl:attribute name="border-right-style">none</xsl:attribute>
        <xsl:attribute name="border-top-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-style">
          <xsl:value-of select="$table.frame.border.style"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-width">
          <xsl:value-of select="$table.frame.border.thickness"/>
        </xsl:attribute>
        <xsl:attribute name="border-top-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
        <xsl:attribute name="border-bottom-color">
          <xsl:value-of select="$table.frame.border.color"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$frame='none'">
        <xsl:attribute name="border-left-style">none</xsl:attribute>
        <xsl:attribute name="border-right-style">none</xsl:attribute>
        <xsl:attribute name="border-top-style">none</xsl:attribute>
        <xsl:attribute name="border-bottom-style">none</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>Impossible frame on table: </xsl:text>
          <xsl:value-of select="$frame"/>
        </xsl:message>
        <xsl:attribute name="border-left-style">none</xsl:attribute>
        <xsl:attribute name="border-right-style">none</xsl:attribute>
        <xsl:attribute name="border-top-style">none</xsl:attribute>
        <xsl:attribute name="border-bottom-style">none</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
