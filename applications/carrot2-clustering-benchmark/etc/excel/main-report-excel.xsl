<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="urn:schemas-microsoft-com:office:spreadsheet"
                xmlns:o="urn:schemas-microsoft-com:office:office"
                xmlns:x="urn:schemas-microsoft-com:office:excel"
                xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
                xmlns:html="http://www.w3.org/TR/REC-html40"
                version="1.1">
   
  <!--
    Transforms benchmarking results into a simple Excel XML workbook.
  
    TODO:
  -->
  <xsl:variable name="string-fields">Process,Query,Test,Balanced Size</xsl:variable>
  
  <xsl:output indent="yes"/>
 
  <!-- Root element -->
  <xsl:template match="report">
    <Workbook>
      <xsl:apply-templates select="results"/>
    </Workbook>
  </xsl:template>

  <!-- Result -->
  <xsl:template match="result">
    <Row>
      <xsl:apply-templates select="entry"/>
    </Row>
  </xsl:template>

  <!-- String result entry -->
  <xsl:template match="result/entry[contains('Process,Query,Test,Balanced Size', @key)]">
    <Cell>
      <Data ss:Type="String"><xsl:value-of select="."/></Data>
    </Cell>
  </xsl:template>

  <!-- Numeric result entry -->
  <xsl:template match="result/entry[not(contains('Process,Query,Test,Balanced Size', @key))]">
    <Cell>
      <xsl:variable name="text"><xsl:value-of select="."/></xsl:variable>
      <xsl:if test="not($text = '')">
        <Data ss:Type="Number"><xsl:value-of select="translate($text, ', ms', '.')"/></Data>
      </xsl:if>
    </Cell>
  </xsl:template>

  <!-- Clustering results container -->
  <xsl:template match="report/results">
    <Worksheet ss:Name="Raw results">
      <Table>
        <Row>
          <xsl:for-each select="result[1]/entry">
            <Cell><Data ss:Type="String"><xsl:value-of select="@key"/></Data></Cell>
          </xsl:for-each>
        </Row>
        <xsl:apply-templates/>
      </Table>
      <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
        <Selected/>
      </WorksheetOptions>
    </Worksheet>
  </xsl:template>

  <!-- Suppress the rest -->
  <xsl:template match="*"/>
</xsl:stylesheet>
