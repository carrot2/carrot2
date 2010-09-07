<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:db="http://docbook.org/ns/docbook"
                xmlns:xlink="http://www.w3.org/1999/xlink">

  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="no"
       encoding="UTF-8" cdata-section-elements="programlisting" />
       
  <xsl:param name="metadata" select="document('components-metadata.xml')" />
  <xsl:param name="carrot2.javadoc.url" />
  
  <xsl:template match="/">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="db:attribute-reference">
    <xsl:apply-templates select="$metadata/processing-component-docs/algorithms/processing-component-doc" />
    <xsl:apply-templates select="$metadata/processing-component-docs/sources/processing-component-doc" />
  </xsl:template>

  <xsl:template match="processing-component-doc">
    <db:section xml:id="section.component.{component-descriptor/@id}">
      <db:title><xsl:apply-templates select="component-descriptor/title" /></db:title>

      <db:para>
        <xsl:apply-templates select="component-descriptor/description" />
      </db:para>

      <xsl:variable name="doc" select="." />
      <db:section role="notoc" xml:id="section.component.{component-descriptor/@id}.by-level">
        <db:title><xsl:apply-templates select="component-descriptor/title" /> input attributes by level</db:title>
        
        <xsl:call-template name="processing-component-doc-by-level">
          <xsl:with-param name="doc" select="$doc" />
          <xsl:with-param name="level" select="'Basic'" />
          <xsl:with-param name="level-id" select="'BASIC'" />
        </xsl:call-template>
        
        <xsl:call-template name="processing-component-doc-by-level">
          <xsl:with-param name="doc" select="$doc" />
          <xsl:with-param name="level" select="'Medium'" />
          <xsl:with-param name="level-id" select="'MEDIUM'" />
        </xsl:call-template>
        
        <xsl:call-template name="processing-component-doc-by-level">
          <xsl:with-param name="doc" select="$doc" />
          <xsl:with-param name="level" select="'Advanced'" />
          <xsl:with-param name="level-id" select="'ADVANCED'" />
        </xsl:call-template>
        
        <xsl:apply-templates select="$doc/attribute/attribute-descriptor" mode="level-check" />
      </db:section>
      
      <db:section role="notoc" xml:id="section.component.{component-descriptor/@id}.by-direction">
        <db:title><xsl:apply-templates select="component-descriptor/title" /> attributes by direction</db:title>
        
        <xsl:call-template name="processing-component-doc-by-direction">
          <xsl:with-param name="doc" select="$doc" />
          <xsl:with-param name="direction" select="'Input'" />
        </xsl:call-template>
        
        <xsl:call-template name="processing-component-doc-by-direction">
          <xsl:with-param name="doc" select="$doc" />
          <xsl:with-param name="direction" select="'Output'" />
        </xsl:call-template>
      </db:section>

      <xsl:for-each select="$doc/groups/string">
        <xsl:variable name="group" select="string(.)" />
        
        <xsl:if test="$doc/attribute[attribute-descriptor/metadata/group = $group]">
          <db:section role="notoc">
            <db:title><xsl:value-of select="$group" /></db:title>
            <xsl:apply-templates select="$doc/attribute[attribute-descriptor/metadata/group = $group]">
              <xsl:sort select="concat(attribute-descriptor/metadata/label, attribute-descriptor/metadata/title)" />
            </xsl:apply-templates>
          </db:section>
        </xsl:if>
      </xsl:for-each>
      
      <xsl:if test="$doc/attribute[not(attribute-descriptor/metadata/group)]">
        <db:section>
          <db:title>Ungrouped</db:title>
          <xsl:apply-templates select="$doc/attribute[not(attribute-descriptor/metadata/group)]">
            <xsl:sort select="concat(attribute-descriptor/metadata/label, attribute-descriptor/metadata/title)" />
          </xsl:apply-templates>
        </db:section>
      </xsl:if>
    </db:section>
  </xsl:template>

  <xsl:template name="processing-component-doc-by-level">
    <xsl:param name="doc" />
    <xsl:param name="level" />
    <xsl:param name="level-id" />
    
    <xsl:variable name="descriptors" select="$doc/attribute[string(descendant::level) = $level-id]/attribute-descriptor" />
    
    <xsl:if test="$descriptors">
      <db:section role="notoc">
        <db:title><xsl:value-of select="$level" /></db:title>
  
        <db:para>
          <db:itemizedlist spacing="compact">
            <xsl:apply-templates select="$descriptors" mode="links">
              <xsl:sort select="concat(metadata/label, metadata/title)" />
            </xsl:apply-templates>
          </db:itemizedlist>
        </db:para>
      </db:section>
    </xsl:if>
  </xsl:template>

  <xsl:template match="attribute-descriptor" mode="level-check">
    <xsl:if test="annotations/annotation[string(.) = 'Input'] and (not(metadata/level) or not(contains('BASIC MEDIUM ADVANCED', string(metadata/level))))">
      <xsl:message>Attribute level not defined for: <xsl:value-of select="@key" /></xsl:message>
    </xsl:if>
  </xsl:template>

  <xsl:template name="processing-component-doc-by-direction">
    <xsl:param name="doc" />
    <xsl:param name="direction" />
    
    <xsl:variable name="descriptors" select="$doc/attribute[descendant::annotation[string(.) = $direction]]/attribute-descriptor" />
    
    <xsl:if test="$descriptors">
      <db:section role="notoc">
        <db:title><xsl:value-of select="$direction" /></db:title>
  
        <db:para>
          <db:itemizedlist spacing="compact">
            <xsl:apply-templates select="$descriptors" mode="links">
              <xsl:sort select="concat(metadata/label, metadata/title)" />
            </xsl:apply-templates>
          </db:itemizedlist>
        </db:para>
      </db:section>
    </xsl:if>
  </xsl:template>

  <xsl:template match="attribute-descriptor" mode="links">
    <xsl:variable name="keyId">
      <xsl:call-template name="keyId">
        <xsl:with-param name="key" select="@key" />
      </xsl:call-template>
    </xsl:variable>

    <db:listitem>
      <db:link linkend="section.attribute.{$keyId}"><xsl:apply-templates select="." mode="label" /></db:link>
    </db:listitem>
  </xsl:template>

  <xsl:template match="attribute">
    <xsl:apply-templates select="attribute-descriptor" />
  </xsl:template>

  <xsl:template name="keyId">
    <xsl:param name="key" />
    
    <xsl:choose>
      <xsl:when test="count($metadata//attribute-descriptor[@key = $key]) > 1">
        <xsl:value-of select="../../component-descriptor/@id" />.<xsl:value-of select="@key" />
      </xsl:when>
      
      <xsl:otherwise><xsl:value-of select="@key" /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="attribute-descriptor" mode="label">
    <xsl:choose>
      <xsl:when test="metadata/label">
        <xsl:value-of select="metadata/label" />
      </xsl:when>
      
      <xsl:otherwise>
        <xsl:value-of select="metadata/title" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="attribute-descriptor">
    <!-- 
          Some attributes are common to all components. If we want to repeat them for
          each component, we need to prefix their section ids with component id
          to ensure unique ids. 
      -->
    <xsl:variable name="keyId">
      <xsl:call-template name="keyId">
        <xsl:with-param name="key" select="@key" />
      </xsl:call-template>
    </xsl:variable>
    
    <db:section xml:id="section.attribute.{$keyId}">
      <db:title><xsl:apply-templates select="." mode="label" /></db:title>
      
      <db:informaltable frame="none">
        <db:tgroup cols="2">
          <db:tbody>
            <db:row>
              <db:entry role="rowhead">Key</db:entry>
              <db:entry><db:link linkend="section.attribute.{$keyId}"><db:constant><xsl:value-of select="@key" /></db:constant></db:link></db:entry>
            </db:row>
            
            <db:row>
              <db:entry role="rowhead">Direction</db:entry>
              <db:entry>
                <xsl:if test="annotations[annotation = 'Input']">
                  <db:constant>Input</db:constant>
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Input' and annotation = 'Output']">
                  and
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Output']">
                  <db:constant>Output</db:constant>
                </xsl:if>
              </db:entry>
            </db:row>
            
            <xsl:if test="annotations[annotation = 'Input']">
              <db:row>
                <db:entry role="rowhead">Level</db:entry>
                <db:entry><db:constant><xsl:value-of select="metadata/level" /></db:constant></db:entry>
              </db:row>
            </xsl:if>
            
            <db:row>
              <db:entry role="rowhead">Description</db:entry>
              <db:entry>
                <xsl:apply-templates select="metadata/title" />.
                <xsl:apply-templates select="metadata/description" />
              </db:entry>
            </db:row>
            
            <xsl:if test="annotations[annotation = 'Input']">
              <db:row>
                <db:entry role="rowhead">Required</db:entry>
                <db:entry>
                  <db:constant>
                    <xsl:choose>
                      <xsl:when test="@required = 'true'">yes</xsl:when>
                      <xsl:otherwise>no</xsl:otherwise>
                    </xsl:choose>
                  </db:constant>
                </db:entry>
              </db:row>
            </xsl:if>
            
            <db:row>
              <db:entry role="rowhead">Scope</db:entry>
              <db:entry>
                <xsl:if test="annotations[annotation = 'Init']">
                  Initialization time
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Init' and annotation = 'Processing']">
                  and
                </xsl:if>
                <xsl:if test="annotations[annotation = 'Processing']">
                  Processing time
                </xsl:if>
              </db:entry>
            </db:row>
            
            <db:row>
              <db:entry role="rowhead">Value type</db:entry>
              <db:entry><db:constant><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="@type" /></xsl:call-template></db:constant></db:entry>
            </db:row>
            
            <db:row>
              <db:entry role="rowhead">Default value</db:entry>
              <db:entry>
                <xsl:choose>
                  <xsl:when test="@default">
                    <db:constant><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="@default" /></xsl:call-template></db:constant>
                  </xsl:when>
                  
                  <xsl:otherwise><db:emphasis>none</db:emphasis></xsl:otherwise>
                </xsl:choose>
              </db:entry>
            </db:row>
            
            <xsl:if test="allowed-values">
              <db:row>
                <db:entry role="rowhead">Allowed values</db:entry>
                <db:entry>
                  <db:itemizedlist>
                    <xsl:apply-templates select="allowed-values/value" />
                  </db:itemizedlist>
                  <xsl:if test="allowed-values/@other-values-allowed = 'true'">
                    Other values are allowed.
                  </xsl:if>
                </db:entry>
              </db:row>
            </xsl:if>
            
            <xsl:apply-templates select="constraints/constraint" />
          </db:tbody>
        </db:tgroup>
      </db:informaltable>
    </db:section>
  </xsl:template>

  <xsl:template name="javadoc-link">
    <xsl:param name="value" />
    <xsl:choose>
      <xsl:when test="starts-with($value, 'org.carrot2') and string-length($carrot2.javadoc.url) > 0">
        <db:link xlink:href="{$carrot2.javadoc.url}/{translate($value, '.$', '/.')}.html"><xsl:value-of select="$value" /></db:link>
      </xsl:when>
      
      <xsl:otherwise><xsl:value-of select="$value" /></xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>  
  
  <xsl:template match="allowed-values/value">
    <db:listitem>
      <db:code><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="string(.)" /></xsl:call-template></db:code>
      <xsl:if test="@label">
        &#160;<db:phrase role="human-readable-label">(<xsl:value-of select="@label" />)</db:phrase>
      </xsl:if>
    </db:listitem>
  </xsl:template>
  
  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.ImplementingClassesConstraint']">
    <db:row>
      <db:entry role="rowhead">Allowed value types</db:entry>
      <db:entry>
        Allowed value types:
        <db:itemizedlist>
          <xsl:for-each select="classes/class">
            <db:listitem>
              <db:constant><xsl:call-template name="javadoc-link"><xsl:with-param name="value" select="string(.)" /></xsl:call-template></db:constant>
            </db:listitem>
          </xsl:for-each>
        </db:itemizedlist>
        <xsl:choose>
          <xsl:when test="@strict = 'true'">
            No other assignable value types are allowed.
          </xsl:when>
          <xsl:otherwise>
            Other assignable value types are allowed.
          </xsl:otherwise>
        </xsl:choose>
      </db:entry>
    </db:row>
  </xsl:template>

  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.IntRangeConstraint' or @class = 'org.carrot2.util.attribute.constraint.DoubleRangeConstraint']">
    <xsl:if test="@min">
      <db:row>
        <db:entry role="rowhead">Min value</db:entry>
        <db:entry><db:constant><xsl:value-of select="@min" /></db:constant></db:entry>
      </db:row>
    </xsl:if>
    <xsl:if test="@max">
      <db:row>
        <db:entry role="rowhead">Max value</db:entry>
        <db:entry><db:constant><xsl:value-of select="@max" /></db:constant></db:entry>
      </db:row>
    </xsl:if>
  </xsl:template>

  <xsl:template match="constraint[@class = 'org.carrot2.util.attribute.constraint.NotBlankConstraint']">
    <db:row>
      <db:entry role="rowhead">Value content</db:entry>
      <db:entry>Must not be blank</db:entry>
    </db:row>
  </xsl:template>
    
  <xsl:template match="constraint">
    <xsl:message>Unsupported constraint type:
    <xsl:value-of select="@class" /></xsl:message>
  </xsl:template>
  
  <!-- Some mappings between HTML elements and their DocBook counterparts -->
  <xsl:template match="ul">
    <db:itemizedlist>
      <xsl:apply-templates />
    </db:itemizedlist>
  </xsl:template>
  
  <xsl:template match="ol">
    <db:orderedlist>
      <xsl:apply-templates />
    </db:orderedlist>
  </xsl:template>
  
  <xsl:template match="dl">
    <db:variablelist>
      <xsl:for-each select="dt">
        <db:varlistentry>
           <db:term><xsl:apply-templates select="." /></db:term>
           <db:listitem><db:para><xsl:apply-templates select="following-sibling::dd[1]" /></db:para></db:listitem>
        </db:varlistentry>
      </xsl:for-each>
    </db:variablelist>
  </xsl:template>
  
  <xsl:template match="li">
    <db:listitem>
      <xsl:apply-templates />
    </db:listitem>
  </xsl:template>

  <xsl:template match="code|tt">
    <db:code>
      <xsl:apply-templates />
    </db:code>
  </xsl:template>

  <xsl:template match="p">
    <db:para>
      <xsl:apply-templates />
    </db:para>
  </xsl:template>
  
  <xsl:template match="i">
    <db:emphasis>
      <xsl:apply-templates />
    </db:emphasis>
  </xsl:template>
  
  <xsl:template match="b|strong">
    <db:emphasis role="bold">
      <xsl:apply-templates />
    </db:emphasis>
  </xsl:template>
  
  <xsl:template match="table">
    <db:informaltable frame="none">
      <db:tgroup cols="{count(.//th)}">
        <xsl:apply-templates />
      </db:tgroup>
    </db:informaltable>
  </xsl:template>
  
  <xsl:template match="thead">
    <db:thead>
      <xsl:apply-templates />
    </db:thead>
  </xsl:template>

  <xsl:template match="tbody">
    <db:tbody>
      <xsl:apply-templates />
    </db:tbody>
  </xsl:template>

  <xsl:template match="tr">
    <db:row>
      <xsl:apply-templates />
    </db:row>
  </xsl:template>

  <xsl:template match="th|td">
    <db:entry>
      <xsl:if test="@align">
        <xsl:attribute name="align"><xsl:value-of select="@align" /></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates />
    </db:entry>
  </xsl:template>

  <xsl:template match="a[@href]">
    <db:link xlink:href="{@href}"><xsl:apply-templates /></db:link>
  </xsl:template>
  
  <xsl:template match="strong">
    <db:emphasis role="bold"><xsl:apply-templates /></db:emphasis>
  </xsl:template>
  
  <!-- Copy certain Docbook elements -->
  <xsl:template match="db:chapter|db:appendix|db:para|db:formalpara|db:section|db:title|db:subtitle|db:example|db:xref|db:programlisting|db:sgmltag|db:classname|db:filename|db:note|db:phrase|db:superscript|db:link|db:itemizedlist|db:listitem">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
