<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">
   
  <!--
    TODO:
      (04/06/30) support for multiple cluster labels
  -->
   
  <!-- Root element -->
  <xsl:template match="report">
    <html>
      <head>
        <meta name="author" content="Stanislaw Osinski"/>
        <link rel="stylesheet" href="css/report.css"/>
        <title>Clustering Report</title>
        <script language="javascript" src="js/folding.js"/>
      </head>

      <body>
        <table>
          <tr>
            <td class="info-container">
              <xsl:apply-templates select="info"/>
            </td>
            <td class="clusters-container">
              <xsl:apply-templates select="raw-clusters"/>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <!-- General info container -->
  <xsl:template match="info">
    <table>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <!-- General info entry -->
  <xsl:template match="info/entry">
    <tr>
      <td class="key"><xsl:value-of select="@key"/></td>
      <td class="value"><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <!-- Clustering results container -->
  <xsl:template match="report/raw-clusters">
    <div class="all-clusters">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <!-- A single cluster -->
  <xsl:template match="raw-cluster">
    <div class="label">
      <a href="#">
        <img src="img/tree.png"/>
        <span class="cluster-label-text"><xsl:value-of select="labels/label[1]"/></span>
        <xsl:text> </xsl:text>
        <span class="cluster-label-size">(<xsl:value-of select="size"/>)</span>
      </a>
    </div>
    <div class="content">
      <div class="indent">
        <xsl:apply-templates select="raw-clusters/raw-cluster"/>
        <xsl:apply-templates select="raw-documents/raw-document"/>
      </div>
    </div>
  </xsl:template>

  <!-- A single raw document -->
  <xsl:template match="raw-document">
    <div class="raw-document">
      <span class="document-title"><xsl:value-of select="title"/></span>
      <xsl:text> </xsl:text>
      <span class="document-snippet"><xsl:value-of select="snippet"/></span>
      <xsl:if test="lang">
        <xsl:text> </xsl:text>
        <span class="document-extra">[<xsl:value-of select="lang"/>]</span>
      </xsl:if>
      <xsl:if test="member-score">
        <xsl:text> </xsl:text>
        <span class="document-extra">[<xsl:value-of select="member-score"/>]</span>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- Suppress the rest -->
  <xsl:template match="*"/>
</xsl:stylesheet>
