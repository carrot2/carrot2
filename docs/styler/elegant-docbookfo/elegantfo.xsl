<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default">

  <!--
      TODO:
        
        * add start-indent and end-indent to all fo:floats
        * add body.margin.inner to all fo:simple-page-masters
        * special orderedlist entry (caption)
        * double-sided -> master sequences change (+margin float start change)
        * section title -> keeps
        * minimum - optimum - maximum values
        * warning when using from-parent() in start-indent in a list-item-body
  -->

  <xsl:import href="../docbook/xsl/fo/docbook.xsl"/>
  <xsl:import href="param.xsl"/>

  <xsl:import href="autotoc.xsl"/>
  <xsl:import href="biblio.xsl"/>
  <xsl:import href="block.xsl"/>
  <xsl:import href="component.xsl"/>
  <xsl:import href="division.xsl"/>
  <xsl:import href="footnote.xsl"/>
  <xsl:import href="formal.xsl"/>
  <xsl:import href="graphic.xsl"/>
  <xsl:import href="inline.xsl"/>
  <xsl:import href="pagesetup.xsl"/>
  <xsl:import href="sections.xsl"/>
  <xsl:import href="table.xsl"/>
  <xsl:import href="titlepage.xsl"/>
  <xsl:import href="toc.xsl"/>

  <xsl:import href="margin.xsl"/>

  <xsl:output indent="yes"/>
 
</xsl:stylesheet>
