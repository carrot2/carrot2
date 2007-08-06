<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <!-- 
<searchresult>
    <query requested-results="100">poland</query>

    <document id="NC0">
        <title>Attractions in Poland</title>
        <url>http://www.discoverpoland.com/attractions_in_poland.htm</url>
        <snippet>Since the political upheavals of 1989, a large number of small,
        privately owned travel companies have emerged that run coach excursions
        or organize tours. However, the national, originally state-run companies
        still provide a good service.</snippet>
    </document>
    
    -->
   
	<!-- Copy the input verbatim -->
	<xsl:template match="/">
		<searchresult>
            <query>
                <xsl:attribute name="requested-results">
                    <xsl:value-of select="string(1 + number(response/end) - number(response/start))" />
                </xsl:attribute>
                <xsl:value-of select="response/query" />
            </query>

            <xsl:for-each select="response/results/result">
                <!-- only results with the required fields -->
                <xsl:if test="url">
                    <document id="{concat('id_', string(position()))}">
                        <xsl:if test="jobtitle or company">
                            <title>
                                <xsl:value-of select="jobtitle" />
                                <xsl:if test="company">
                                    <xsl:value-of select="concat(' (', company, ')')" />
                                </xsl:if>
                            </title>
                        </xsl:if>
                        <xsl:copy-of select="url" />
                        <xsl:if test="snippet">
                            <xsl:copy-of select="snippet" />
                        </xsl:if>
                    </document><xsl:value-of select="'&#10;'" />
                </xsl:if>
            </xsl:for-each>
        </searchresult>
	</xsl:template>
   
</xsl:stylesheet>
