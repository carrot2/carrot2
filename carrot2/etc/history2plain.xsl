<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="history">
History file of the Carrot2 project:
<xsl:apply-templates select="changelist">
    <xsl:sort order="descending" select="date" />
</xsl:apply-templates>
</xsl:template>


<xsl:template match="changelist">

<xsl:if test="preceding-sibling::*[position() = count(.) and name() = 'releasetag']">

============================================================================
RELEASE: <xsl:value-of select="preceding-sibling::*[position() = count(.) and name() = 'releasetag']/@tag" /> (CVS tag: <xsl:value-of select="preceding-sibling::*[position() = count(.) and name() = 'releasetag']/@cvstag" />)
--------
<xsl:apply-templates select="preceding-sibling::*[position() = count(.) and name() = 'releasetag']" />
============================================================================
</xsl:if>

----------------------------------------------------------------------------
<xsl:value-of select="date"/>, committer: <xsl:value-of select="committer"/>
----------------------------------------------------------------------------
<xsl:apply-templates select="change">
    <xsl:sort select="@component" />
    <xsl:sort select="@type" />
</xsl:apply-templates>
</xsl:template>

<xsl:template match="change">
[<xsl:value-of select="@type"/>], component: <xsl:value-of select="@component"/><xsl:if test="@scheduled">, pending since: <xsl:value-of select="@scheduled"/></xsl:if>
<xsl:value-of select="'&#10;'" />
<xsl:apply-templates />
<xsl:value-of select="'&#10;'" />
</xsl:template>

<xsl:template match="text()">
<xsl:value-of select="normalize-space(.)" />
</xsl:template>

</xsl:stylesheet>
