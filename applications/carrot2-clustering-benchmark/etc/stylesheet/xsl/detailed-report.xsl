<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">
   
  <!--
    TODO:
      (04/06/30) support for multiple cluster labels
  -->

  <xsl:import href="lingo.xsl"/>
  <xsl:import href="odp.xsl"/>

  <xsl:output indent="no"/>

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

        <xsl:apply-templates select="profiles"/>
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
        <span class="cluster-label-text"><xsl:apply-templates select="labels"/></span>
        <xsl:text> </xsl:text>
        <span class="cluster-label-size">(<xsl:value-of select="size"/>)</span>
        <xsl:if test="properties/property[@key = 'contamination']">
          <xsl:text> </xsl:text>
          <span class="cluster-label-extras">[<xsl:value-of select="properties/property[@key = 'contamination']"/>]</span>
        </xsl:if>
        <xsl:if test="properties/property[@key = 'cluscatid']">
          <xsl:text> </xsl:text>
          <span class="cluster-label-extras">[<xsl:value-of select="properties/property[@key = 'cluscatid']"/>]</span>
        </xsl:if>
      </a>
    </div>
    <div class="content">
      <div class="indent">
        <xsl:apply-templates select="raw-clusters/raw-cluster"/>
        <xsl:apply-templates select="raw-documents/raw-document"/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="raw-cluster/labels">
    <xsl:if test="label"><xsl:value-of select="label[1]"/><xsl:for-each select="label[position() > 1]">,<xsl:text> </xsl:text><xsl:value-of select="."/></xsl:for-each></xsl:if>
  </xsl:template>

<!--   <xsl:template match="labels/label[1]"><xsl:value-of select="."/></xsl:template> -->
<!--   <xsl:template match="labels/label[position() > 1]">,<xsl:text> </xsl:text><xsl:value-of select="."/></xsl:template> -->

  <!-- A single raw document -->
  <xsl:template match="raw-document">
    <div class="raw-document">
      <span class="document-title"><xsl:value-of select="title"/></span>
      <xsl:text> </xsl:text>
      <span class="document-snippet"><xsl:value-of select="snippet"/></span>
      <xsl:if test="lang">
        <xsl:text> </xsl:text>
        <span class="document-extras">[<xsl:value-of select="lang"/>]</span>
      </xsl:if>
      <xsl:if test="member-score">
        <xsl:text> </xsl:text>
        <span class="document-extras">[<xsl:value-of select="member-score"/>]</span>
      </xsl:if>
      <xsl:if test="catid">
        <xsl:text> </xsl:text>
        <span class="document-extras">[<xsl:value-of select="catid"/>]</span>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- List of profiles -->
  <xsl:template match="profiles">
    <div class="all-profiles">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <!-- A single profile -->
  <xsl:template match="profile">
    <xsl:if test="profile-entry">
      <div class="label">
        <a href="#">
          <img src="img/tree.png"/>
          <span class="profile-label"><xsl:value-of select="@component"/></span>
        </a>
      </div>
      <div class="content">
        <div class="profile">
          <xsl:apply-templates/>
        </div>
      </div>
    </xsl:if>
  </xsl:template>

  <!-- A single profile entry -->
  <xsl:template match="profile-entry">
    <div class="label">
      <a href="#">
        <img src="img/tree.png"/>
        <span class="profile-label"><xsl:value-of select="name"/></span>
      </a>
    </div>
    <div class="content">
      <div class="profile">
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>

  <!-- Generic data container -->
  <xsl:template match="profile-entry/data">
    <table class="profile-data">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <!-- Generic data entry -->
  <xsl:template match="data/entry">
    <tr>
      <xsl:if test="@key">
        <td class="profile-key"><xsl:value-of select="@key"/></td>
      </xsl:if>
      <td class="profile-value"><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <!-- Suppress the rest -->
  <xsl:template match="name"/>
  <xsl:template match="description"/>
</xsl:stylesheet>
