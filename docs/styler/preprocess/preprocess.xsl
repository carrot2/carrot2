<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<xsl:param name="remove.images.width" select="'1'" />
<xsl:param name="conditionals"        select="''"  />
<xsl:variable name="normalized"       select="concat(' ',normalize-space($conditionals),' ')"/>


<!-- add a note about work in progress -->
<xsl:template match="section">
    <section>
        <xsl:copy-of select="@*" />
        <xsl:if test="not(contains($normalized,' final '))">
        <note>
        This manual is a work in-progress and there may be inacurracies or
        straight errors that we kindly ask you to report to project administrators.
        Thanks for understanding!
        </note>
        </xsl:if>
        <xsl:apply-templates />
    </section>
</xsl:template>
                
<!--
    Paste an image 
    @element      pasteFigure
    @attribute    id                The id of this figure will be a concatenation of 'figure:' and the id.
                                    If not defined, the file name (without last 4 characters - extension)
                                    is used as an id.
    @attribute    src               Filename (wuthout path) to be used to load the figure.
    @attribute    width             (optional) image width.

    @global       remove.images.width If not '0', width attribute is ignored
-->
<xsl:template match="pasteFigure">
    <figure>
      <xsl:attribute name="id">
        <xsl:if test="@id">
            <xsl:value-of select="concat('figure:',@id)" />
        </xsl:if>
        <xsl:if test="not(@id)">
            <xsl:value-of select="concat('figure:',substring(@src, 0,string-length(@src)-3))" />
        </xsl:if>
      </xsl:attribute>
      <title><xsl:apply-templates /></title>
      <mediaobject>
        <imageobject>
          <imagedata fileref="{concat('figures/',@src)}"
                     format="{translate(substring(@src,string-length(@src)-2),'qwertyuioplkjhgfdsazxcvbnm','QWERTYUIOPLKJHGFDSAZXCVBNM')}"
                     align="center">
          <xsl:if test="$remove.images.width='0' and @width">
            <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
          </xsl:if>
          </imagedata>
        </imageobject>
        <textobject><phrase><xsl:apply-templates select=".//text()" /></phrase></textobject>
      </mediaobject>
    </figure>
</xsl:template>


<!-- Include another file -->

<xsl:template match="include">
    <xsl:apply-templates select="document(@file,.)" />
</xsl:template>


<!-- Conditional rendering -->

<xsl:template match="ifdefined">
    <xsl:if test="contains($normalized,concat(' ',normalize-space(@value),' '))">
        <xsl:apply-templates />
    </xsl:if>
</xsl:template>

<!-- Copy all unrecognized elements -->

<xsl:template match="*|text()|processing-instruction()">
    <xsl:copy><xsl:copy-of select="@*" /><xsl:apply-templates /></xsl:copy>
</xsl:template>

                
</xsl:stylesheet>


