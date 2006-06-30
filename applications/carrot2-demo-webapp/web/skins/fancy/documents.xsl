<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:import href="common.xsl" />
  <xsl:strip-space elements="*"/>

  <xsl:output indent="yes" omit-xml-declaration="yes" media-type="text/html" encoding="utf-8"
       doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <xsl:template name="head-insert">
    <link rel="stylesheet" href="{$skinuri}/css/documents.css" />
  </xsl:template>

  <xsl:template match="searchresult[@type='documents']">
    <div id="documents">
      <xsl:apply-templates select="document" />
    </div>
  </xsl:template>

  <xsl:template match="document">
    <table class="d" id="{@id}">
      <tr>
        <td class="r">
          <xsl:value-of select="position()" />
        </td>
        <td class="c">
          <div class="t">
            <a>
              <xsl:attribute name="href"><xsl:value-of select="url" /></xsl:attribute>
              <xsl:apply-templates select="title" />
            </a>
          </div>

          <div class="s">
            <xsl:apply-templates select="snippet" />
          </div>

          <div class="u">
            <xsl:value-of select="url" />
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
