<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:autn="http://schemas.autonomy.com/aci/"
xmlns:atom="http://purl.org/atom/ns#"
>
    <xsl:output method="xml" indent="yes" media-type="application/rss+xml"/>
    <xsl:include href="RetrievalAppConfig.tmpl" />
    <xsl:template match="/">
		<rss xmlns:os="http://a9.com/-/spec/opensearch/1.1/" version="2.0">
			<channel>
			   <title>IDOL search: OpenSearch RSS</title>
			   <description>IDOL search: OpenSearch RSS</description>
			   <link><xsl:value-of select="$TransportProtocol" /><xsl:value-of select="$idolHost" />:<xsl:value-of select="$idolPort" />/action=query&amp;text=Food&amp;sourceid=mozilla-search&amp;xmlmeta=true&amp;summary=quick&amp;template=OpenSearchRSS2.0</link>
			   <os:totalResults><xsl:value-of select="autnresponse/responsedata/autn:totalhits" /></os:totalResults>
			   <os:startIndex>1</os:startIndex>
			   <os:itemsPerPage><xsl:value-of select="autnresponse/responsedata/autn:numhits" /></os:itemsPerPage>
			   <xsl:for-each select="autnresponse/responsedata/autn:hit">
					<item>
						<title><xsl:value-of select="autn:title"/></title>
						<link><xsl:value-of select="autn:reference"/></link>
						<guid isPermaLink="true"><xsl:value-of select="autn:reference"/></guid>
						<pubDate><xsl:value-of select="autn:datestring"/></pubDate>
						<description><xsl:value-of select="autn:summary"/></description>
					</item>
			   </xsl:for-each> 
			</channel>
		</rss>
    </xsl:template>
</xsl:stylesheet>