<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   
	<!-- Copy the input verbatim -->
	<xsl:template match="/">
		<searchresult>
            <xsl:for-each select="rss/channel/item">
                <!-- only results with the required fields -->
                <xsl:if test="link">
                    <document id="{generate-id(.)}">
                        <xsl:if test="title">
							<xsl:copy-of select="title" />
                        </xsl:if>
						<url><xsl:value-of select="link" /></url>
                        <xsl:if test="description">
							<snippet><xsl:value-of select="description" /></snippet>
                        </xsl:if>
                    </document><xsl:value-of select="'&#10;'" />
                </xsl:if>
            </xsl:for-each>
        </searchresult>
	</xsl:template>
   
</xsl:stylesheet>
