<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:sin="http://www.dawidweiss.com/projects/SIN"
>
<xsl:output method="html" indent="no" encoding="UTF-8" />

<!-- ######################################################################### -->
<!-- ## PARAMAMETERS AND VARIABLES WHICH MUST BE PASSED FROM THE SIN ENGINE ## -->
<!-- ######################################################################### -->

<xsl:param name="docsBaseURL"     select="''" /> <!-- ex: http://www.cs.put.poznan.pl/dweiss/site -->
<xsl:param name="sitemapURL"      select="''" /> <!-- ex: file:///home/dweiss/public_html/site/sitemap.xml -->

<xsl:param name="engineBaseURL"   select="''" /> <!-- ex: http://www.cs.put.poznan.pl/dweiss     -->
<xsl:param name="engineScriptURL" select="''" /> <!-- ex: http://www.cs.put.poznan.pl/dweiss/engine.php -->
<xsl:param name="pageURL"         select="''" /> <!-- ex: http://www.cs.put.poznan.pl/dweiss/engine.php/pubs/index.xml -->
<xsl:param name="queryString"     select="''" /> <!-- ex: picid=SENTINEL&wuwu=23    (it is the query string without
                                                                                     leading ? and with lang parameter stripped --> 
<xsl:param name="realPageURL" 	  select="''" /> <!-- ex: http://www.cs.put.poznan.pl/dweiss/site/pubs -->
<xsl:param name="localPageDir"    select="''" /> <!-- ex: /home/dweiss/site/pubs -->

<xsl:param name="lastUpdate"      select="''" /> <!-- Last modification date of the root XML file being rendered -->
<xsl:param name="currentDate"     select="''" /> <!-- Current date as YYYY-MM-DD -->
<xsl:param name="engineVersion"   select="''" /> <!-- Engine version -->

<xsl:param name="requestedLang"   select="''" /> <!-- requested language. An empty string or an acronym 'pl', 'en' or any other -->


<!-- ######################################################################### -->
<!-- ## GLOBAL VARIABLES                                                    ## -->
<!-- ######################################################################### -->

<xsl:variable name="cssBaseURL" select="concat($docsBaseURL,'/css')" />
<xsl:variable name="gfxBaseURL" select="concat($docsBaseURL,'/gfx')" />

<!-- set the sitemap's department id for control panel. Use default if no /page/@section exists -->
<xsl:variable name="sectionId">
	<xsl:choose>
		<xsl:when test="/page/@section"><xsl:value-of select="/page/@section"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<!-- set the language variable -->
<xsl:variable name="lang">
	<xsl:choose>
		<!-- If there is a lang parameter and any of the languages specified on page matches it, use it -->
		<xsl:when test="$requestedLang"><xsl:value-of select="$requestedLang"/></xsl:when>
		<!-- If there is no lang parameter and there is a default language of the page, use it -->
		<xsl:when test="not($requestedLang) and /*/languages/default"><xsl:value-of select="/*/languages/default/@id"/></xsl:when>
		<!-- default language is the first from the list, if not specified otherwise -->
		<xsl:otherwise><xsl:value-of select="/*/languages/lng/@id"/></xsl:otherwise>
	</xsl:choose>
</xsl:variable>


<!-- ################################################################### -->
<!-- URL-MANIPULATION AND XML FILE INCLUSION                             -->
<!-- ###################################################################

	func 	rewriteImgURL(href)
	func 	rewriteURL(href)
	match 	include(@file)
	func	addLangParam(href, substlang=$requestedLang)

     ################################################################### -->


<!-- rewrites image's url -->
<xsl:template name="rewriteImgURL">
	<xsl:param name="href" />
	
	<xsl:choose>
		<xsl:when test="starts-with($href,'http://')">
			<!-- absolute url -->
			<xsl:value-of  select="$href" />
		</xsl:when>
		<xsl:when test="starts-with($href,'/gfx/')">
			<!-- absolute from the graphics root. -->
			<xsl:value-of  select="concat($gfxBaseURL,'/', substring($href,6))" />
		</xsl:when>
		<xsl:when test="starts-with($href,'/')">
			<!-- absolute from site root -->
			<xsl:value-of  select="concat($docsBaseURL,'/',$href)" />
		</xsl:when>
		<xsl:otherwise>
			<!-- relative. rewrite. Cut out the Engine part. -->
			<xsl:value-of  select="concat($realPageURL,'/',$href)" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>



<!-- Rewrites an URL. Outside-url's are not touched, some config
     parameters are added to local engine url's -->
<xsl:template name="rewriteURL">
	<xsl:param name="href" />
	
	<!-- rewrite only those URL's that point to .xml documents -->
	<xsl:choose>
		<xsl:when test="starts-with($href,'#')">
			<xsl:value-of  select="$href" />
		</xsl:when>
		<xsl:when test="contains($href,'.xml?') or (contains($href,'.xml') and not(contains($href,'?')))">
			<xsl:choose>
				<xsl:when test="starts-with($href,'http://') or starts-with($href,'ftp://') or starts-with($href,'mailto:')">
					<!-- absolute url -->
					<xsl:value-of  select="$href" />
				</xsl:when>
				<xsl:when test="starts-with($href,'/')">
					<!-- absolute from the engine root. add engine's base url and rewrite. -->
					<xsl:call-template name="addLangParam">
						<xsl:with-param name="href" select="concat($engineScriptURL,$href)" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- relative. rewrite. -->
					<xsl:call-template name="addLangParam">
						<xsl:with-param name="href" select="$href" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<!-- strip the engine path from relative references -->
			<xsl:choose>
				<xsl:when test="starts-with($href,'http://') or starts-with($href,'ftp://') or starts-with($href,'mailto:')">
					<!-- absolute url -->
					<xsl:value-of  select="$href" />
				</xsl:when>
				<xsl:when test="starts-with($href,'/')">
					<!-- absolute from the engine root. add engine's base url and rewrite. -->
					<xsl:value-of  select="concat($docsBaseURL,$href)" />
				</xsl:when>
				<xsl:otherwise>
					<!-- relative. rewrite. -->
					<xsl:value-of  select="concat($realPageURL,'/', $href)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>



<!-- Adds language parameter to a URL (unless there already is a language param) -->
<xsl:template name="addLangParam">
	<xsl:param name="href" />
	<xsl:param name="substlang" select="$lang" />

	<xsl:choose>
		<xsl:when test="contains($href,'?')">
			<xsl:choose>
				<xsl:when test="contains(substring-after($href,'?'),'lang=')">
					<!-- copy the href verbatim -->
					<xsl:value-of select="$href" />
				</xsl:when>
				<xsl:otherwise>
					<!-- append lang param -->
					<xsl:value-of select="concat(substring-before($href,'?'),'?lang=',$substlang,'&amp;',substring-after($href,'?'))" /> 
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<!-- append ? and lang param -->
            <xsl:choose>
                <xsl:when test="contains($href,'#')">
                    <xsl:value-of select="concat(substring-before($href,'#'),'?lang=',$substlang,'#',substring-after($href,'#'))" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($href,'?lang=',$substlang)" />
                </xsl:otherwise>
            </xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>




<!-- ################################################################### -->
<!-- HTML-FORM HANDLING                                                  -->
<!-- ###################################################################

	match	form|textarea|input|select|option(@*), @action is URL-rewritten.

     ################################################################### -->

<xsl:template match="form|textarea|input|select|option">
	<xsl:copy>
		<xsl:copy-of select="@*[name()!='action']" />
		<xsl:attribute name="action">
			<xsl:call-template name="rewriteURL">
				<xsl:with-param name="href" select="@action" />
			</xsl:call-template>
		</xsl:attribute>
		<xsl:apply-templates />
	</xsl:copy>
</xsl:template>


<!-- ################################################################### -->
<!-- engine_pages sin: namespace markup handling                         -->
<!-- ###################################################################

	match	sin:page

     ################################################################### -->
	 
<xsl:template match="sin:page">
	<html>
	<body>
		<h2>SIN engine service page:</h2>
		<h1><xsl:apply-templates select="sin:cause" /></h1>
		<hr />
			<div style="margin: 20px;">
				<b>
				<font size="+1" color="black">
				<xsl:apply-templates select="*[ name() != 'sin:cause' ]"/>
				</font>
				</b>
			</div>
		<hr />
		<div style="width: 60%;">
			<font size="-1">
			If you see this page it is likely an error. Please report
			how did you access this page (i.e. by clicking on which link), possibly providing
			URL addresses and browser type. Thank you.
			<br/>
			<br/>
			<b>Use <tt><font size="+1">BACK</font></tt> history  button in your browser to return to the page that likely caused this error.</b>
			</font>
		</div>
	</body>
	</html>
</xsl:template>

<xsl:template match="sin:cause">
	<font color="red"><xsl:apply-templates /></font>
</xsl:template>

</xsl:stylesheet>
