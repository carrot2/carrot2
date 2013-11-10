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
  <xsl:param name="solr.id-field"></xsl:param>
  <xsl:param name="solr.use-highlighter-output">true</xsl:param>
  <xsl:param name="solr.copy-fields">false</xsl:param>

  <xsl:key name="highlighter"  match="/response/lst[@name='highlighting']/lst" use="@name" />

  <xsl:template match="/">
    <searchresult>
      <!-- Query hint -->
      <query><xsl:value-of select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" /></query>

      <!-- Emit documents. -->
      <xsl:for-each select="/response/result/doc">
        <xsl:variable name="docid" select="*[@name = $solr.id-field]" />
        <document>
          <xsl:if test="$solr.id-field != ''">
            <xsl:attribute name="id">
                <xsl:value-of select="$docid" />
            </xsl:attribute>
          </xsl:if>

          <url><xsl:value-of select="*[@name=$solr.url-field]" /></url>
          <title><xsl:value-of select="*[@name=$solr.title-field]" /></title>
          <snippet>
            <xsl:variable name="fragment" select="key('highlighter', $docid)/arr[@name=$solr.summary-field]" />
            <xsl:choose>
              <xsl:when test="$solr.use-highlighter-output = 'true' and $fragment">
                <xsl:for-each select="$fragment/str">
                  ... <xsl:value-of select="." />
                </xsl:for-each>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="*[@name=$solr.summary-field]" />
              </xsl:otherwise>
            </xsl:choose>
          </snippet>
          <xsl:if test="$solr.copy-fields = 'true'">
            <xsl:apply-templates mode="copy-fields" />
          </xsl:if>
        </document>
      </xsl:for-each>

      <!-- Extract Solr-generated clusters if any. -->
      <xsl:if test="$solr.id-field != '' and /response/arr[@name='clusters']">
        <xsl:comment>Clusters from Solr</xsl:comment>

        <xsl:for-each select="/response/arr[@name = 'clusters']/lst">
          <xsl:call-template name="cluster-adapter" />
        </xsl:for-each>
      </xsl:if>

      <!-- Extra attributes. -->
      <xsl:if test="/response/result/@numFound">
        <attribute key="results-total">
          <value type="java.lang.Long" value="{/response/result/@numFound}" />
        </attribute>
      </xsl:if>
    </searchresult>
  </xsl:template>
  
  <xsl:template match="arr/str">
    <xsl:apply-templates />
    <xsl:if test="not(position() = last())">
      <xsl:value-of select="' . '" />
    </xsl:if>
  </xsl:template>

  <!-- Solr to Carrot2 cluster adapter. -->  
  <xsl:template name="cluster-adapter">
    <group>
      <xsl:if test="double[@name = 'score']">
        <xsl:attribute name="score"><xsl:value-of select="double[@name = 'score']"/></xsl:attribute>
      </xsl:if>

      <title>
        <xsl:for-each select="arr[@name = 'labels']/str">
          <phrase><xsl:value-of select="." /></phrase>
        </xsl:for-each>
      </title>
      
      <xsl:if test="bool[@name = 'other-topics'] = 'true'">
        <attribute key="other-topics"><value value="true"/></attribute>
      </xsl:if>

      <xsl:for-each select="arr[@name = 'docs']/str">
        <document refid="{.}" />
      </xsl:for-each>

      <!-- sub-clusters? -->
      <xsl:for-each select="arr[@name = 'clusters']/lst">
        <xsl:call-template name="cluster-adapter" />
      </xsl:for-each>
    </group>
  </xsl:template>

  <xsl:template match="arr" mode="copy-fields">
    <field key="{@name}">
      <value>
        <wrapper class="org.carrot2.util.simplexml.ListSimpleXmlWrapper">
          <list>
            <xsl:for-each select="*">
              <value value="{.}"/>
            </xsl:for-each>
          </list>
        </wrapper>
      </value>
    </field>
  </xsl:template>
  
  <xsl:template match="bool|str|int|long|double" mode="copy-fields">
    <field key="{@name}">
      <value value="{.}"/>
    </field>
  </xsl:template>
</xsl:stylesheet>
