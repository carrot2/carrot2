<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="sin-generic-base.xsl" />
<xsl:include href="sin-html-markup.xsl" />
<xsl:include href="sin-i18n.xsl" />
<xsl:include href="carrot2_sitemap.xsl" />
<xsl:include href="carrot2_elements.xsl" />

<xsl:output method="html" indent="no" encoding="UTF-8" />

<xsl:template match="page"><xsl:if test="not(@lng) or $lang=@lng">
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
                    <xsl:when test="title"><xsl:apply-templates select="title[not(@lng) or @lng=$lang]/text()" /></xsl:when>
                    <xsl:otherwise>Carrot<sup>2</sup></xsl:otherwise>
                </xsl:choose>
            </title>
            
            <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        </head>

        <body class="page" leftmargin="0" topmargin="0">
            <xsl:call-template name="control-panel" />
            
            <table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%">
            <tr>
                <!-- left pane -->
                <td align="left" valign="top" width="180" style="max-width: 180px;">
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
</xsl:if>
</xsl:template>


<xsl:template match="page/title"></xsl:template>


<xsl:template match="chapter"><xsl:if test="not(@lng) or $lang=@lng">
    <xsl:apply-templates select="*[name()='title']" />
    <div class="chapter_nested_{count(ancestor::chapter)}">
        <xsl:apply-templates select="*[name()!='title']" />
    </div>
</xsl:if></xsl:template>


<xsl:template match="chapter/title"><xsl:if test="not(@lng) or $lang=@lng">
    <div class="chapter_nested_{count(ancestor::chapter)-1}_title">
        <xsl:apply-templates />
    </div>
</xsl:if></xsl:template>


<xsl:template match="illustration"><xsl:if test="not(@lng) or $lang=@lng">
    <xsl:choose>
        <xsl:when test="@float"><xsl:call-template name="pasteIllustration" /></xsl:when>
        <xsl:otherwise>
            <div style="text-align: center;"><xsl:call-template name="pasteIllustration" /></div>
        </xsl:otherwise>
    </xsl:choose>
</xsl:if></xsl:template>


<xsl:template name="pasteIllustration">
    <table class="figure" width="1%">
    <xsl:if test="@float">
        <xsl:attribute name="style">
            <xsl:choose>
                <xsl:when test="@float='right'">float: right; margin-right: 0em;</xsl:when>
                <xsl:otherwise>float: left; margin-left: 0em;</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:if>
    <tr><td class="pic" align="center" valign="top">
        <xsl:choose>
            <xsl:when test="@thumb">
                <a>
                    <xsl:attribute name="href">
                        <xsl:call-template name="rewriteImgURL">
                            <xsl:with-param name="href" select="@src" />
                        </xsl:call-template>
                    </xsl:attribute>
                    <img border="0">
                    <xsl:copy-of select="@alt"/>
                    <xsl:attribute name="src">
                        <xsl:call-template name="rewriteImgURL">
                            <xsl:with-param name="href" select="@thumb" />
                        </xsl:call-template>
                    </xsl:attribute>
                    </img>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <img>
                <xsl:copy-of select="@alt"/>
                <xsl:attribute name="src">
                    <xsl:call-template name="rewriteImgURL">
                        <xsl:with-param name="href" select="@src" />
                    </xsl:call-template>
                </xsl:attribute>
                </img>
            </xsl:otherwise>
        </xsl:choose>
        </td>
    </tr>
    <xsl:if test="description">
        <xsl:for-each select="description[not(@lng) or $lang=@lng]">
        <tr><td class="desc" align="left" valign="top"><table cellspacing="0" cellpadding="0" border="0"><tr><td width="100%"><xsl:apply-templates /></td></tr></table></td></tr>
        </xsl:for-each>
    </xsl:if>
    </table>
</xsl:template>

<!-- ################################### -->
<!-- {{{ I80N -->
<!-- ################################### -->

<xsl:template match="stub"><xsl:if test="not(@lng) or $lang=@lng">
<xsl:apply-templates />
</xsl:if></xsl:template>

<!-- }}} -->


</xsl:stylesheet>

