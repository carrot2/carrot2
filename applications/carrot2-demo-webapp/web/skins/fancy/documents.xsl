<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />
  <xsl:strip-space elements="*"/>

  <xsl:output indent="no" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8"
       doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <xsl:template name="head-insert">
    <link rel="stylesheet" href="{$skinuri}/css/common.css" />
    <link rel="stylesheet" href="{$skinuri}/css/documents.css" />
    <script type="text/javascript" src="{$skinuri}/js/yui/yahoo-dom-event.js" ><xsl:comment>x</xsl:comment></script>
    <script type="text/javascript" src="{$skinuri}/js/Documents.js" ><xsl:comment>x</xsl:comment></script>
    <script type="text/javascript" src="{$skinuri}/js/Utils.js"><xsl:comment>x</xsl:comment></script>
<script type="text/javascript">
  YAHOO.util.Event.addListener(window, "load", init);
</script>    
  </xsl:template>
    
  <xsl:template name="on-load">
    <xsl:text>javascript:parent.afterDocsLoaded();</xsl:text>
  </xsl:template>

  <xsl:template match="searchresult[@type='documents']">
    <div id="documents">
      <xsl:apply-templates />

      <xsl:if test="count(document) = 0">
          <div id="no-documents">Your query returned no documents.<br/>Please try a more general query.</div>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="exception">
      <div style="margin-top: 5px; border: 1px dotted red; border-left: 5px solid red; padding: 4px; margin-left: 2px;">
          <div style="font-size: 9px; color: gray; background-color: #ffe0e0;"><xsl:apply-templates select="class" /></div>
          <pre style="font-size: 11px; color: black; font-weight: bold;">
              <xsl:apply-templates select="message" />
          </pre>
      </div>

      <xsl:if test="cause">
          <xsl:apply-templates select="cause/exception" />
      </xsl:if>
  </xsl:template>

  <xsl:template match="document">
    <table class="d" id="{@id}">
      <tr>
        <td class="r">
          <xsl:value-of select="position()" /><br/>
          <img id="sic{@id}" src="{$skinuri}/img/sic.gif" class="sic" />
        </td>
        <td class="c">
          <div class="t">
            <a target="_top">
              <xsl:attribute name="href"><xsl:value-of select="url" /></xsl:attribute>
              <xsl:apply-templates select="title" />
            </a>
            <span id="pre{@id}" class="prev" title="Show preview"><img alt="Preview" src="{$skinuri}/img/preview.gif" class="onw"/></span>              
          </div>

          <div class="s">
            <xsl:apply-templates select="snippet" />
          </div>
          
          <div class="p">
            <iframe class="preview" id="pr{@id}" style="display: none" frameborder="no" 
                    url="{url}" />
          </div>

          <div class="u">
            <xsl:value-of select="url" />
            <div class="o">[<xsl:value-of select="sources" />]</div>
          </div>
        </td>
      </tr>
    </table>
  </xsl:template>

  <!-- Skip documents -->
  <xsl:template match="group" />

  <!-- Skip query -->
  <xsl:template match="query" />
</xsl:stylesheet>
