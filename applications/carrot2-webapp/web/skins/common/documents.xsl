<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="utf-8" />
              
  <xsl:strip-space elements="*" />
  
  <!-- Documents -->
  <xsl:template match="page[@type = 'DOCUMENTS']">
    <div id="documents">
      <xsl:choose>
        <xsl:when test="count(searchresult/document) > 0">
          <xsl:apply-templates select="searchresult/document" />
        
          <script>
            var fetchedDocumentsCount = <xsl:value-of select="count(searchresult/document)" />;
            var totalDocumentsCount = "<xsl:value-of select="searchresult/attribute[@key = 'results-total']/value/@value" />";
            var sourceTime = "<xsl:value-of select="searchresult/attribute[@key = 'processing-time-source']/value/@value" />";
          </script>
        </xsl:when>
        
        <xsl:otherwise>
          <div id="no-documents">Your query returned no documents. <br />Please try a more general query.</div>
        </xsl:otherwise>
      </xsl:choose>    
    </div>
  </xsl:template>

  <xsl:template match="document">
    <div id="d{@id}" class="document">
      <div class="title">
        <h3>
          <span class="rank"><xsl:value-of select="number(@id) + 1" /></span>
          <span class="title-in-clusters">
            <a href="{url}" class="title">
              <xsl:choose>
                <xsl:when test="field[@key = 'title-highlight']">
                  <xsl:value-of disable-output-escaping="yes" select="field[@key = 'title-highlight']/value" />
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="string(title)" />
                </xsl:otherwise>
              </xsl:choose>
            </a>
            <a href="#" class="in-clusters" title="Show in clusters">&#160;<small>Show in clusters</small></a>
          </span>
          <a href="{url}" target="_blank" class="in-new-window" title="Open in new window">&#160;<small>Open in new window</small></a>
          <a href="#" class="show-preview" title="Show preview">&#160;<small>Show preview</small></a>
        </h3>
      </div>
      <xsl:if test="field[@key = 'thumbnail-url']">
        <img class="thumbnail" src="{field[@key = 'thumbnail-url']/value}" />
      </xsl:if>
      <xsl:if test="string-length(snippet) &gt; 0">
        <div class="snippet">
          <xsl:choose>
            <xsl:when test="field[@key = 'snippet-highlight']">
              <xsl:value-of disable-output-escaping="yes" select="field[@key = 'snippet-highlight']/value" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="string(snippet)" />
            </xsl:otherwise>
          </xsl:choose>
        </div>
      </xsl:if>
      <div class="url">
        <xsl:apply-templates select="url" />
        <xsl:if test="count(sources/source) > 0">
          <span class="sources"> [<xsl:apply-templates select="sources/source" />]</span>
        </xsl:if>
      </div>
      <div style="clear: both"><xsl:comment></xsl:comment></div>
    </div>
  </xsl:template>
  
  <xsl:template match="document/sources/source[position() = last()]">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="document/sources/source[position() != last()]">
    <xsl:apply-templates />,
  </xsl:template>
</xsl:stylesheet>
