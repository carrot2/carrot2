<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>

  <xsl:template name="process.image">
    <!-- When this template is called, the current node should be  -->
    <!-- a graphic, inlinegraphic, imagedata, or videodata. All    -->
    <!-- those elements have the same set of attributes, so we can -->
    <!-- handle them all in one place.                             -->

    <xsl:variable name="scalefit">
      <xsl:choose>
        <xsl:when test="$ignore.image.scaling != 0">0</xsl:when>
        <xsl:when test="@contentwidth or @contentdepth">0</xsl:when>
        <xsl:when test="@scale">0</xsl:when>
        <xsl:when test="@scalefit"><xsl:value-of select="@scalefit"/></xsl:when>
        <xsl:when test="@width or @depth">1</xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="scale">
      <xsl:choose>
        <xsl:when test="$ignore.image.scaling != 0">0</xsl:when>
        <xsl:when test="@contentwidth or @contentdepth">1.0</xsl:when>
        <xsl:when test="@scale">
          <xsl:value-of select="@scale div 100.0"/>
        </xsl:when>
        <xsl:otherwise>1.0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="filename">
      <xsl:choose>
        <xsl:when test="local-name(.) = 'graphic'
                        or local-name(.) = 'inlinegraphic'">
          <!-- handle legacy graphic and inlinegraphic by new template --> 
          <xsl:call-template name="mediaobject.filename">
            <xsl:with-param name="object" select="."/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <!-- imagedata, videodata, audiodata -->
          <xsl:call-template name="mediaobject.filename">
            <xsl:with-param name="object" select=".."/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="bgcolor">
      <xsl:call-template name="dbfo-attribute">
        <xsl:with-param name="pis"
                        select="../processing-instruction('dbfo')"/>
        <xsl:with-param name="attribute" select="'background-color'"/>
      </xsl:call-template>
    </xsl:variable>

    <fo:external-graphic>
      <xsl:attribute name="src">
        <xsl:call-template name="fo-external-image">
          <xsl:with-param name="filename" select="$filename"/>
        </xsl:call-template>
      </xsl:attribute>

      <xsl:attribute name="width">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@width,'%')">
            <xsl:value-of select="@width"/>
          </xsl:when>
          <xsl:when test="@width">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@width"/>
              <xsl:with-param name="default.units" select="'px'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>auto</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:attribute name="height">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@depth,'%')">
            <xsl:value-of select="@depth"/>
          </xsl:when>
          <xsl:when test="@depth">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@depth"/>
              <xsl:with-param name="default.units" select="'px'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>auto</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:attribute name="content-width">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@contentwidth,'%')">
            <xsl:value-of select="@contentwidth"/>
          </xsl:when>
          <xsl:when test="@contentwidth">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@contentwidth"/>
              <xsl:with-param name="default.units" select="'px'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="number($scale) != 1.0">
            <xsl:value-of select="$scale * 100"/>
            <xsl:text>%</xsl:text>
          </xsl:when>
          <xsl:otherwise>auto</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:attribute name="content-height">
        <xsl:choose>
          <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
          <xsl:when test="contains(@contentdepth,'%')">
            <xsl:value-of select="@contentdepth"/>
          </xsl:when>
          <xsl:when test="@contentdepth">
            <xsl:call-template name="length-spec">
              <xsl:with-param name="length" select="@contentdepth"/>
              <xsl:with-param name="default.units" select="'px'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="number($scale) != 1.0">
            <xsl:value-of select="$scale * 100"/>
            <xsl:text>%</xsl:text>
          </xsl:when>
          <xsl:otherwise>auto</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:if test="$bgcolor != ''">
        <xsl:attribute name="background-color">
          <xsl:value-of select="$bgcolor"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@align">
        <xsl:attribute name="text-align">
          <xsl:value-of select="@align"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@valign">
        <xsl:attribute name="display-align">
          <xsl:choose>
            <xsl:when test="@valign = 'top'">before</xsl:when>
            <xsl:when test="@valign = 'middle'">center</xsl:when>
            <xsl:when test="@valign = 'bottom'">after</xsl:when>
            <xsl:otherwise>auto</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="@border = 'yes'">
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-width"><xsl:value-of select="$graphic.border-width"/></xsl:attribute>
      </xsl:if>
    </fo:external-graphic>
  </xsl:template>

</xsl:stylesheet>
