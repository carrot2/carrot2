<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:template match="section/name">
<xsl:apply-templates />
</xsl:template>

<xsl:template name="control-panel">
		<div id="control-panel" style="position: absolute; left: 0px; top: 0px; margin-top: 0px; padding-left: 0px; padding-right: 0px;">
			<table cellspacing="0" cellpadding="0"><tr>
			<!-- apply templates to the department string -->
  			<td style="padding-bottom: 1px; background-color: black; color: white;">
  				<!-- apply any tags inside the tag -->
				<xsl:call-template  name="sitemap-path-iter">
					<xsl:with-param name="nde" select="document($sitemapURL)/sitemap" />
				</xsl:call-template>
  			</td>

			<!-- language indicators for this page -->
  			<td style="padding-bottom: 1px; background-color: black; color: white; padding-left: 10px;">
  				<nobr>
				<xsl:variable name="fullURL">
					<xsl:value-of select="concat($pageURL,'?')" />
					<xsl:if test="$queryString!=''">
						<xsl:value-of select="concat($queryString,'&amp;')" />
					</xsl:if>
				</xsl:variable>
				<xsl:for-each select="/page/languages/lng">
					[<a class="dept" href="{concat($fullURL,'lang=',@id)}"><xsl:value-of select="text()"/></a>]
				</xsl:for-each>
				</nobr>
  			</td>

  			<td valign="top">
				<img>
					<xsl:attribute name="src">
						<xsl:call-template name="rewriteImgURL">
					 		<xsl:with-param name="href" select="'/gfx/control-panel/departmentAngle.gif'" />
					 	</xsl:call-template>
					</xsl:attribute>
				</img>
			</td>
			</tr></table>
		</div>
</xsl:template>

<xsl:template name="sitemap-hierarchical">
	<xsl:param name="level" />
    <xsl:param name="topSection" />

	<xsl:for-each select="document($sitemapURL)/sitemap//section[@id=$topSection]">
		<xsl:call-template name="show_depts">
			<xsl:with-param name="depth" 		select="$level" />
			<xsl:with-param name="show_desc" 	select="'no'" />
			<xsl:with-param name="root" 		select="./section" />
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>


<xsl:template name="show_depts">
	<xsl:param name="root" 					  />
	<xsl:param name="show_desc" select="'no'" />
	<xsl:param name="depth"                   />

	<!-- Display the leafs -->
	<xsl:for-each select="$root">	
		<xsl:call-template name="show_dept">
			<xsl:with-param name="depth" 		select="$depth" />
			<xsl:with-param name="currentdepth" select="'1'" />
			<xsl:with-param name="show_desc" 	select="$show_desc" />
			<xsl:with-param name="root" 		select="." />
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>


<xsl:template name="show_dept">
	<xsl:param name="root" 					  />
	<xsl:param name="show_desc" select="'no'" />
	<xsl:param name="depth"                   />
	<xsl:param name="currentdepth" select="'1'" />
	
	<div class="{concat('menu_level_', $currentdepth)}">
	<xsl:call-template name="emitDeptName">
		<xsl:with-param name="dpt" select="$root" />
	</xsl:call-template>
	</div>

	<!-- if subnodes exist, and the max level hasn't been reached, display them -->
	<xsl:if test="$root/section and $depth!='0'">
			<xsl:for-each select="$root/section">
				<xsl:call-template name="show_dept">
					<xsl:with-param name="depth" 		select="$depth - 1" />
					<xsl:with-param name="show_desc" 	select="$show_desc" />
					<xsl:with-param name="root" 		select="." />
					<xsl:with-param name="currentdepth" select="$currentdepth + 1" />
				</xsl:call-template>
			</xsl:for-each>
	</xsl:if>

</xsl:template>


<xsl:template match="location|desc">
	<xsl:apply-templates />
</xsl:template>


<xsl:template name="emitDeptName">
	<xsl:param name="class" 	select="'menu'" />
	<xsl:param name="dpt" 					/>

	<xsl:variable name="linkLocation">
		<xsl:choose>
			<xsl:when test="$dpt/@location">
				<xsl:call-template name="rewriteURL">
					<xsl:with-param name="href" select="$dpt/@location" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$dpt/location">
				<xsl:variable name="complexhref">
					<xsl:apply-templates select="$dpt/location" />
				</xsl:variable>
				<xsl:call-template name="rewriteURL">
					<xsl:with-param name="href" select="normalize-space($complexhref)" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:choose>
		<xsl:when test="normalize-space($linkLocation)=''">
			<xsl:apply-templates select="$dpt/name" />
		</xsl:when>
		<xsl:otherwise>
		<a class="{$class}" href="{$linkLocation}"><xsl:apply-templates select="$dpt/name" /></a>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template match="sitemap-path">
	<xsl:call-template  name="sitemap-path-iter">
		<xsl:with-param name="nde" select="document($sitemapURL)/sitemap" />
	</xsl:call-template>
</xsl:template>


<xsl:template name="sitemap-path-iter">
	<xsl:param name="nde" />
	
	<xsl:variable name="subpaths" select="$nde/section[descendant-or-self::section[@id=$sectionId]]" />
	<xsl:variable name="imgArrow"><xsl:call-template name="rewriteImgURL"><xsl:with-param name="href" select="'/gfx/control-panel/arrow.gif'" /></xsl:call-template></xsl:variable>

	<xsl:for-each select="$subpaths">
		<xsl:if test="./parent::sitemap">
			<img width="9" height="8">
			<xsl:attribute name="src">
				<xsl:call-template name="rewriteImgURL">
					<xsl:with-param name="href" select="'/gfx/control-panel/rootmarker.gif'" />
				</xsl:call-template></xsl:attribute>
			</img>
		</xsl:if><xsl:if test="count( $subpaths ) &gt; 1" > [</xsl:if><img width="10" height="12" align="top" src="{$imgArrow}" /><xsl:call-template name="emitDeptName">
			<xsl:with-param name="dpt" select="." />
			<xsl:with-param name="class" select="'dept'" />
		</xsl:call-template>

		<xsl:call-template name="sitemap-path-iter">
			<xsl:with-param name="nde" select="." />
		</xsl:call-template>
		<xsl:if test="count( $subpaths ) &gt; 1" >]</xsl:if>
	</xsl:for-each>
</xsl:template>


</xsl:stylesheet>

