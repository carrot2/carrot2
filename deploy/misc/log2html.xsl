<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<!--

  @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
  
-->
<xsl:decimal-format decimal-separator="." grouping-separator="," />

<xsl:template match="/">
<html>
  <head>
    <style type="text/css">
    .bannercell {
      border: 0px;
      padding: 0px;
    }
    body {
      margin: 1em;
      font:normal 100% arial,helvetica,sanserif;
      background-color:#FFFFFF;
      color:#000000;
    }
    table.status {
      font:bold 80% arial,helvetica,sanserif;
      background-color:#525D76;
      color:#ffffff;
    }
    table.log tr td, tr th {
      font-size: 80%;
    }
    .error {
      color:red;
    }
    .warn {
      color:brown;
    }
    .info {
      color:gray;
    }
    .debug{
      color:gray;
    }
    .failed {
      font-size:80%;
      background-color: red;
      color:#FFFFFF;
      font-weight: bold
    }
    .complete {
      font-size:80%;
      background-color: #525D76;
      color:#FFFFFF;
      font-weight: bold
    }
    .a td { 
      background: #efefef;
    }
    .b td { 
      background: #fff;
    }
    th, td {
      text-align: left;
      vertical-align: top;
    }
    th {
      background: #ccc;
      color: black;
    }
    table, th, td {
      border: none
    }
    h3 {
      font:bold 80% arial,helvetica,sanserif;
      background: #525D76;
      color: white;
      text-decoration: none;
      padding: 5px;
      margin-right: 2px;
      margin-left: 2px;
      margin-bottom: 0;
    }
    </style>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <!-- jakarta logo -->
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td valign="top" class="bannercell">
      <img src="../gfx/Carrot2-build-log.gif" />
      </td>
      <td style="text-align: right; vertical-align: bottom; font-size: 11px;">
      ANT logs from the last build<br/>
      Visit the Carrot<sup>2</sup> project at <a href="http://www.cs.put.poznan.pl/dweiss/carrot">http://www.cs.put.poznan.pl/dweiss/carrot</a>
      </td>
    </tr>
    </table>

    <table border="0" width="100%">
    <tr><td><hr noshade="yes" size="1"/></td></tr>
    </table>

    <xsl:apply-templates select="build"/>

    <!-- FOOTER -->
    <table width="100%">
      <tr><td><hr noshade="yes" size="1"/></td></tr>
      <tr><td>
      <div align="center"><font color="#525D76" size="-1"><em>

      </em></font></div>
      </td></tr>
    </table>
  </body>
</html>
</xsl:template>

<xsl:template match="build">
  <!-- build status -->
  <table width="100%">
    <xsl:attribute name="class">
      <xsl:if test="@error">failed</xsl:if>
      <xsl:if test="not(@error)">complete</xsl:if>
    </xsl:attribute>
    <tr>
      <td nowrap="yes">
      <xsl:if test="@error">
        Build Failed
      </xsl:if>
      <xsl:if test="not(@error)">
        Build Complete
      </xsl:if>
      <xsl:if test="@time">
          <xsl:value-of select="concat(' [', @time, ']')" />
      </xsl:if>
      </td>
      <td style="text-align:right" nowrap="yes"></td>
    </tr>
    <tr>
      <td colspan="2">
        <xsl:if test="@error">
          <tt><xsl:value-of select="@error"/></tt><br/>
          <i style="font-size:80%">See the <a href="#stacktrace" alt="Click for details">stacktrace</a>.</i>
        </xsl:if>
      </td>
    </tr>
  </table>
  <table border="1" cellspacing="2" cellpadding="3" width="100%" style="font-size:80%">
    <tr class="a"><td width="1">ant.file</td><td><xsl:value-of select="substring-after(message[contains(text(),'ant.file')], '->')"/></td></tr>
    <tr class="b"><td width="1">ant.version</td><td><xsl:value-of select="substring-after(message[contains(text(),'ant.version')], '->')"/></td></tr>
    <tr class="a"><td width="1">java.version</td><td><xsl:value-of select="substring-after(message[contains(text(),'java.vm.version')], '->')"/></td></tr>
    <tr class="b"><td width="1">os.name</td><td><xsl:value-of select="substring-after(message[contains(text(),'os.name')], '->')"/></td></tr>
  </table>
  <!-- build information -->
  <h3>Build events</h3>
  <table class="log" border="1" cellspacing="2" cellpadding="3" width="100%">
  <tr>
    <th nowrap="yes" align="left" width="1%">target</th>
    <th nowrap="yes" align="left" width="1%">task</th>
    <th nowrap="yes" align="left">message</th>
  </tr>
  <xsl:apply-templates select=".//message[@priority != 'debug']"/>
  </table>
  <p>
  <!-- stacktrace -->
  <xsl:if test="stacktrace">
  <a name="stacktrace"/>
  <h3>Error details</h3>
  <table width="100%">
    <tr><td>
      <pre><xsl:value-of select="stacktrace"/></pre>
    </td></tr>
  </table>
  </xsl:if>
  </p>
</xsl:template>

<!-- report every message but those with debug priority -->
<xsl:template match="message[@priority!='debug']">
  <tr valign="top">
    <!-- alternated row style -->
    <xsl:attribute name="class">
      <xsl:if test="position() mod 2 = 1">a</xsl:if>
      <xsl:if test="position() mod 2 = 0">b</xsl:if>
    </xsl:attribute>
    <td nowrap="yes" width="1%">
        <xsl:value-of select="../../@name"/>
    </td>
    <td nowrap="yes" style="text-align:right" width="1%">[ <xsl:value-of select="../@name"/> ]</td>
    <td class="{@priority}" nowrap="yes">
            <xsl:value-of select="text()"/>
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
