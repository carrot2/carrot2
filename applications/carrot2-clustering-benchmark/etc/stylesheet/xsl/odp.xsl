<?xml version="1.0" encoding="Windows-1250"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.1">

  <!--
    Styles for profiling information provided by ODP input
  -->
 
  <xsl:template match="odp-topics">
    <table class="profile-data">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="odp-topic[1]">
    <tr>
      <th class="profile-key">CatId</th>
      <th>ODP Path</th>
      <th>Document count</th>
    </tr>
    <tr>
      <td class="profile-key"><xsl:value-of select="catid"/></td>
      <td class="profile-value"><xsl:value-of select="id"/></td>
      <td class="profile-value"><xsl:value-of select="size"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="odp-topic">
    <tr>
      <td class="profile-key"><xsl:value-of select="catid"/></td>
      <td class="profile-value"><xsl:value-of select="id"/></td>
      <td class="profile-value"><xsl:value-of select="size"/></td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
