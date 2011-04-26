<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes" omit-xml-declaration="yes"
       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
       media-type="text/html" encoding="UTF-8" />
 
  <xsl:param name="baseline" />
  <xsl:param name="current" />

  <xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Carrot2 Java API backwards compatibility report: version <xsl:value-of select="$current" /> vs. <xsl:value-of select="$baseline" /></title>
    <style>
      body {
        font-family: Arial, sans-serif;
        font-size: 12px;
      }

      h1 small {
        font-size: 100%;
        color: #888;
      }

      li {
        margin-bottom: 0.8ex;
        color: #666;
      }

      li <xsl:value-of select="'>'" disable-output-escaping="yes" /> ul <xsl:value-of select="'>'" disable-output-escaping="yes" /> li {
        margin-bottom: 0;
      }

      tt {
        font-family: "Consolas","Bitstream Vera Sans Mono","Courier New",Courier,monospace;
        color: #000;
      }
    </style>
  </head>

  <body>
    <h1>Carrot&#178; Java API backwards compatibility report: <small>version <xsl:value-of select="$current" /> vs. <xsl:value-of select="$baseline" /></small></h1>

    <p>
      Please note that a number of public but non-core packages have been excluded from this report for clarity. 
      Incompatible changes can mean source-incompatiblity, binary-incompatibility or both.
    </p>

    <xsl:apply-templates>
      <xsl:with-param name="level">ERROR</xsl:with-param>
      <xsl:with-param name="title">Incompatible changes</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates>
      <xsl:with-param name="level">WARNING</xsl:with-param>
      <xsl:with-param name="title">Warnings</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates>
      <xsl:with-param name="level">INFO</xsl:with-param>
      <xsl:with-param name="title">Compatible changes</xsl:with-param>
    </xsl:apply-templates>
  </body>
</html>
  </xsl:template>

  <xsl:key name="difference-by-class" match="difference" use="@class" />
  <xsl:template match="/diffreport">
    <xsl:param name="level" />
    <xsl:param name="title" />

    <xsl:if test="difference[@srcseverity = $level]">
      <h2><xsl:value-of select="$title" /></h2>
      <ul>
      <xsl:for-each select="difference[(@srcseverity = $level or @binseverity = $level) and count(. | key('difference-by-class', @class)[1]) = 1]">
        <xsl:sort select="@class" />
        <xsl:variable name="class" select="@class" />
        <li>
          <xsl:choose>
            <xsl:when test="count(/diffreport/difference[(@srcseverity = $level or @binseverity = $level) and @class = $class]) &gt; 1">
              <tt><xsl:value-of select="@class" /></tt>
              <ul>
                <xsl:apply-templates select="/diffreport/difference[(@srcseverity = $level or @binseverity = $level) and @class = $class]" mode="multiple" />
              </ul>
            </xsl:when>

            <xsl:otherwise>
              <xsl:apply-templates select="/diffreport/difference[(@srcseverity = $level or @binseverity = $level) and @class = $class]" mode="single" />
            </xsl:otherwise>
          </xsl:choose>
        </li>
      </xsl:for-each>
      </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="difference" mode="single"> 
    <tt><xsl:value-of select="@class" /></tt>: <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="difference" mode="multiple"> 
    <li>
      <xsl:apply-templates />
    </li>
  </xsl:template>
</xsl:stylesheet>
