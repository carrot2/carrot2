<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output indent="yes" omit-xml-declaration="no"
       media-type="application/xml" encoding="UTF-8" />

  <!-- 
       Parameters passed by Carrot2 and corresponding to Solr -> Carrot2 field mappings.
   -->
  <xsl:param name="solr.title-field">title</xsl:param>
  <xsl:param name="solr.summary-field">description</xsl:param>
  <xsl:param name="solr.url-field">url</xsl:param>

  <!-- 
    Use clusters if present in Solr's response. The key field is required if documents are to be properly 
    linked from their clusters.
    -->
  <xsl:param name="solr.use-clusters">false</xsl:param>
  <xsl:param name="solr.id-field"></xsl:param>

  <xsl:key name="docs" match="/response/result/doc" use="*[@name='ID']"/>

  <xsl:template match="/">
    <searchresult>
      <query><xsl:value-of select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" /></query>

      <!-- Emit documents. -->
      <xsl:for-each select="/response/result/doc">
        <document>
          <xsl:if test="$solr.use-clusters = 'true'">
            <xsl:attribute name="id">
              <xsl:value-of select="count(preceding-sibling::doc)" />
            </xsl:attribute>
          </xsl:if>

          <url><xsl:value-of select="*[@name=$solr.url-field]" /></url>
          <title><xsl:value-of select="*[@name=$solr.title-field]" /></title>
          <snippet><xsl:value-of select="*[@name=$solr.summary-field]" /></snippet>
        </document>
      </xsl:for-each>

      <!-- Emit clusters, if requested. -->
      <xsl:if test="$solr.use-clusters = 'true'">
        <xsl:comment>Clusters from Solr</xsl:comment>

        <xsl:for-each select="/response/arr[@name = 'clusters']/lst">
          <group>
            <title>
              <xsl:for-each select="arr[@name = 'labels']/str">
                <phrase><xsl:value-of select="." /></phrase>
              </xsl:for-each>
            </title>

            <xsl:if test="bool[@name = 'other-topics'] = 'true'">
              <attribute key="other-topics"><value value="true"/></attribute>
            </xsl:if>

            <xsl:for-each select="arr[@name = 'docs']/str">
              <document refid="{count(key('docs',.)/preceding-sibling::doc)}" />
            </xsl:for-each>
          </group>
        </xsl:for-each>
      </xsl:if>

      <!-- extra attributes -->
      <xsl:if test="/response/result/@numFound">
        <attribute key="results-total">
          <value type="java.lang.Long" value="{/response/result/@numFound}" />
        </attribute>
      </xsl:if>
    </searchresult>
  </xsl:template>
</xsl:stylesheet>
