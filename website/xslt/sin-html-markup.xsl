<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- REQUIRED INCLUDE: GENERIC_BASE.XSL -->


<!-- ################################################################### -->
<!-- ANCHORS                                                             -->
<!-- ###################################################################

	match	a(@style,@class,@href,@name)    @href rewritten
	match	img(@class,@alt,@src)		@src rewritten

     ################################################################### -->


<xsl:template match="a">
	<a><xsl:if test="@style|@class">
			<xsl:copy-of select="@style|@class" />
		</xsl:if>
		<xsl:if test="@href">
			<xsl:attribute name="href">
				<xsl:call-template name="rewriteURL">
					<xsl:with-param name="href" select="@href" />
				</xsl:call-template>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@name"><xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute></xsl:if>
		<xsl:apply-templates /></a>
</xsl:template>


<xsl:template match="img">
	<xsl:copy>
		<xsl:copy-of select="@class"/>
		<xsl:copy-of select="@alt"/>
        <xsl:copy-of select="@width"/>
        <xsl:copy-of select="@height"/>
		<xsl:attribute name="src">
			<xsl:call-template name="rewriteImgURL">
				<xsl:with-param name="href" select="@src" />
			</xsl:call-template>
		</xsl:attribute>
	</xsl:copy>
</xsl:template>


<!-- ################################################################### -->
<!-- LAYOUT ELEMENTS                                                     -->
<!-- ###################################################################

	match	span|strong|i|br|ol|li|ul|br|sub|sup|b|wbr
	match	p(@class,@style)

     ################################################################### -->

<xsl:template match="strong|i|br|ol|li|ul|br|sub|sup|b|wbr|tt|dd|dl|dt|table|tr|td|div">
	<xsl:copy><xsl:copy-of select="@*"/><xsl:apply-templates /></xsl:copy>
</xsl:template>


<xsl:template match="p|span">
 	<xsl:copy>
		<xsl:if test="@style|@class">
				<xsl:copy-of select="@style|@class" />
		</xsl:if>
		<xsl:apply-templates />
	</xsl:copy>
</xsl:template>


<!-- ################################################################### -->
<!-- VERBATIM MARKUP COPY                                                -->
<!-- ###################################################################

	match	quote-html

     ################################################################### -->


<xsl:template match="quote-html"><xsl:apply-templates mode="quoting" /></xsl:template>

<xsl:template match="*|text()|processing-instruction()" mode="quoting">
    <xsl:choose>
        <xsl:when test="name() = 'quote-stop'"><xsl:apply-templates /></xsl:when>
        <xsl:otherwise><xsl:copy><xsl:copy-of select="@*" /><xsl:apply-templates mode="quoting" /></xsl:copy></xsl:otherwise>
    </xsl:choose>
</xsl:template>


<!-- ################################################################### -->
<!-- Default rule copies text nodes to the output, ignores processing    -->
<!-- instructions and highlights unmatched tags in red.                  -->
<!-- ###################################################################

	match	text()
	match	processing-instruction()

     ################################################################### -->


<xsl:template match="text()">
	<xsl:value-of select="." />
</xsl:template>


<!-- ignore processing instructions -->
<xsl:template match="processing-instruction()" priority="2">
	<!-- ignore -->
</xsl:template>


<!-- Don't allow unmatched tags to propagate. -->
<xsl:template match="*">
	<span style="color:red;">[Unrecognized input node named: 
		 &lt;<xsl:value-of select="concat(name(),' ')"/>
		 <xsl:for-each select="@*">
			<xsl:value-of select="concat(name(.),'=&quot;',.,'&quot; ')"/>
		 </xsl:for-each><xsl:value-of select="'>'"/>]
	</span>
</xsl:template>


</xsl:stylesheet>
