<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">
   
  <!-- Overrides default chapter title -->
  <xsl:template name="chapter.titlepage">
    <fo:block margin-left="{$body.margin.inner}" margin-bottom="{$chapter.title.margin-bottom}">
      <fo:table border-width="0.1mm"> <!-- border-width must be here -->
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell background-color="black" height="{$chapter.title.number-block.height}" 
                           border-after-color="white" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block line-height="100%" margin-right="1mm" 
                        margin-bottom="3mm" font-family="SansSerif-Bold" 
                        font-size="78pt" color="white" text-align="right">
                <xsl:call-template name="substitute-markup">
                  <xsl:with-param name="allow-anchors" select="'1'"/>
                  <xsl:with-param name="template" select="'%n'"/>
                </xsl:call-template>          
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" 
                           display-align="after">
              <fo:block margin-left="{$marginbar.margin}" margin-bottom="1mm"
                        wrap-option="{$chapter.title.wrap-option}"
                        font-size="{$chapter.title.font-size}" color="black" line-height="100%" 
                        text-align="left" vertical-align="bottom">
                  <xsl:call-template name="substitute-markup">
                    <xsl:with-param name="allow-anchors" select="'1'"/>
                    <xsl:with-param name="template" select="'%t'"/>
                  </xsl:call-template>          
                </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <!-- Overrides default appendix title -->
  <xsl:template name="appendix.titlepage">
    <fo:block margin-left="{$body.margin.inner}" margin-bottom="{$chapter.title.margin-bottom}">
      <fo:table border-width="0.1mm"> <!-- border-width must be here -->
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell background-color="black" height="{$chapter.title.number-block.height}" 
                           border-after-color="white" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block line-height="100%" margin-right="1mm" 
                        margin-bottom="3mm" font-family="SansSerif-Bold" 
                        font-size="78pt" color="white" text-align="right">
                <xsl:call-template name="substitute-markup">
                  <xsl:with-param name="allow-anchors" select="'1'"/>
                  <xsl:with-param name="template" select="'%n'"/>
                </xsl:call-template>          
              </fo:block>
            </fo:table-cell>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" 
                           display-align="after">
              <fo:block margin-left="{$marginbar.margin}" margin-bottom="1mm"
                        wrap-option="{$chapter.title.wrap-option}"
                        font-size="{$chapter.title.font-size}" color="black" line-height="100%" 
                        text-align="left" vertical-align="bottom">
                  <xsl:call-template name="substitute-markup">
                    <xsl:with-param name="allow-anchors" select="'1'"/>
                    <xsl:with-param name="template" select="'%t'"/>
                  </xsl:call-template>          
                </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <!-- Overrides default sect1 title -->
  <xsl:template name="sect1.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              space-before="{$sect1.title.space-before}" 
              margin-bottom="{$sect1.title.margin-bottom}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block line-height="118%" margin-bottom="2mm"
                        font-family="SansSerif-Bold" font-size="22pt" 
                        color="black" text-align="right"
                        margin-right="0.01mm"> <!-- TODO: resolve the 0.01mm margin problem -->
                <xsl:call-template name="substitute-markup">
                  <xsl:with-param name="allow-anchors" select="'1'"/>
                  <xsl:with-param name="template" select="'%n'"/>
                </xsl:call-template>          
              </fo:block>
            </fo:table-cell>

            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$sect1.title.font-size}" 
                        wrap-option="{$sect1.title.wrap-option}"
                        line-height="75%" margin-left="3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="substitute-markup">
                  <xsl:with-param name="allow-anchors" select="'1'"/>
                  <xsl:with-param name="template" select="'%t'"/>
                </xsl:call-template>          
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template name="sect2.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              space-before="{$sect2.title.space-before}" 
              margin-bottom="{$sect2.title.margin-bottom}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-width="0" display-align="after">
              <fo:block font-family="SansSerif-Bold" font-size="15pt" 
                        line-height="127%" text-align="right"
                        margin-right="0.01mm"> <!-- TODO: resolve the 0.01mm margin problem -->
                <xsl:call-template name="substitute-markup">
                  <xsl:with-param name="allow-anchors" select="'1'"/>
                  <xsl:with-param name="template" select="'%n'"/>
                </xsl:call-template>          
              </fo:block>
            </fo:table-cell>
            <fo:table-cell display-align="after">
              <fo:block font-size="{$sect2.title.font-size}" 
                        wrap-option="{$sect2.title.wrap-option}"
                        line-height="75%" margin-left="3mm" text-align="left">
                <xsl:call-template name="substitute-markup">
                  <xsl:with-param name="allow-anchors" select="'1'"/>
                  <xsl:with-param name="template" select="'%t'"/>
                </xsl:call-template>          
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template name="sect3.titlepage">
    <!-- TODO: why margin doesn't work here? -->
    <fo:block padding-left="{$body.margin.inner}"
              font-weight="bold" font-size="{$sect3.title.font-size}" line-height="75%" 
              wrap-option="{$sect2.title.wrap-option}"
              space-before="{$sect3.title.space-before}" 
              margin-bottom="{$sect3.title.margin-bottom}"
              text-align="left" keep-with-next="always">
      <xsl:call-template name="substitute-markup">
        <xsl:with-param name="allow-anchors" select="'1'"/>
        <xsl:with-param name="template" select="'%t'"/>
      </xsl:call-template>          
    </fo:block>
  </xsl:template>

  <xsl:template match="title" mode="simplesect.titlepage.recto.auto.mode">
    <fo:block font-family="Serif-Bold" font-size="10pt" line-height="75%" space-before="7mm" margin-bottom="0mm" text-align="left" keep-with-next="always">
      <xsl:call-template name="substitute-markup">
        <xsl:with-param name="allow-anchors" select="'1'"/>
        <xsl:with-param name="template" select="'%t'"/>
      </xsl:call-template>          
    </fo:block>
  </xsl:template>

  <xsl:template name="bibliography.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              margin-bottom="{$biblio.title.margin-bottom}"
              margin-top="{$biblio.title.margin-top}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after"/>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$biblio.title.font-size}" 
                        wrap-option="{$biblio.title.wrap-option}"
                        line-height="80%" margin-left="2.3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="component.title">
                  <xsl:with-param name="node" select="ancestor-or-self::bibliography[1]"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>  

  <xsl:template name="list.of.examples.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              margin-bottom="{$list-of-examples.title.margin-bottom}"
              margin-top="{$list-of-examples.title.margin-top}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after"/>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$list-of-examples.title.font-size}" 
                        wrap-option="{$list-of-examples.title.wrap-option}"
                        line-height="80%" margin-left="2.3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="gentext">
                  <xsl:with-param name="key" select="'ListofExamples'"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>  

  <xsl:template name="list.of.tables.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              margin-bottom="{$list-of-tables.title.margin-bottom}"
              margin-top="{$list-of-tables.title.margin-top}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after"/>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$list-of-tables.title.font-size}" 
                        wrap-option="{$list-of-tables.title.wrap-option}"
                        line-height="80%" margin-left="2.3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="gentext">
                  <xsl:with-param name="key" select="'ListofTables'"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>  

  <xsl:template name="list.of.figures.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              margin-bottom="{$list-of-figures.title.margin-bottom}"
              margin-top="{$list-of-figures.title.margin-top}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after"/>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$list-of-figures.title.font-size}" 
                        wrap-option="{$list-of-figures.title.wrap-option}"
                        line-height="80%" margin-left="2.3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="gentext">
                  <xsl:with-param name="key" select="'ListofFigures'"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>  

  <xsl:template name="table.of.contents.titlepage">
    <fo:block margin-left="{$body.margin.inner}" 
              margin-bottom="{$table-of-contents.title.margin-bottom}"
              margin-top="{$table-of-contents.title.margin-top}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after"/>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$table-of-contents.title.font-size}" 
                        wrap-option="{$table-of-contents.title.wrap-option}"
                        line-height="80%" margin-left="2.3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="gentext">
                  <xsl:with-param name="key" select="'TableofContents'"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>  

  <xsl:template match="abstract" mode="book.titlepage.verso.auto.mode">
    <xsl:variable name="margin-top">
      <xsl:choose>
        <xsl:when test="preceding-sibling::abstract"><xsl:value-of select="$abstract.title.spacing"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="$abstract.title.spacing"/> + <xsl:value-of select="$abstract.title.margin-top"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
  
    <fo:block margin-left="{$body.margin.inner}" 
              margin-top="{$margin-top}" 
              margin-bottom="{$abstract.title.margin-bottom}"
              keep-with-next.within-page="always">
      <fo:table border-width="0.1mm">
        <fo:table-column column-number="1" column-width="{$marginbar.width}"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after"/>

            <fo:table-cell border-after-color="black" 
                           border-after-style="solid" 
                           border-after-width="0.2mm" display-align="after">
              <fo:block font-size="{$abstract.title.font-size}" 
                        line-height="75%" margin-left="3mm"
                        padding-bottom="2mm" text-align="left">
                <xsl:call-template name="gentext">
                  <xsl:with-param name="key" select="'Abstract'"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <fo:table-row>
            <fo:table-cell background-color="black" height="2mm"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
    
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
      <xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="ackno" mode="book.titlepage.verso.auto.mode">
    <fo:block margin-bottom="8mm" margin-top="30mm">
      <fo:float float="start">
        <fo:block-container background-color="white" border-after-color="black" border-after-style="solid" border-after-width="2mm" height="34mm" width="25mm" display-align="after">
           <fo:block width="10mm" height="10mm" margin-right="1mm" margin-bottom="3mm">
           </fo:block>
        </fo:block-container>
      </fo:float>
    
      <fo:float float="start">
        <fo:block-container height="34mm" width="100%" display-align="after" padding-left="1mm" border-after-color="black" border-after-style="solid" border-after-width="0.2mm">
          <fo:block margin-left="2mm" line-height="100%" margin-bottom="1mm" 
                     font-family="BookAntiquaCE" font-size="30pt" color="black" 
                     text-align="left" vertical-align="bottom">
            Acknowledgments
          </fo:block>
        </fo:block-container>
      </fo:float>
    </fo:block>
    <fo:block margin-bottom="8mm" clear="start"/>
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
      <xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>
