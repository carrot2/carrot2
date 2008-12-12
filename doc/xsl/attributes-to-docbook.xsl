<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:db="http://docbook.org/ns/docbook"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                exclude-result-prefixes="db">

  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="no"
       encoding="utf-8" cdata-section-elements="programlisting" />
       
  <xsl:param name="metadata" select="document('components-metadata.xml')" />
  <xsl:param name="javadoc.url" />
  
  <xsl:template match="/">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="db:attribute-reference">
    <xsl:apply-templates select="$metadata/processing-component-docs/sources/processing-component-doc" />
    <xsl:apply-templates select="$metadata/processing-component-docs/algorithms/processing-component-doc" />
  </xsl:template>

  <xsl:template match="processing-component-doc">
    <section xml:id="section.component.{component-descriptor/@id}">
      <title><xsl:apply-templates select="component-descriptor/title" /></title>

      <para>
        <xsl:apply-templates select="component-descriptor/description" />
      </para>

      <xsl:variable name="doc" select="." />
      <xsl:for-each select="$doc/groups/string">
        <xsl:variable name="group" select="string(.)" />
        
        <xsl:if test="$doc/attribute[attribute-descriptor/metadata/group = $group]">
          <section role="notoc">
            <title><xsl:value-of select="$group" /></title>
            <xsl:apply-templates select="$doc/attribute[attribute-descriptor/metadata/group = $group]" />
          </section>
        </xsl:if>
      </xsl:for-each>
      
      <xsl:if test="$doc/attribute[not(attribute-descriptor/metadata/group)]">
        <section>
          <title>Ungrouped</title>
          <xsl:apply-templates select="$doc/attribute[not(attribute-descriptor/metadata/group)]" />
        </section>
      </xsl:if>
    </section>
  </xsl:template>

  <xsl:template match="attribute">
    <xsl:apply-templates select="attribute-descriptor" />
  </xsl:template>

  <xsl:template match="attribute-descriptor">
    <section xml:id="section.attribute.{@key}">
      <title>
        <xsl:choose>
          <xsl:when test="metadata/label">
            <xsl:value-of select="metadata/label" />
          </xsl:when>
          
          <xsl:otherwise>
            <xsl:value-of select="metadata/title" />
          </xsl:otherwise>
        </xsl:choose>
        
      </title>
      <informaltable frame="none">
        <tgroup cols="2">
          <tbody>
            <row>
              <entry role="rowhead">Key</entry>
              <entry><constant><xsl:value-of select="@key" /></constant></entry>
            </row>
            
            <row>
              <entry role="rowhead">Direction</entry>
              <entry>
                <xsl:if test="annotations[annotation = 'Input']">
                  Input
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Input' and annotation = 'Output']">
                  and
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Output']">
                  Output
                </xsl:if>
              </entry>
            </row>
            
            <xsl:if test="annotations[annotation = 'Input']">
              <row>
                <entry role="rowhead">Level</entry>
                <entry><constant><xsl:value-of select="metadata/level" /></constant></entry>
              </row>
            </xsl:if>
            
            <row>
              <entry role="rowhead">Description</entry>
              <entry>
                <xsl:apply-templates select="metadata/title" />.
                <xsl:apply-templates select="metadata/description" />
              </entry>
            </row>
            
            <xsl:if test="annotations[annotation = 'Input']">
              <row>
                <entry role="rowhead">Required</entry>
                <entry>
                  <constant>
                    <xsl:choose>
                      <xsl:when test="@required = 'true'">yes</xsl:when>
                      <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                  </constant>
                </entry>
              </row>
            </xsl:if>
            
            <row>
              <entry role="rowhead">Scope</entry>
              <entry>
                <xsl:if test="annotations[annotation = 'Init']">
                  Initialization time
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Init' and annotation = 'Processing']">
                  and
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Processing']">
                  Processing time
                </xsl:if>
              </entry>
            </row>
            
            <row>
              <entry role="rowhead">Value type</entry>
              <entry><constant><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="@type" /></xsl:call-template></constant></entry>
            </row>
            
            <row>
              <entry role="rowhead">Default value</entry>
              <entry>
                <xsl:choose>
                  <xsl:when test="@default">
                    <constant><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="@default" /></xsl:call-template></constant>
                  </xsl:when>
                  
                  <xsl:otherwise><emphasis>none</emphasis></xsl:otherwise>
                </xsl:choose>
              </entry>
            </row>
            
            <xsl:if test="allowed-values">
              <row>
                <entry role="rowhead">Allowed values</entry>
                <entry>
                  <itemizedlist>
                    <xsl:apply-templates select="allowed-values/value" />
                  </itemizedlist>
                </entry>
              </row>
            </xsl:if>
            
            <xsl:apply-templates select="constraints/constraint" />
          </tbody>
        </tgroup>
      </informaltable>
    </section>
  </xsl:template>

  <xsl:template name="javadoc-link">
    <xsl:param name="value" />
    <xsl:choose>
      <xsl:when test="starts-with($value, 'org.carrot2') and string-length($javadoc.url) > 0">
        <link xlink:href="{$javadoc.url}/{translate($value, '.$', '/.')}.html"><xsl:value-of select="$value" /></link>
      </xsl:when>
      
      <xsl:otherwise><xsl:value-of select="$value" /></xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>  
  
  <xsl:template match="allowed-values/value">
    <listitem><code><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="string(.)" /></xsl:call-template></code></listitem>
  </xsl:template>
  
  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.ImplementingClassesConstraint']">
    <row>
      <entry role="rowhead">Allowed value types</entry>
      <entry>
        Allowed value types:
        <itemizedlist>
          <xsl:for-each select="classes/class">
            <listitem>
              <constant><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="string(.)" /></xsl:call-template></constant>
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
    <xsl:message>Unsupported constraint type:    <xsl:value-of select="@class" /></xsl:message>
  </xsl:template>
  
  <!-- Some mappings between HTML elements and their DocBook counterparts -->
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
  
  <xsl:template match="table">
    <informaltable frame="none">
      <tgroup cols="{count(.//th)}">
        <xsl:apply-templates />
      </tgroup>
    </informaltable>
  </xsl:template>
  
  <xsl:template match="thead">
    <thead>
      <xsl:apply-templates />
    </thead>
  </xsl:template>

  <xsl:template match="tbody">
    <tbody>
      <xsl:apply-templates />
    </tbody>
  </xsl:template>

  <xsl:template match="tr">
    <row>
      <xsl:apply-templates />
    </row>
  </xsl:template>

  <xsl:template match="th|td">
    <entry>
      <xsl:if test="@align">
        <xsl:attribute name="align"><xsl:value-of select="@align" /></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates />
    </entry>
  </xsl:template>

  <xsl:template match="a[@href]">
    <link xlink:href="{@href}"><xsl:apply-templates /></link>
  </xsl:template>
  
  <xsl:template match="strong">
    <emphasis role="bold"><xsl:apply-templates /></emphasis>
  </xsl:template>
  
  <!-- Copy certain Docbook elements -->
  <xsl:template match="db:chapter|db:appendix|db:para|db:section|db:title|db:subtitle|db:example|db:xref|db:programlisting|db:sgmltag|db:classname|db:filename|db:note|db:phrase">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
