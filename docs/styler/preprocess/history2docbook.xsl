<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="history">
    <revhistory>
    <xsl:apply-templates select="changelist">
        <xsl:sort order="descending" select="date" />
    </xsl:apply-templates>
    </revhistory>
</xsl:template>


<xsl:template match="changelist">
    <revision>
        <revnumber><xsl:value-of select="concat(date,'/',last() - position())" /></revnumber>
        <date><xsl:value-of select="date"/></date>
        <authorinitials><xsl:value-of select="committer"/></authorinitials>
        <revdescription>
            <itemizedlist>
            <xsl:for-each select="change">
                <xsl:sort select="@component" />
                <xsl:sort select="@type" />
                
                <listitem>
                <para>
                <emphasis role="bold">[<xsl:value-of select="@type"/>]</emphasis>
                Component: <emphasis role="bold"><xsl:value-of select="@component"/></emphasis>
                <xsl:if test="@scheduled">, pending since: <xsl:value-of select="@scheduled"/></xsl:if>
                </para>
                <para>
                <xsl:apply-templates />
                </para>
                </listitem>
                
            </xsl:for-each>
            </itemizedlist>
        </revdescription>
    </revision>
</xsl:template>

<xsl:template match="text()">
<xsl:value-of select="normalize-space(.)" />
</xsl:template>

</xsl:stylesheet>
