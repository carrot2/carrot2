<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- 
  This stylesheet verifies whether there is a matching language for the current page.
  If not, a message in English is printed.
  
  Also, a 'lang' variable is instantiated.

  REQUIRED INCLUDE: GENERIC_BASE.XSL
  -->
  
  
<!-- ################################################################### -->
<!-- URL-MANIPULATION AND XML FILE INCLUSION                             -->
<!-- ###################################################################

	func 	sin-lang-check()  [must define /*/languages element]
	match	/*/languages

     ################################################################### -->

<!-- check whether the current page can be displayed in requested language -->
<xsl:template name="sin-lang-check">
	<xsl:choose>
		<xsl:when test="/*/languages/lng[@id = $lang] or not(/*/languages)">
			<xsl:apply-templates />
		</xsl:when>
		<xsl:otherwise>
			<html>
			<body>
				<h2>SIN engine service page:</h2>
				<h1><font color="red">Language not detected</font></h1>
				<hr />
					<div style="margin: 20px;">
						<b>
						<font size="+1" color="black">
							<p>There is no language '<xsl:value-of select="$lang"/>' defined for this page.</p>
							<p>Available languages are:</p>
							<ul>
								<xsl:for-each select="/*/languages/lng">
									<li>Language:
										<a>
											<xsl:attribute name="href">
												<xsl:call-template name="addLangParam">
													<xsl:with-param name="href" select="$pageURL" />
													<xsl:with-param name="substlang" select="@id" />
												</xsl:call-template>
											</xsl:attribute>
											<xsl:value-of select="text()"/>
										</a>
										<xsl:if test="@id = /*/languages/default/@id"> (suggested default)</xsl:if>
									</li>
								</xsl:for-each>
							</ul>
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
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template match="/*/languages"></xsl:template>


</xsl:stylesheet>

