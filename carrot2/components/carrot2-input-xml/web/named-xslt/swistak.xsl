<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:param name="swistak.query" />
    <xsl:param name="swistak.results" select="'200'" />

    <!-- Process all feeds in the XML file -->
	<xsl:template match="/feed">
        <xsl:variable name="feedurl" select="concat('http://www.swistak.pl/szukaj_rss.html?limit=', $swistak.results, '&amp;html=1&amp;query=', $swistak.query)" />
        <xsl:apply-templates select="document($feedurl)" />
	</xsl:template>

	<xsl:template match="rss">
		<searchresult>
            <query>
                <xsl:attribute name="requested-results">100</xsl:attribute>
                <xsl:value-of select="channel/description" />
            </query>

            <xsl:for-each select="channel/item">
                <document id="{concat('id_', string(position()))}">
                    <xsl:if test="title">
                        <title><xsl:value-of select="title" /></title>
                    </xsl:if>

                    <url>
                        <xsl:choose>
                            <xsl:when test="url">
                                <xsl:value-of select="url" />
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- Make up an URL. -->
                                <xsl:value-of select="concat('http://nodomain.org/result-', position())" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </url>

                    <!-- 
                    Swistak doesn't return anything that could be used as a snippet...
                    <snippet>
                    </snippet>
                    -->
                </document><xsl:value-of select="'&#10;'" />
            </xsl:for-each>
        </searchresult>
	</xsl:template>
   
</xsl:stylesheet>
