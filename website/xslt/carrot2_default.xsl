<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="sin-generic-base.xsl" />
<xsl:include href="sin-html-markup.xsl" />
<xsl:include href="sin-i18n.xsl" />
<xsl:include href="sin-illustration.xsl" />
<xsl:include href="carrot2_sitemap.xsl" />
<xsl:include href="carrot2_elements.xsl" />

<xsl:output method="html" indent="no" encoding="UTF-8" />

<xsl:template match="page">
    <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"&gt;</xsl:text>
    <xsl:variable name="base"><xsl:call-template name="rewriteImgURL"><xsl:with-param name="href" select="'/gfx/carrot2'" /></xsl:call-template></xsl:variable>
    <html>
        <head>
            <meta name="AUTHOR" content="Dawid Weiss" />
            <meta name="COPYRIGHT" content="(c) 2000-2002 Dawid Weiss" />
            
            <link rel="SHORTCUT ICON" type="image/x-icon">
                <xsl:attribute name="href"><xsl:value-of select="concat($docsBaseURL,'/favicon.ico')"/></xsl:attribute>
            </link>
 
            <link REL="stylesheet" TYPE="text/css" TITLE="style" MEDIA="all">
                <xsl:attribute name="href"><xsl:value-of select="concat($cssBaseURL,'/carrot2.css')"/></xsl:attribute>
            </link>
            <link REL="stylesheet" TYPE="text/css" TITLE="style" MEDIA="print">
                <xsl:attribute name="href"><xsl:value-of select="concat($cssBaseURL,'/carrot2_print.css')"/></xsl:attribute>
            </link>

            <title>
                <xsl:choose>
                    <xsl:when test="title"><xsl:apply-templates select="title" /></xsl:when>
                    <xsl:otherwise>Carrot<sup>2</sup></xsl:otherwise>
                </xsl:choose>
            </title>
            
            <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        </head>

        <body class="page" leftmargin="0" topmargin="0">
            <xsl:call-template name="control-panel" />
            
            <table cellspacing="0" cellpadding="0" border="0" width="800" height="100%">
            <tr><td width="180"><img src="{concat($gfxBaseURL,'/','empty.gif')}" width="180" height="1" /></td>
                <td width="600"><img src="{concat($gfxBaseURL,'/','empty.gif')}" width="600" height="1" /></td></tr>
            <tr>
                <!-- left pane -->
                <td align="left" valign="top" width="180">
                    <img style="margin-left: 15px; margin-bottom: 10px; margin-top: 10px;" src="{concat($base,'/Carrot2-infopage.gif')}" width="66" height="80" />

                    <xsl:call-template name="sitemap-hierarchical">
                        <xsl:with-param name="level" select="'2'" />
                        <xsl:with-param name="topSection" select="'carrot2'" />
                    </xsl:call-template>
                </td>
                <td align="left" valign="top" rowspan="2">
                    <xsl:apply-templates />
                </td>
            </tr>
            </table>
        </body>
    </html>
</xsl:template>


<xsl:template match="page/title"></xsl:template>


<xsl:template match="chapter">
    <xsl:apply-templates select="*[name()='title']" />
    <div class="chapter_nested_{count(ancestor::chapter)}">
        <xsl:apply-templates select="*[name()!='title']" />
    </div>
</xsl:template>


<xsl:template match="chapter/title">
    <div class="chapter_nested_{count(ancestor::chapter)-1}_title">
        <xsl:apply-templates />
    </div>
</xsl:template>


<!-- ################################### -->
<!-- {{{ I80N -->
<!-- ################################### -->

<xsl:template match="stub">
<xsl:apply-templates />
</xsl:template>

<!-- }}} -->


</xsl:stylesheet>

