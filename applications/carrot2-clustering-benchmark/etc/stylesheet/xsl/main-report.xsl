<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">
   
  <!--
    TODO:
      (04/07/01) grouping by process or query?
  -->

  <xsl:variable name="include-fields">Query,Documents,Filter Time,Coverage,Avg Cont</xsl:variable>

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
              <xsl:apply-templates select="results"/>
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

  <!-- General info entry -->
  <xsl:template match="result/entry">
    <td class="result-value"><xsl:value-of select="."/></td>
  </xsl:template>

  <!-- Clustering results container -->
  <xsl:template match="report/results">
    <div class="all-results">
      <xsl:for-each select="/report/processes/process">
        <xsl:variable name="process-id"><xsl:value-of select="."/></xsl:variable>

        <div class="label">
          <a href="#">
            <img src="img/tree.png"/>
            <span class="process-name"><xsl:value-of select="$process-id"/>
          </span></a>
        </div>
        <div class="content-nc">
          <div class="result">
            <table class="result">
              <tr>
                <xsl:for-each select="/report/results/result[1]/entry[contains($include-fields, @key)]">
                  <th><xsl:value-of select="@key"/></th>
                </xsl:for-each>
                <th/> <!-- Details -->
              </tr>
              <xsl:for-each select="/report/results/result">
                <xsl:if test="entry[@key = 'Process'] = $process-id">
                  <tr>
                    <xsl:apply-templates select="entry[contains($include-fields, @key)]"/>
                    <td class="result-value">
                      <a>
                        <xsl:attribute name="href"><xsl:value-of select="entry[@key = 'Process']"/>-<xsl:value-of select="entry[@key = 'Query']"/>.html</xsl:attribute>
                        details &#187;&#187;
                      </a>
                    </td>
                  </tr>
                </xsl:if>
              </xsl:for-each>
            </table>
          </div>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>

  <!-- Suppress the rest -->
  <xsl:template match="*"/>
</xsl:stylesheet>
