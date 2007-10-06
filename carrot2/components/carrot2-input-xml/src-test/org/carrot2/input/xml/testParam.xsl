<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:param name="query" select="'novalue'" />
    <xsl:param name="custom.param" select="'novalue'" />

	<!-- Copy the input verbatim -->
	<xsl:template match="/">
        <xsl:choose>
            <xsl:when test="$query = 'novalue'">
                <xsl:message terminate="yes">No required argument 'query'.</xsl:message>
            </xsl:when>
            <xsl:when test="$custom.param = 'novalue'">
                <xsl:message terminate="yes">No required argument 'custom.param'.</xsl:message>
            </xsl:when>
            <xsl:otherwise>
                <searchresult>
                </searchresult>
            </xsl:otherwise>
        </xsl:choose>
	</xsl:template>
   
</xsl:stylesheet>
