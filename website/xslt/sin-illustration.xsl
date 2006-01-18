<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!--
  ## This template matches 'illustraction' element and puts a bordered
  ## illustration with an optional caption.
  ##
  ## example markup:
  ## <illustration float="right/inline/left" thumb="" width="" height="" src="" alt="">
  ## <description>Description</description>
  ## </illustration>
  ##
  ## if 'thumb' is present, width and height refer to thumb's size.
  ## thumb, width, height, alt and float attrs. are optional.
  -->
  
  <!--
  CSS must define these:

TABLE.figure {
	border:     0px;
	padding:    0px;
	margin:     3px;
}

TABLE.figure TD.pic {
	border-color:	black;
	border-width:	1px;
	border-style:	solid;
	padding: 		4px;
	background-color: #FFFFFF;
}

TABLE.figure TD.desc {
	border-color:	black;
	border-width:	1px;
	border-style:	solid;
	background-color: #FFFFCC;
	padding:        1px;
}
  
  -->

<xsl:template match="illustration">
<xsl:call-template name="pasteIllustration" />
</xsl:template>

<xsl:template name="pasteIllustration">
	<table class="figure" width="1px" height="1px">
    <xsl:choose>
	<xsl:when test="@float">
		<xsl:attribute name="style">
			<xsl:choose>
				<xsl:when test="@float='right'">float: right; margin-right: 0em; clear: both;</xsl:when>
				<xsl:when test="@float='inline'">display: inline;</xsl:when>
				<xsl:otherwise>float: left; margin-left: 0em; clear: both;</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	</xsl:when>
    </xsl:choose>

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
    				<xsl:if test="@width"><xsl:copy-of select="@width"/></xsl:if>
    				<xsl:if test="@height"><xsl:copy-of select="@height"/></xsl:if>
					<xsl:copy-of select="@alt"/>
					<xsl:attribute name="src">
						<xsl:call-template name="rewriteImgURL">
							<xsl:with-param name="href" select="@thumb" />
						</xsl:call-template>
					</xsl:attribute>
					</img>
				</a>
			</xsl:when>
            <xsl:when test="not(@src) or @src=''">
                <div style="background-color: #e0e0e0;">
                    Brak&#x00A0;zdjęcia&#x00A0;(missing&#x00A0;picture):<br/><b>
                    <xsl:value-of select="@alt" /></b>
                </div>
            </xsl:when>
			<xsl:otherwise>
				<img>
				<xsl:if test="@width"><xsl:copy-of select="@width"/></xsl:if>
				<xsl:if test="@height"><xsl:copy-of select="@height"/></xsl:if>
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
		<xsl:for-each select="description">
		<tr><td class="desc" align="left" valign="top"><table cellspacing="0" cellpadding="0" border="0"><tr><td width="100%"><xsl:apply-templates /></td></tr></table></td></tr>
		</xsl:for-each>
	</xsl:if>
	</table>
</xsl:template>


</xsl:stylesheet>

