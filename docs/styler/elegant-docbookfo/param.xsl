<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default">

  <!--
       Default DocBook XSL style parameters
  -->
  
  <!-- This style is XEP-only by design -->
  <xsl:variable name="xep.extensions">1</xsl:variable>
 
  <!-- Section numbering -->
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>  

  <!-- Fonts -->
  <xsl:param name="body.font.family">BookAntiquaCE,serif</xsl:param>
  <xsl:param name="body.font.size">10pt</xsl:param>
  <xsl:param name="title.font.family">BookAntiquaCE,serif</xsl:param>
  <xsl:param name="sans.font.family">BookAntiquaCE,serif</xsl:param>
  <xsl:param name="monospace.font.family">monospace</xsl:param>

  <!-- Page setup -->
  <xsl:param name="paper.type">A4</xsl:param>
  <xsl:param name="double.sided">0</xsl:param>
  
  <xsl:param name="page.margin.top">25mm</xsl:param>
  <xsl:param name="page.margin.bottom">18mm</xsl:param>
  <xsl:param name="page.margin.outer">13mm</xsl:param>
  <xsl:param name="page.margin.inner">30mm</xsl:param>
  
  <xsl:param name="body.margin.top">0mm</xsl:param>
  <xsl:param name="body.margin.bottom">7mm</xsl:param>
  <xsl:variable name="body.margin.inner">-<xsl:value-of select="$marginbar.width"/> - <xsl:value-of select="$marginbar.margin"/></xsl:variable>

  <xsl:param name="draft.watermark.image" select="'../../images/draft.png'"/>

  <!-- Paragraph properties -->
  <xsl:param name="line-height">1.6</xsl:param>
  <xsl:param name="line-height-shift-adjustment">disregard-shifts</xsl:param>
  <xsl:param name="hyphenate">true</xsl:param>
  <xsl:param name="alignment">justify</xsl:param>
                
  <xsl:variable name="shade.verbatim">1</xsl:variable>

  <!-- Table properties -->
  <xsl:param name="table.cell.border.color" select="'black'"/>
  <xsl:param name="table.cell.border.style" select="'solid'"/>
  <xsl:param name="table.cell.border.thickness" select="'0.0pt'"/>
  <xsl:param name="table.frame.border.color" select="'black'"/>
  <xsl:param name="table.frame.border.style" select="'solid'"/>
  <xsl:param name="table.frame.border.thickness" select="'0.2mm'"/>

  <!--
      Elegant-specific parameters
  -->

  <xsl:param name="marginbar.width">25mm</xsl:param>
  <xsl:param name="marginbar.margin">3mm</xsl:param>
  <xsl:param name="marginbar.line-height">1.0</xsl:param>

  <!-- Chapter title parameters -->
  <xsl:param name="chapter.title.number-block.height">34mm</xsl:param>
  <xsl:param name="chapter.title.margin-bottom">6mm</xsl:param>
  <xsl:param name="chapter.title.font-size">29pt</xsl:param>
  <xsl:param name="chapter.title.wrap-option">no-wrap</xsl:param>

  <!-- Sect1 title parameters -->
  <xsl:param name="sect1.title.space-before">8mm</xsl:param>
  <xsl:param name="sect1.title.margin-bottom">3mm</xsl:param>
  <xsl:param name="sect1.title.font-size">22pt</xsl:param>
  <xsl:param name="sect1.title.wrap-option">no-wrap</xsl:param>

  <!-- Sect2 title parameters -->
  <xsl:param name="sect2.title.space-before">4mm</xsl:param>
  <xsl:param name="sect2.title.margin-bottom">1mm</xsl:param>
  <xsl:param name="sect2.title.font-size">17pt</xsl:param>
  <xsl:param name="sect2.title.wrap-option">no-wrap</xsl:param>

  <!-- Sect3 title parameters -->
  <xsl:param name="sect3.title.space-before">4mm</xsl:param>
  <xsl:param name="sect3.title.margin-bottom">0mm</xsl:param>
  <xsl:param name="sect3.title.font-size">10pt</xsl:param>
  <xsl:param name="sect3.title.wrap-option">no-wrap</xsl:param>

  <!-- Abstract title parameters -->
  <xsl:param name="abstract.title.margin-bottom">8mm</xsl:param>
  <xsl:param name="abstract.title.margin-top">15mm</xsl:param>
  <xsl:param name="abstract.title.spacing">20mm</xsl:param>
  <xsl:param name="abstract.title.font-size">30pt</xsl:param>

  <!-- List of figures title parameters -->
  <xsl:param name="list-of-figures.title.margin-bottom">8mm</xsl:param>
  <xsl:param name="list-of-figures.title.margin-top">54mm</xsl:param>
  <xsl:param name="list-of-figures.title.font-size">30pt</xsl:param>
  <xsl:param name="list-of-figures.title.wrap-option">no-wrap</xsl:param>

  <!-- List of tables title parameters -->
  <xsl:param name="list-of-tables.title.margin-bottom">8mm</xsl:param>
  <xsl:param name="list-of-tables.title.margin-top">54mm</xsl:param>
  <xsl:param name="list-of-tables.title.font-size">30pt</xsl:param>
  <xsl:param name="list-of-tables.title.wrap-option">no-wrap</xsl:param>

  <!-- List of examples title parameters -->
  <xsl:param name="list-of-examples.title.margin-bottom">8mm</xsl:param>
  <xsl:param name="list-of-examples.title.margin-top">54mm</xsl:param>
  <xsl:param name="list-of-examples.title.font-size">30pt</xsl:param>
  <xsl:param name="list-of-examples.title.wrap-option">no-wrap</xsl:param>

  <!-- Table of contents title parameters -->
  <xsl:param name="table-of-contents.title.margin-bottom">8mm</xsl:param>
  <xsl:param name="table-of-contents.title.margin-top">54mm</xsl:param>
  <xsl:param name="table-of-contents.title.font-size">30pt</xsl:param>
  <xsl:param name="table-of-contents.title.wrap-option">no-wrap</xsl:param>

  <!-- Bibliography title parameters -->
  <xsl:param name="biblio.title.margin-bottom">8mm</xsl:param>
  <xsl:param name="biblio.title.margin-top">54mm</xsl:param>
  <xsl:param name="biblio.title.font-size">30pt</xsl:param>
  <xsl:param name="biblio.title.wrap-option">no-wrap</xsl:param>

  <xsl:param name="biblio.line-height">1.4</xsl:param>
  <xsl:param name="biblio.numbered">0</xsl:param>
  <xsl:param name="biblioentry.space-after">6mm</xsl:param>
  <xsl:param name="biblioentry.start-indent">0.5in</xsl:param>

  <xsl:param name="superscript.baseline-shift">20%</xsl:param>
  <xsl:param name="superscript.font-size">80%</xsl:param>
  <xsl:param name="subscript.baseline-shift">-15%</xsl:param>
  <xsl:param name="subscript.font-size">80%</xsl:param>

  <xsl:param name="footnote.gap">1.5mm</xsl:param>

  <xsl:param name="graphic.border-width">0.15mm</xsl:param>

  <!-- Derived parameters -->
  <xsl:variable name="marginbar.indent-size"><xsl:value-of select="$marginbar.width"/> + <xsl:value-of select="$marginbar.margin"/></xsl:variable>

  <xsl:variable name="marginbar.space-before">(<xsl:value-of select="$line-height"/> - <xsl:value-of select="$marginbar.line-height"/>) * 0.5 * <xsl:value-of select="$body.font.size"/></xsl:variable>
  
  <!--
      Overriden default DocBook attibute sets
  -->

  <!-- line-height-shift-adjustment added -->
  <xsl:attribute-set name="root.properties">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$body.font.family"/>
    </xsl:attribute>
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.size"/>
    </xsl:attribute>
    <xsl:attribute name="text-align">
      <xsl:value-of select="$alignment"/>
    </xsl:attribute>
    <xsl:attribute name="line-height">
      <xsl:value-of select="$line-height"/>
    </xsl:attribute>
    <xsl:attribute name="line-height-shift-adjustment">
      <xsl:value-of select="$line-height-shift-adjustment"/>
    </xsl:attribute>
    <xsl:attribute name="font-selection-strategy">character-by-character</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="normal.para.spacing">
    <xsl:attribute name="space-before.optimum">0.7em</xsl:attribute>
    <xsl:attribute name="line-height-shift-adjustment">
      <xsl:value-of select="$line-height-shift-adjustment"/>
    </xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="simple.para.spacing">
    <xsl:attribute name="space-before.optimum">0.3em</xsl:attribute>
    <xsl:attribute name="line-height-shift-adjustment">
      <xsl:value-of select="$line-height-shift-adjustment"/>
    </xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="list.block.spacing">
    <xsl:attribute name="margin-left">0.5in</xsl:attribute>
    <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="list.item.spacing">
    <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="monospace.properties">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$monospace.font.family"/>
    </xsl:attribute>
    <xsl:attribute name="font-size">0.9em</xsl:attribute>
    <xsl:attribute name="line-height">1</xsl:attribute>
  </xsl:attribute-set>
 
  <xsl:attribute-set name="biblioentry.spacing">
    <xsl:attribute name="line-height">1.3</xsl:attribute>
    <xsl:attribute name="space-before.optimum">0.3em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="figure.style">
    <xsl:attribute name="border-color">black</xsl:attribute>
    <xsl:attribute name="border-style">solid</xsl:attribute>
    <xsl:attribute name="border-width">0.1mm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="shade.verbatim.style">
    <xsl:attribute name="background-color">#E8E8E8</xsl:attribute>
    <xsl:attribute name="border-color">black</xsl:attribute>
    <xsl:attribute name="border-style">solid</xsl:attribute>
    <xsl:attribute name="border-width">0.1mm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="admonition.title.properties">
    <xsl:attribute name="font-family">SansSerif-Bold</xsl:attribute>
    <xsl:attribute name="font-size">90%</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="footer.content.properties">
    <xsl:attribute name="font-family">SansSerif</xsl:attribute>
    <xsl:attribute name="font-size">8pt</xsl:attribute>
    <xsl:attribute name="margin-left">0mm</xsl:attribute>
    <xsl:attribute name="margin-right">0mm </xsl:attribute>
  </xsl:attribute-set> 

  <xsl:attribute-set name="verbatim.properties">
    <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
    <xsl:attribute name="padding">1em</xsl:attribute>
    <xsl:attribute name="margin">0em</xsl:attribute> <!-- Remove this and see what will happen. A bug in xep or in my understanding of FO ? ;) -->
  </xsl:attribute-set>

  <xsl:attribute-set name="formal.title.properties"
                     use-attribute-sets="normal.para.spacing">
    <xsl:attribute name="line-height">0.95</xsl:attribute>
    <xsl:attribute name="hyphenate">false</xsl:attribute>
  </xsl:attribute-set>  

  <xsl:attribute-set name="blockquote.properties">
    <xsl:attribute name="start-indent">from-parent() + 0.5in</xsl:attribute>
    <xsl:attribute name="end-indent">from-parent() + 0.5in</xsl:attribute>
    <xsl:attribute name="space-after.minimum">0.5em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">2em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="blockquote.properties.in-list-item-body">
    <xsl:attribute name="space-after.minimum">0.5em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">2em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="table.properties">
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="table.cell.padding">
    <xsl:attribute name="padding-left">2pt</xsl:attribute>
    <xsl:attribute name="padding-right">2pt</xsl:attribute>
    <xsl:attribute name="padding-top">2pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
    <xsl:attribute name="start-indent">0mm</xsl:attribute>
    <xsl:attribute name="end-indent">0mm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="biblioentry.spacing">
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="line-height"><xsl:value-of select="$biblio.line-height"/></xsl:attribute>
  </xsl:attribute-set>

</xsl:stylesheet>
