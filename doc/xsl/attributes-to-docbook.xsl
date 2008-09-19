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
    <xsl:apply-templates select="$metadata/processing-component-docs/algorithms/processing-component-doc" />
  </xsl:template>

  <xsl:template match="processing-component-doc">
    <section xml:id="section.{component-descriptor/@id}">
      <title><xsl:apply-templates select="component-descriptor/title" /></title>

      <xsl:call-template name="attribute-section">
        <xsl:with-param name="section-title" select="'Initialization input attributes'" />
        <xsl:with-param name="doc" select="." />
        <xsl:with-param name="scope" select="'Init'" />
        <xsl:with-param name="direction" select="'Input'" />
      </xsl:call-template>

      <xsl:call-template name="attribute-section">
        <xsl:with-param name="section-title" select="'Processing input attributes'" />
        <xsl:with-param name="doc" select="." />
        <xsl:with-param name="scope" select="'Processing'" />
        <xsl:with-param name="direction" select="'Input'" />
      </xsl:call-template>

      <xsl:call-template name="attribute-section">
        <xsl:with-param name="section-title" select="'Processing output attributes'" />
        <xsl:with-param name="doc" select="." />
        <xsl:with-param name="scope" select="'Processing'" />
        <xsl:with-param name="direction" select="'Output'" />
      </xsl:call-template>
    </section>
  </xsl:template>

  <xsl:template name="attribute-section">
    <xsl:param name="doc" />
    <xsl:param name="scope" />
    <xsl:param name="direction" />
    <xsl:param name="section-title" />
    
    <xsl:if test="$doc/attribute[contains(string(attribute-descriptor/annotations), $scope) and contains(string(attribute-descriptor/annotations), $direction)]">
      <section>
        <title><xsl:value-of select="$section-title" /></title>
        
        <xsl:for-each select="$doc/groups/string">
          <xsl:variable name="group" select="string(.)" />
          
          <xsl:if test="$doc/attribute[contains(string(attribute-descriptor/annotations), $scope) and contains(string(attribute-descriptor/annotations), $direction) and attribute-descriptor/metadata/group = $group]">
            <section>
              <title><xsl:value-of select="$group" /></title>
              <xsl:apply-templates select="$doc/attribute[contains(string(attribute-descriptor/annotations), $scope) and contains(string(attribute-descriptor/annotations), $direction) and attribute-descriptor/metadata/group = $group]" />
            </section>
          </xsl:if>
        </xsl:for-each>
        
        <xsl:if test="$doc/attribute[contains(string(attribute-descriptor/annotations), $scope) and contains(string(attribute-descriptor/annotations), $direction) and not(attribute-descriptor/metadata/group)]">
          <section>
            <title>Ungrouped</title>
            <xsl:apply-templates select="$doc/attribute[contains(string(attribute-descriptor/annotations), $scope) and contains(string(attribute-descriptor/annotations), $direction) and not(attribute-descriptor/metadata/group)]" />
          </section>
        </xsl:if>
      </section>
    </xsl:if>
  </xsl:template>

  <xsl:template match="attribute">
    <xsl:apply-templates select="attribute-descriptor" />
  </xsl:template>

  <xsl:template match="attribute-descriptor">
    <section>
      <title><xsl:value-of select="metadata/title" /></title>
      <informaltable frame="none">
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
              <entry>
                <xsl:choose>
                  <xsl:when test="@default">
                    <constant><xsl:value-of select="@default" /></constant>
                  </xsl:when>
                  
                  <xsl:otherwise><emphasis>none</emphasis></xsl:otherwise>
                </xsl:choose>
              </entry>
            </row>
            
            <xsl:apply-templates select="constraints/constraint" />
          </tbody>
        </tgroup>
      </informaltable>
    </section>
  </xsl:template>
  
  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.ImplementingClassesConstraint']">
    <row>
      <entry role="rowhead">Allowed value types</entry>
      <entry>
        Allowed value types:
        <itemizedlist>
          <xsl:for-each select="classes/class">
            <listitem>
              <constant><xsl:apply-templates /></constant>
            </listitem>
          </xsl:for-each>
        </itemizedlist>
        <xsl:choose>
          <xsl:when test="@strict = 'true'">
            No other assignable value types are allowed.
          </xsl:when>
          <xsl:otherwise>
            Other assignable value types are allowed.
          </xsl:otherwise>
        </xsl:choose>
      </entry>
    </row>
  </xsl:template>

  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.IntRangeConstraint' or @class = 'org.carrot2.util.attribute.constraint.DoubleRangeConstraint']">
    <xsl:if test="@min">
      <row>
        <entry role="rowhead">Min value</entry>
        <entry><constant><xsl:value-of select="@min" /></constant></entry>
      </row>
    </xsl:if>
    <xsl:if test="@max">
      <row>
        <entry role="rowhead">Max value</entry>
        <entry><constant><xsl:value-of select="@max" /></constant></entry>
      </row>
    </xsl:if>
  </xsl:template>

  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.NotBlankConstraint']">
    <row>
      <entry role="rowhead">Value content</entry>
      <entry>Must not be blank</entry>
    </row>
  </xsl:template>
    
  <xsl:template match="constraint">
    <xsl:message>Unsupported constraint type:<xsl:value-of select="@class" /></xsl:message>
  </xsl:template>
  
  <!-- Some mappings between HTML elements and their DocBook counterparts-->
  <xsl:template match="ul">
    <itemizedlist>
      <xsl:apply-templates />
    </itemizedlist>
  </xsl:template>
  
  <xsl:template match="li">
    <listitem>
      <xsl:apply-templates />
    </listitem>
  </xsl:template>

  <xsl:template match="code">
    <code>
      <xsl:apply-templates />
    </code>
  </xsl:template>

  <xsl:template match="p">
    <para>
      <xsl:apply-templates />
    </para>
  </xsl:template>

  <!-- Copy certain Docbook elements -->
  <xsl:template match="db:chapter|db:para|db:section|db:title|db:subtitle|db:example|db:xref|db:programlisting|db:sgmltag|db:classname|db:filename|db:note|db:phrase">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
