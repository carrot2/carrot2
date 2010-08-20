<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="no" omit-xml-declaration="yes" method="xml"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
              media-type="text/html" encoding="UTF-8" />
              
  <xsl:strip-space elements="*" />

  <xsl:key name="urls-by-root" match="document" use="substring-before(concat(url, '/'), '/')" />
  <xsl:variable name="unique-urls" select="count(/page/searchresult/document[generate-id(.) = generate-id(key('urls-by-root', substring-before(concat(substring-after(url, 'http://'), '/'), '/'))[1])])" />
  <xsl:variable name="document-count" select="count(/page/searchresult/document)" />

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
          <xsl:apply-templates select="." mode="rank" />
          <span class="title-in-clusters">
            <a href="{url}" class="title">
              <xsl:if test="$open-results-in-new-window = 'true'">
                <xsl:attribute name="target">_blank</xsl:attribute>
              </xsl:if>
              <xsl:choose>
                <xsl:when test="field[@key = 'title-highlight']">
                  <xsl:value-of disable-output-escaping="yes" select="field[@key = 'title-highlight']/value/@value" />
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
      <xsl:choose>
        <xsl:when test="field[@key = 'thumbnail-url']">
          <img class="thumbnail" src="{field[@key = 'thumbnail-url']/value/@value}" />
        </xsl:when>
        <xsl:when test="@id != 'document-template' and contains($document-source-ids-for-thumbnails, /page/request/@source) and $unique-urls >= ($document-count * $unique-urls-for-thumbnails)">
          <xsl:variable name="url-root" select="substring-before(concat(substring-after(url, 'http://'), '/'), '/')" />
          <img class="thumbnail" src="http://www.shrinktheweb.com/xino.php?embed=1&amp;u=3682f&amp;STWAccessKeyId=5c6a365af1001d3&amp;Size=sm&amp;Url={$url-root}" />
        </xsl:when>
      </xsl:choose>
      <xsl:if test="string-length(snippet) &gt; 0">
        <div class="snippet">
          <xsl:choose>
            <xsl:when test="field[@key = 'snippet-highlight']">
              <xsl:value-of disable-output-escaping="yes" select="field[@key = 'snippet-highlight']/value/@value" />
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
          <span class="sources"> [<xsl:apply-templates select="sources/source" mode="document-source" />]</span>
        </xsl:if>
      </div>
      <div style="clear: both"><xsl:comment></xsl:comment></div>
    </div>
  </xsl:template>
  
  <xsl:template match="document" mode="rank">
    <span class="rank"><xsl:value-of select="number(@id) + 1" /></span>
  </xsl:template>
  
  <xsl:template match="document/sources/source[position() = last()]" mode="document-source">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="document/sources/source[position() != last()]" mode="document-source">
    <xsl:apply-templates />,
  </xsl:template>
</xsl:stylesheet>
