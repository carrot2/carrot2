<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:template match="download">
	<table class="download">
		<xsl:attribute name="style">
			<xsl:choose>
				<xsl:when test="@float='right'">float: right; margin-right: 0em;</xsl:when>
				<xsl:otherwise>float: left; margin-left: 0em;</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	<tr>
	    <td class="pic" align="center" valign="middle" rowspan="2">
	    	<img>
				<xsl:attribute name="src">
					<xsl:call-template name="rewriteImgURL">
						<xsl:with-param name="href" select="concat('/gfx/icons/',substring(@src,string-length(@src)-2),'.gif')" />
					</xsl:call-template>
				</xsl:attribute>
			</img>
		</td>
		<td class="link" align="left" valign="top">
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="rewriteURL">
						<xsl:with-param name="href" select="@src" />
					</xsl:call-template>
				</xsl:attribute>

				<xsl:apply-templates />
			</a>
		</td>
	</tr>
	<tr>
		<td class="desc" align="left" valign="top">
		Download file
		</td>
	</tr>
	</table>
</xsl:template>


<xsl:template match="carrot-text">Carrot<sup>2</sup></xsl:template>

<!-- applies templates to some included XML file. -->
<xsl:template match="include">
	<xsl:apply-templates select="document(concat($localPageDir, '/', @file))/*" />
</xsl:template>


<xsl:template match="carrot">Carrot<sup>2</sup>
<!--
    <img border="0" align="bottom">
    <xsl:attribute name="src">
    <xsl:call-template name="rewriteImgURL">
        <xsl:with-param name="href" select="'/gfx/carrot2/carrot-supersmall.gif'" />
    </xsl:call-template>
    </xsl:attribute>
    </img>
-->
</xsl:template>


<xsl:template match="carrot-black">Carrot<small><sup>2</sup></small>
<!--
    <img border="0" align="bottom">
    <xsl:attribute name="src">
    <xsl:call-template name="rewriteImgURL">
        <xsl:with-param name="href" select="'/gfx/carrot2/carrot-supersmall-black.gif'" />
    </xsl:call-template>
    </xsl:attribute>
    </img>
-->
</xsl:template>

<xsl:template match="frame">
<div class="frame"><xsl:apply-templates /></div>
</xsl:template>

<xsl:variable name="showNews">
	<xsl:call-template name="rewriteURL">
		<xsl:with-param name="href" select="'/news/index.xml'" />
	</xsl:call-template>
</xsl:variable>

<xsl:template match="newslist">
	<xsl:variable name="curts"><xsl:value-of select="number(substring($currentDate,1,4))*12*30+number(substring($currentDate,6,2))*30+number(substring($currentDate,9,2))"/></xsl:variable>
	<xsl:variable name="nlist" select="news[(number($curts)-90) &lt; (number(substring(@date,1,4))*12*30+number(substring(@date,6,2))*30+number(substring(@date,9,2)))]" />

	<xsl:choose>
		<xsl:when test="count($nlist) &gt; 0">
			<xsl:apply-templates select="$nlist" />
		</xsl:when>
		<xsl:otherwise>
		No news.
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="news">
		<p>
		<span style="font-size: 80%; font-weight: bold;">(<xsl:value-of select="@date"/>)</span>
		<br/>
			<xsl:apply-templates select="head" />
		</p>
</xsl:template>

<xsl:template match="head">
	<xsl:apply-templates />
</xsl:template>

</xsl:stylesheet>

