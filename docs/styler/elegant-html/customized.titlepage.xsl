<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>


<xsl:include href="generated.titlepage.xsl" />


<xsl:template match="revhistory">
  <div class="{name(.)}">
      <table border="0" cellspacing="1" cellpadding="0" class="revhistory">
        <tr>
            <td align="center" valign="top" width="1%" style="padding-left: 0.5em; padding-right: 0.5em; background: black; color: white;">Number</td>
            <td align="center" valign="top" width="1%" style="padding-left: 0.5em; padding-right: 0.5em; background: black; color: white;">Date</td>
            <td align="center" valign="top" width="1%" style="padding-left: 0.5em; padding-right: 0.5em; background: black; color: white;">Author</td>
            <td align="center" valign="top" style="background: black; color: white;">Changes</td>
        </tr>

      <xsl:apply-templates/>
      </table>
  </div>
</xsl:template>

<xsl:template match="revhistory/revision">
  <xsl:variable name="revnumber" select=".//revnumber"/>
  <xsl:variable name="revdate"   select=".//date"/>
  <xsl:variable name="revauthor" select=".//authorinitials"/>
  <xsl:variable name="revremark" select=".//revremark|.//revdescription"/>

  <tr>  
  <td align="center" valign="top" class="revnumber"><nobr><xsl:apply-templates select="$revnumber"/></nobr></td>
  <td align="center" valign="top" class="revdate"><nobr><xsl:apply-templates select="$revdate"/></nobr></td>
  <td align="center" valign="top" class="revauthor"><nobr><xsl:apply-templates select="$revauthor"/></nobr></td>
  <td align="left"   valign="top" class="revremark"><xsl:apply-templates select="$revremark"/></td>
  </tr>
</xsl:template>


</xsl:stylesheet>


