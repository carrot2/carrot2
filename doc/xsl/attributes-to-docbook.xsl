<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:db="http://docbook.org/ns/docbook"
                exclude-result-prefixes="db">

  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="no"
       encoding="utf-8" cdata-section-elements="programlisting" />
       
  <xsl:param name="metadata" select="document('components-metadata.xml')" />
  
  <xsl:template match="/">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="db:attribute-reference">
    <xsl:apply-templates select="$metadata/processing-component-docs/sources/processing-component-doc" />
  </xsl:template>

  <xsl:template match="processing-component-doc">
    <section xml:id="section.{component-descriptor/@id}">
      <title><xsl:apply-templates select="component-descriptor/title" /></title>

      <xsl:variable name="this" select="." />
      
      <xsl:for-each select="groups/string">
        <xsl:variable name="group" select="string(.)" />
        
        <section>
          <title><xsl:value-of select="$group" /></title>
          <xsl:apply-templates select="$this/attribute[attribute-descriptor/@input = 'true' and attribute-descriptor/metadata/group = $group]" />
        </section>
      </xsl:for-each>
      
      <section>
        <title>Ungrouped</title>
        <xsl:apply-templates select="$this/attribute[attribute-descriptor/@input = 'true' and not(attribute-descriptor/metadata/group)]" />
      </section>
    </section>
  </xsl:template>

  <xsl:template match="attribute">
    <xsl:apply-templates select="attribute-descriptor" />
  </xsl:template>

  <xsl:template match="attribute-descriptor">
    <section>
      <title><xsl:value-of select="metadata/title" /></title>
      <informaltable frame="all">
        <tgroup cols="2">
          <tbody>
            <row>
              <entry role="rowhead">Key</entry>
              <entry><constant><xsl:value-of select="@key" /></constant></entry>
            </row>
            
            <row>
              <entry role="rowhead">Required</entry>
              <entry><constant><xsl:value-of select="@required" /></constant></entry>
            </row>
            
            <row>
              <entry role="rowhead">Level</entry>
              <entry><constant><xsl:value-of select="metadata/level" /></constant></entry>
            </row>
            
            <row>
              <entry role="rowhead">Description</entry>
              <entry>
                <xsl:apply-templates select="metadata/description" />
              </entry>
            </row>
            
            <row>
              <entry role="rowhead">Type</entry>
              <entry><constant><xsl:value-of select="@type" /></constant></entry>
            </row>
            
            <row>
              <entry role="rowhead">Default value</entry>
              <entry><constant><xsl:value-of select="@default" /></constant></entry>
            </row>
          </tbody>
        </tgroup>
      </informaltable>
    </section>
  </xsl:template>
  
  <!-- Certain Docbook elements -->
  <xsl:template match="db:chapter|db:para|db:section|db:title|db:subtitle|db:example|db:xref|db:programlisting|db:sgmltag|db:classname|db:filename|db:note|db:phrase">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
