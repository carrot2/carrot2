<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="UTF-8" />
              
  <xsl:strip-space elements="*" />
  
  <!-- Clusters -->
  <xsl:template match="page[@type = 'CLUSTERS']">
    <div id="clusters">
      <xsl:choose>
        <xsl:when test="count(searchresult/group) > 0">
          <a id="tree-top" href="#"><span class="label">All Topics</span><span class="size">(<xsl:value-of select="count(searchresult/document)" />)</span></a>
          <ul>
            <xsl:apply-templates select="searchresult/group" />
          </ul>
      
          <script>
            $.clusters.setDocuments({
              <xsl:apply-templates select="searchresult/group" mode="json" />
            });
            var documentCount = <xsl:value-of select="count(searchresult/document)" />;
            var algorithmTime = "<xsl:value-of select="searchresult/attribute[@key = 'processing-time-algorithm']/value/@value" />";
          </script>
        </xsl:when>
        
        <xsl:otherwise>
          <div id="no-clusters">No clusters found</div>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="group">
    <li id="{generate-id(.)}">
      <xsl:choose>
        <xsl:when test="attribute[@key = 'other-topics']">
          <xsl:attribute name="class">folded other</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">folded</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <a href="#"><span class="label"><xsl:apply-templates select="title" /></span><span class="size" dir="ltr">(<xsl:value-of select="@size" />)</span></a>
      <xsl:if test="group">
        <ul>
          <xsl:apply-templates select="group" />
        </ul>
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template match="group/title">
    <xsl:choose>
      <xsl:when test="../attribute[@key = 'other-topics']/value[@value = 'true']">Other topics</xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="phrase[1]" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="group" mode="json">
    '<xsl:value-of select="generate-id(.)" />': {
      <xsl:if test="document">
        d: [<xsl:apply-templates mode="json" select="document" />]<xsl:if test="group">,</xsl:if>
      </xsl:if>
      <xsl:if test="group">
        c: { 
          <xsl:apply-templates select="group" mode="json" /> 
        }
      </xsl:if>
    }<xsl:if test="not(position() = last())">,</xsl:if>
  </xsl:template>

  <xsl:template match="group/document" mode="json">
    <xsl:value-of select="@refid" /><xsl:if test="not(position() = last())">,</xsl:if>
  </xsl:template>
</xsl:stylesheet>
