<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- This stylesheet was created by template/titlepage.xsl; do not edit it by hand. -->




  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="bookinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="bookinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="bookinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="bookinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="bookinfo/corpauthor"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="bookinfo/authorgroup"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.recto.auto.mode" select="bookinfo/author"/>
    
</xsl:template>
      
      
      
      
      
    

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.verso">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="bookinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/corpauthor"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/authorgroup"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/author"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/othercredit"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/pubdate"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/copyright"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/abstract"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/legalnotice"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="book.titlepage.verso.auto.mode" select="bookinfo/revhistory"/>
  
</xsl:template>
      
      
      
      
      
      
      
      
      
      
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.separator">
      <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" break-after="page"/>
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.before.verso">
      <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" break-after="page"/>
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="book.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="book.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="book.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="24.8832pt" space-before="2in" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="customized.book.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::book[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="20.736pt" space-before="15.552pt" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" font-size="17.28pt" keep-with-next="always" space-before="2in">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" space-before="2in">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" font-size="17.28pt" space-before="10.8pt" keep-with-next="always">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" font-size="14.4pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="customized.book.verso.title">
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="verso.authorgroup">
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" space-before="1em">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" font-size="8pt">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" space-before="1in">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="partinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="part.titlepage.recto.auto.mode" select="partinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="part.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="part.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="partinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="part.titlepage.recto.auto.mode" select="partinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="part.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="part.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

  
</xsl:template>
      
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="part.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="part.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="part.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="part.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="part.titlepage.recto.style" text-align="center" font-size="24.8832pt" space-before="18.6624pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="division.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::part[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="part.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="part.titlepage.recto.style" text-align="center" font-size="20.736pt" space-before="15.552pt" font-weight="bold" font-style="italic" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="part.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="partintroinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="partintroinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/corpauthor"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/authorgroup"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/author"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/othercredit"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/releaseinfo"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/copyright"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/legalnotice"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/pubdate"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/revision"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/revhistory"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="partintroinfo/abstract"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="partintro.titlepage.recto.auto.mode" select="docinfo/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="partintro.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="partintro.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="partintro.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style" text-align="center" font-size="24.8832pt" font-weight="bold" space-before="1em" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style" text-align="center" font-size="14.4pt" font-weight="bold" font-style="italic" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="partintro.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="partintro.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="partintro.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="referenceinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="referenceinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/corpauthor"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/authorgroup"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/author"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/othercredit"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/releaseinfo"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/copyright"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/legalnotice"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/pubdate"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/revision"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/revhistory"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="referenceinfo/abstract"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="reference.titlepage.recto.auto.mode" select="docinfo/abstract"/>
  
</xsl:template>
      
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="reference.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="reference.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="reference.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style" text-align="center" font-size="24.8832pt" space-before="18.6624pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="division.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::reference[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style" font-family="{$title.fontset}" text-align="center">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="reference.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="reference.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="reference.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="refsynopsisdivinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsynopsisdiv.titlepage.recto.auto.mode" select="refsynopsisdivinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsynopsisdiv.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsynopsisdiv.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  
</xsl:template>
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsynopsisdiv.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsynopsisdiv.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsynopsisdiv.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="refsynopsisdiv.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="refsynopsisdiv.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="refsynopsisdiv.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="refsectioninfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsection.titlepage.recto.auto.mode" select="refsectioninfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsection.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsection.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  
</xsl:template>
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsection.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsection.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsection.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="refsection.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="refsection.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="refsection.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="refsect1info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect1.titlepage.recto.auto.mode" select="refsect1info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect1.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect1.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  
</xsl:template>
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect1.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsect1.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsect1.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="refsect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="refsect1.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="refsect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="refsect2info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect2.titlepage.recto.auto.mode" select="refsect2info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect2.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect2.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  
</xsl:template>
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect2.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsect2.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsect2.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="refsect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="refsect2.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="refsect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="refsect3info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect3.titlepage.recto.auto.mode" select="refsect3info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect3.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="refsect3.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  
</xsl:template>
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="refsect3.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsect3.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="refsect3.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="refsect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="refsect3.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="refsect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="dedication.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::dedication[1]"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="dedicationinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="dedication.titlepage.recto.auto.mode" select="dedicationinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="dedication.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="dedication.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="dedication.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="dedication.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="dedication.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="dedication.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="dedication.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="dedication.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::preface[1]"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="prefaceinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/corpauthor"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/corpauthor"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/authorgroup"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/authorgroup"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/author"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/author"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/othercredit"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/othercredit"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/releaseinfo"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/releaseinfo"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/copyright"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/copyright"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/legalnotice"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/legalnotice"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/pubdate"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/pubdate"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/revision"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/revision"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/revhistory"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/revhistory"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="prefaceinfo/abstract"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="preface.titlepage.recto.auto.mode" select="docinfo/abstract"/>
    
</xsl:template>
      
      
      
      
      
      
      
      
      
      
      
      
      
    

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="preface.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="preface.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="preface.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="preface.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="preface.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="preface.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="chapterinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>


      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="chapterinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>


      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/corpauthor"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/corpauthor"/>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/authorgroup"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/authorgroup"/>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/author"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/author"/>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/othercredit"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/othercredit"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/releaseinfo"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/releaseinfo"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/copyright"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/copyright"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/legalnotice"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/legalnotice"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/pubdate"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/pubdate"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/revision"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/revision"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/revhistory"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/revhistory"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="chapterinfo/abstract"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="chapter.titlepage.recto.auto.mode" select="docinfo/abstract"/>
    
</xsl:template>
      

      

      

      

      

      
      
      
      
      
      
      
      
    

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="{$title.fontset}">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" margin-left="{$title.margin.left}">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="chapter.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="chapter.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="chapter.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style" font-size="24.8832pt" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::chapter[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style" space-before="0.5em" font-style="italic" font-size="14.4pt" font-weight="bold">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style" space-before="0.5em" space-after="0.5em" font-size="14.4pt">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style" space-before="0.5em" space-after="0.5em" font-size="14.4pt">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style" space-before="0.5em" space-after="0.5em" font-size="14.4pt">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="chapter.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="chapter.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="appendixinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="appendixinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/corpauthor"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/corpauthor"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/authorgroup"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/authorgroup"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/author"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/author"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/othercredit"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/othercredit"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/releaseinfo"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/releaseinfo"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/copyright"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/copyright"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/legalnotice"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/legalnotice"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/pubdate"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/pubdate"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/revision"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/revision"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/revhistory"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/revhistory"/>
      
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="appendixinfo/abstract"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="appendix.titlepage.recto.auto.mode" select="docinfo/abstract"/>
    
</xsl:template>
      
      
      
      
      
      
      
      
      
      
      
      
      
    

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="appendix.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="appendix.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="appendix.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::appendix[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="appendix.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="appendix.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="appendix.titlepage.recto.mode"/>
</fo:block>
</xsl:template>




  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sectioninfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sectioninfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="section.titlepage.recto.auto.mode" select="sectioninfo/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="section.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="section.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="section.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="section.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="section.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="section.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect1info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect1info/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect1.titlepage.recto.auto.mode" select="sect1info/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect1.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect1.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect1.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="sect1.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect1.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect1.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect2info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect2info/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect2.titlepage.recto.auto.mode" select="sect2info/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect2.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect2.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect2.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="sect2.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect2.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect2.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect3info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect3info/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect3.titlepage.recto.auto.mode" select="sect3info/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect3.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect3.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect3.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="sect3.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect3.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect3.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect4info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect4info/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect4.titlepage.recto.auto.mode" select="sect4info/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect4.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect4.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect4.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="sect4.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect4.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect4.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect5info/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="sect5info/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="sect5.titlepage.recto.auto.mode" select="sect5info/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="sect5.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect5.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="sect5.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="sect5.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="sect5.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="sect5.titlepage.recto.mode"/>
</fo:block>
</xsl:template>


  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.recto">
    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="simplesectinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="simplesectinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/corpauthor"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/corpauthor"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/authorgroup"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/authorgroup"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/author"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/author"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/othercredit"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/othercredit"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/releaseinfo"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/releaseinfo"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/copyright"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/copyright"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/legalnotice"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/legalnotice"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/pubdate"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/pubdate"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/revision"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/revision"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/revhistory"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/revhistory"/>
    
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="simplesectinfo/abstract"/>
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="simplesect.titlepage.recto.auto.mode" select="docinfo/abstract"/>
  
</xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="simplesect.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="simplesect.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="simplesect.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style" margin-left="{$title.margin.left}" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="corpauthor" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="authorgroup" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="author" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="othercredit" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="releaseinfo" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="copyright" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="legalnotice" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="pubdate" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revision" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="revhistory" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="abstract" mode="simplesect.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="simplesect.titlepage.recto.style">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="simplesect.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="bibliography.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::bibliography[1]"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="bibliographyinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliography.titlepage.recto.auto.mode" select="bibliographyinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliography.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliography.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliography.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="bibliography.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="bibliography.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="bibliography.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="bibliography.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="bibliography.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="bibliodivinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliodiv.titlepage.recto.auto.mode" select="bibliodivinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliodiv.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliodiv.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="bibliodivinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliodiv.titlepage.recto.auto.mode" select="bibliodivinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliodiv.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="bibliodiv.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="bibliodiv.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="bibliodiv.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="bibliodiv.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="bibliodiv.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="bibliodiv.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="20.736pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::bibliodiv[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="bibliodiv.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="bibliodiv.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="bibliodiv.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="glossary.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::glossary[1]"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="glossaryinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossary.titlepage.recto.auto.mode" select="glossaryinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossary.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossary.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossary.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="glossary.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="glossary.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="glossary.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="glossary.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="glossary.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.recto">
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="glossdivinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossdiv.titlepage.recto.auto.mode" select="glossdivinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossdiv.titlepage.recto.auto.mode" select="docinfo/title"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="title">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossdiv.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="glossdivinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossdiv.titlepage.recto.auto.mode" select="glossdivinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossdiv.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="glossdiv.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="glossdiv.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="glossdiv.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="glossdiv.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="title" mode="glossdiv.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="glossdiv.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="20.736pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::glossdiv[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="glossdiv.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="glossdiv.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="glossdiv.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="index.titlepage.recto.style" margin-left="0" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::index[1]"/>
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="pagewide" select="1"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="indexinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="index.titlepage.recto.auto.mode" select="indexinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="index.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="index.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="index.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="index.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="index.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="index.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="index.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="index.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
  
  

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="indexdiv.titlepage.recto.style">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="title" select="title"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="indexdivinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="indexdiv.titlepage.recto.auto.mode" select="indexdivinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="indexdiv.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="indexdiv.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="indexdiv.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="indexdiv.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="indexdiv.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="indexdiv.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="indexdiv.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="indexdiv.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="setindex.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::setindex[1]"/>
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="pagewide" select="1"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="setindexinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="setindex.titlepage.recto.auto.mode" select="setindexinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="setindex.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="setindex.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="setindex.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="setindex.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="setindex.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="setindex.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="setindex.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="setindex.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="colophon.titlepage.recto.style" margin-left="{$title.margin.left}" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="component.title">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="node" select="ancestor-or-self::colophon[1]"/>
</xsl:call-template></fo:block>
      
  <xsl:choose xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="colophoninfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="colophon.titlepage.recto.auto.mode" select="colophoninfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="docinfo/subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="colophon.titlepage.recto.auto.mode" select="docinfo/subtitle"/>
    </xsl:when>
    <xsl:when xmlns:xsl="http://www.w3.org/1999/XSL/Transform" test="subtitle">
      <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" mode="colophon.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

    
</xsl:template>
      
      
    

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.verso">
  
</xsl:template>
  

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.separator">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.before.recto">
  
</xsl:template>

  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.before.verso">
  
</xsl:template>


<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="colophon.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="colophon.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="colophon.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="subtitle" mode="colophon.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="colophon.titlepage.recto.style" font-family="{$title.fontset}">
<xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="colophon.titlepage.recto.mode"/>
</fo:block>
</xsl:template>



  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="table.of.contents.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'TableofContents'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="table.of.contents.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="table.of.contents.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="table.of.contents.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="list.of.tables.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'ListofTables'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.tables.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.tables.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.tables.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="list.of.figures.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'ListofFigures'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.figures.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.figures.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.figures.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="list.of.examples.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'ListofExamples'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.examples.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.examples.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.examples.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="list.of.equations.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'ListofEquations'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.equations.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.equations.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.equations.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="list.of.procedures.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'ListofProcedures'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.procedures.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.procedures.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.procedures.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

  
    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.recto">
      
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="list.of.unknowns.titlepage.recto.style" space-before.minimum="1em" space-before.optimum="1.5em" space-before.maximum="2em" space-after="0.5em" margin-left="{$title.margin.left}" font-size="17.28pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="gentext">
<xsl:with-param xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="key" select="'ListofUnknown'"/>
</xsl:call-template></fo:block>
    
</xsl:template>
      
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.verso">
    
</xsl:template>
    

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.separator">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.before.recto">
    
</xsl:template>

    

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.before.verso">
    
</xsl:template>
  

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.before.recto"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.recto"/>
    </fo:block>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.before.verso"/>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.verso"/>
    </fo:block>
    <xsl:call-template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" name="list.of.unknowns.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.unknowns.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template xmlns:xsl="http://www.w3.org/1999/XSL/Transform" match="*" mode="list.of.unknowns.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="." mode="titlepage.mode"/>
</xsl:template>





</xsl:stylesheet>
