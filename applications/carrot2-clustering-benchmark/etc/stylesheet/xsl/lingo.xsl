<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">

  <!--
    Styles for profiling information provided by Lingo
  -->
  
  <xsl:template match="token[1]">
    <tr>
      <th class="profile-key">Image</th>
      <th>TF</th>
      <th>IDF</th>
      <th>DF</th>
      <th></th>
    </tr>
    <tr>
      <td class="profile-key"><xsl:value-of select="image"/></td>
      <td class="profile-value"><xsl:value-of select="tf"/></td>
      <td class="profile-value"><xsl:value-of select="idf"/></td>
      <td class="profile-value"><xsl:value-of select="df"/></td>
      <td class="profile-value"><xsl:if test="@sw = 'true'">stop word</xsl:if></td>
    </tr>
  </xsl:template>

  <xsl:template match="token">
    <tr>
      <td class="profile-key"><xsl:value-of select="image"/></td>
      <td class="profile-value"><xsl:value-of select="tf"/></td>
      <td class="profile-value"><xsl:value-of select="idf"/></td>
      <td class="profile-value"><xsl:value-of select="df"/></td>
      <td class="profile-value"><xsl:if test="@sw = 'true'">stop word</xsl:if></td>
    </tr>
  </xsl:template>

  <xsl:template match="token-sequence[1]">
    <tr>
      <th class="profile-key">Image</th>
      <th>TF</th>
    </tr>
    <tr>
      <td class="profile-key"><xsl:value-of select="image"/></td>
      <td class="profile-value"><xsl:value-of select="tf"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="token-sequence">
    <tr>
      <td class="profile-key"><xsl:value-of select="image"/></td>
      <td class="profile-value"><xsl:value-of select="tf"/></td>
    </tr>
  </xsl:template>

  <!-- Matrix formatting -->
  <xsl:template match="matrix">
    <table>
      <tr>
        <td/>
        <xsl:apply-templates select="column-labels"/>
      </tr>
      <xsl:apply-templates select="body/row"/>
    </table>
  </xsl:template>

  <xsl:template match="column-labels/label">
    <td class="profile-key">
      <xsl:value-of select="."/>
    </td>
  </xsl:template>

  <xsl:template match="body/row">
    <tr>
      <td class="profile-key"><xsl:value-of select="label"/></td>
      <xsl:apply-templates select="values/v"/>
    </tr>
  </xsl:template>

  <xsl:template match="values/v">
    <td>
      <xsl:value-of select="."/>
    </td>
  </xsl:template>
</xsl:stylesheet>
