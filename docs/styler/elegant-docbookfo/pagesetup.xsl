<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

  <xsl:template name="setup.pagemasters">
    <fo:layout-master-set>
      <!-- blank pages -->
      <fo:simple-page-master master-name="blank"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-right="{$page.margin.outer}"
                             margin-left="{$page.margin.inner}">
        <fo:region-body display-align="center"
                        margin-right="{$body.margin.inner}"
                        margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}">
          <xsl:if test="$fop.extensions = 0">
            <xsl:attribute name="region-name">blank-body</xsl:attribute>
          </xsl:if>
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-blank"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-blank"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <!-- title pages -->
      <fo:simple-page-master master-name="titlepage-first"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.titlepage}"
                        column-count="{$column.count.titlepage}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-first"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-first"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="titlepage-odd"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.titlepage}"
                        column-count="{$column.count.titlepage}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-odd"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-odd"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="titlepage-even"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}">
        <xsl:if test="$double.sided = 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$double.sided != 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
        </xsl:if>

        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.titlepage}"
                        column-count="{$column.count.titlepage}">
          <xsl:if test="$double.sided = 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$double.sided != 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-even"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-even"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <!-- list-of-title pages -->
      <fo:simple-page-master master-name="lot-first"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.lot}"
                        column-count="{$column.count.lot}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-first"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-first"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="lot-odd"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.lot}"
                        column-count="{$column.count.lot}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-odd"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-odd"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="lot-even"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}">
        <xsl:if test="$double.sided = 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$double.sided != 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
        </xsl:if>

        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.body}"
                        column-count="{$column.count.body}">
          <xsl:if test="$double.sided = 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$double.sided != 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-even"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-even"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <!-- frontmatter pages -->
      <fo:simple-page-master master-name="front-first"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.front}"
                        column-count="{$column.count.front}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-first"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-first"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="front-odd"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.front}"
                        column-count="{$column.count.front}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-odd"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-odd"
                         extent="{$region.after.extent}"
                          display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="front-even"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}">

        <xsl:if test="$double.sided = 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$double.sided != 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
        </xsl:if>

        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.body}"
                        column-count="{$column.count.body}">
          <xsl:if test="$double.sided = 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$double.sided != 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-even"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-even"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <!-- body pages -->
      <fo:simple-page-master master-name="body-first"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.body}"
                        column-count="{$column.count.body}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-first"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-first"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="body-odd"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.body}"
                        column-count="{$column.count.body}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-odd"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-odd"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="body-even"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}">

        <xsl:if test="$double.sided = 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$double.sided != 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
        </xsl:if>

        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.body}"
                        column-count="{$column.count.body}">
          <xsl:if test="$double.sided = 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$double.sided != 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-even"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-even"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <!-- backmatter pages -->
      <fo:simple-page-master master-name="back-first"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.back}"
                        column-count="{$column.count.back}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-first"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-first"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="back-odd"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        margin-right="{$body.margin.inner}"
                        column-gap="{$column.gap.back}"
                        column-count="{$column.count.back}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-odd"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-odd"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="back-even"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}">

        <xsl:if test="$double.sided = 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$double.sided != 0">
          <xsl:attribute name="margin-right">
            <xsl:value-of select="$page.margin.inner"/>
          </xsl:attribute>
          <xsl:attribute name="margin-left">
            <xsl:value-of select="$page.margin.outer"/>
          </xsl:attribute>
        </xsl:if>

        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.back}"
                        column-count="{$column.count.back}">
          <xsl:if test="$double.sided = 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$double.sided != 0">
            <xsl:attribute name="margin-right">
              <xsl:value-of select="$body.margin.inner"/>
            </xsl:attribute>
          </xsl:if>
        </fo:region-body>
        
        <fo:region-before region-name="xsl-region-before-even"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-even"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <!-- index pages -->
      <fo:simple-page-master master-name="index-first"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.index}"
                        column-count="{$column.count.index}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-first"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-first"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="index-odd"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-left="{$page.margin.inner}"
                             margin-right="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.index}"
                        column-count="{$column.count.index}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-odd"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-odd"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <fo:simple-page-master master-name="index-even"
                             page-width="{$page.width}"
                             page-height="{$page.height}"
                             margin-top="{$page.margin.top}"
                             margin-bottom="{$page.margin.bottom}"
                             margin-right="{$page.margin.inner}"
                             margin-left="{$page.margin.outer}">
        <fo:region-body margin-bottom="{$body.margin.bottom}"
                        margin-top="{$body.margin.top}"
                        column-gap="{$column.gap.index}"
                        column-count="{$column.count.index}">
        </fo:region-body>
        <fo:region-before region-name="xsl-region-before-even"
                          extent="{$region.before.extent}"
                          display-align="before"/>
        <fo:region-after region-name="xsl-region-after-even"
                         extent="{$region.after.extent}"
                         display-align="after"/>
      </fo:simple-page-master>

      <xsl:if test="$draft.mode != 'no'">
        <!-- draft blank pages -->
        <fo:simple-page-master master-name="blank-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.outer}"
                               margin-right="{$page.margin.inner}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-blank"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-blank"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <!-- draft title pages -->
        <fo:simple-page-master master-name="titlepage-first-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.titlepage}"
                          column-count="{$column.count.titlepage}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-first"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-first"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="titlepage-odd-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.titlepage}"
                          column-count="{$column.count.titlepage}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-odd"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-odd"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="titlepage-even-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-right="{$page.margin.inner}"
                               margin-left="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.titlepage}"
                          column-count="{$column.count.titlepage}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-even"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-even"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <!-- draft list-of-title pages -->
        <fo:simple-page-master master-name="lot-first-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.lot}"
                          column-count="{$column.count.lot}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-first"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-first"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="lot-odd-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.lot}"
                          column-count="{$column.count.lot}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-odd"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-odd"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="lot-even-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-right="{$page.margin.inner}"
                               margin-left="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.lot}"
                          column-count="{$column.count.lot}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-even"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-even"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <!-- draft frontmatter pages -->
        <fo:simple-page-master master-name="front-first-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.front}"
                          column-count="{$column.count.front}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-first"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-first"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="front-odd-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.front}"
                          column-count="{$column.count.front}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-odd"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-odd"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="front-even-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-right="{$page.margin.inner}"
                               margin-left="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.front}"
                          column-count="{$column.count.front}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-even"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-even"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <!-- draft body pages -->
        <fo:simple-page-master master-name="body-first-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.body}"
                          column-count="{$column.count.body}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-first"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-first"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="body-odd-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.body}"
                          column-count="{$column.count.body}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-odd"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-odd"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="body-even-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-right="{$page.margin.inner}"
                               margin-left="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.body}"
                          column-count="{$column.count.body}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-even"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-even"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <!-- draft backmatter pages -->
        <fo:simple-page-master master-name="back-first-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.back}"
                          column-count="{$column.count.back}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-first"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-first"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="back-odd-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.back}"
                          column-count="{$column.count.back}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-odd"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-odd"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="back-even-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-right="{$page.margin.inner}"
                               margin-left="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.back}"
                          column-count="{$column.count.back}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-even"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-even"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <!-- draft index pages -->
        <fo:simple-page-master master-name="index-first-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.index}"
                          column-count="{$column.count.index}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-first"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-first"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="index-odd-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-left="{$page.margin.inner}"
                               margin-right="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.index}"
                          column-count="{$column.count.index}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-odd"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-odd"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>

        <fo:simple-page-master master-name="index-even-draft"
                               page-width="{$page.width}"
                               page-height="{$page.height}"
                               margin-top="{$page.margin.top}"
                               margin-bottom="{$page.margin.bottom}"
                               margin-right="{$page.margin.inner}"
                               margin-left="{$page.margin.outer}">
          <fo:region-body margin-bottom="{$body.margin.bottom}"
                          margin-top="{$body.margin.top}"
                          column-gap="{$column.gap.index}"
                          column-count="{$column.count.index}">
            <xsl:if test="$draft.watermark.image != ''
                          and $fop.extensions = 0">
              <xsl:attribute name="background-image">
                <xsl:call-template name="fo-external-image">
                  <xsl:with-param name="filename" select="$draft.watermark.image"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="background-attachment">fixed</xsl:attribute>
              <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
              <xsl:attribute name="background-position-horizontal">center</xsl:attribute>
              <xsl:attribute name="background-position-vertical">center</xsl:attribute>
            </xsl:if>
          </fo:region-body>
          <fo:region-before region-name="xsl-region-before-even"
                            extent="{$region.before.extent}"
                            display-align="before"/>
          <fo:region-after region-name="xsl-region-after-even"
                           extent="{$region.after.extent}"
                           display-align="after"/>
        </fo:simple-page-master>
      </xsl:if>

      <!-- setup for title page(s) -->
      <fo:page-sequence-master master-name="titlepage">
        <fo:repeatable-page-master-alternatives>
          <fo:conditional-page-master-reference master-reference="blank"
                                                blank-or-not-blank="blank"/>
          <fo:conditional-page-master-reference master-reference="titlepage-first"
                                                page-position="first"/>
          <fo:conditional-page-master-reference master-reference="titlepage-odd"
                                                odd-or-even="odd"/>
          <fo:conditional-page-master-reference master-reference="titlepage-even"
                                                odd-or-even="even"/>
        </fo:repeatable-page-master-alternatives>
      </fo:page-sequence-master>

      <!-- setup for lots -->
      <fo:page-sequence-master master-name="lot">
        <fo:repeatable-page-master-alternatives>
          <fo:conditional-page-master-reference master-reference="blank"
                                                blank-or-not-blank="blank"/>
          <fo:conditional-page-master-reference master-reference="lot-first"
                                                page-position="first"/>
          <fo:conditional-page-master-reference master-reference="lot-odd"
                                                odd-or-even="odd"/>
          <fo:conditional-page-master-reference master-reference="lot-even"
                                                odd-or-even="even"/>
        </fo:repeatable-page-master-alternatives>
      </fo:page-sequence-master>

      <!-- setup front matter -->
      <fo:page-sequence-master master-name="front">
        <fo:repeatable-page-master-alternatives>
          <fo:conditional-page-master-reference master-reference="blank"
                                                blank-or-not-blank="blank"/>
          <fo:conditional-page-master-reference master-reference="front-first"
                                                page-position="first"/>
          <fo:conditional-page-master-reference master-reference="front-odd"
                                                odd-or-even="odd"/>
          <fo:conditional-page-master-reference master-reference="front-even"
                                                odd-or-even="even"/>
        </fo:repeatable-page-master-alternatives>
      </fo:page-sequence-master>

      <!-- setup for body pages -->
      <fo:page-sequence-master master-name="body">
        <fo:repeatable-page-master-alternatives>
          <fo:conditional-page-master-reference master-reference="blank"
                                                blank-or-not-blank="blank"/>
          <fo:conditional-page-master-reference master-reference="body-first"
                                                page-position="first"/>
          <fo:conditional-page-master-reference master-reference="body-odd"
                                                odd-or-even="odd"/>
          <fo:conditional-page-master-reference master-reference="body-even"
                                                odd-or-even="even"/>
        </fo:repeatable-page-master-alternatives>
      </fo:page-sequence-master>

      <!-- setup back matter -->
      <fo:page-sequence-master master-name="back">
        <fo:repeatable-page-master-alternatives>
          <fo:conditional-page-master-reference master-reference="blank"
                                                blank-or-not-blank="blank"/>
          <fo:conditional-page-master-reference master-reference="back-first"
                                                page-position="first"/>
          <fo:conditional-page-master-reference master-reference="back-odd"
                                                odd-or-even="odd"/>
          <fo:conditional-page-master-reference master-reference="back-even"
                                                odd-or-even="even"/>
        </fo:repeatable-page-master-alternatives>
      </fo:page-sequence-master>

      <!-- setup back matter -->
      <fo:page-sequence-master master-name="index">
        <fo:repeatable-page-master-alternatives>
          <fo:conditional-page-master-reference master-reference="blank"
                                                blank-or-not-blank="blank"/>
          <fo:conditional-page-master-reference master-reference="index-first"
                                                page-position="first"/>
          <fo:conditional-page-master-reference master-reference="index-odd"
                                                odd-or-even="odd"/>
          <fo:conditional-page-master-reference master-reference="index-even"
                                                odd-or-even="even"/>
        </fo:repeatable-page-master-alternatives>
      </fo:page-sequence-master>

      <xsl:if test="$draft.mode != 'no'">
        <!-- setup for draft title page(s) -->
        <fo:page-sequence-master master-name="titlepage-draft">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="blank-draft"
                                                  blank-or-not-blank="blank"/>
            <fo:conditional-page-master-reference master-reference="titlepage-first-draft"
                                                  page-position="first"/>
            <fo:conditional-page-master-reference master-reference="titlepage-odd-draft"
                                                  odd-or-even="odd"/>
            <fo:conditional-page-master-reference master-reference="titlepage-even-draft"
                                                  odd-or-even="even"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>

        <!-- setup for draft lots -->
        <fo:page-sequence-master master-name="lot-draft">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="blank-draft"
                                                  blank-or-not-blank="blank"/>
            <fo:conditional-page-master-reference master-reference="lot-first-draft"
                                                  page-position="first"/>
            <fo:conditional-page-master-reference master-reference="lot-odd-draft"
                                                  odd-or-even="odd"/>
            <fo:conditional-page-master-reference master-reference="lot-even-draft"
                                                  odd-or-even="even"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>

        <!-- setup draft front matter -->
        <fo:page-sequence-master master-name="front-draft">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="blank-draft"
                                                  blank-or-not-blank="blank"/>
            <fo:conditional-page-master-reference master-reference="front-first-draft"
                                                  page-position="first"/>
            <fo:conditional-page-master-reference master-reference="front-odd-draft"
                                                  odd-or-even="odd"/>
            <fo:conditional-page-master-reference master-reference="front-even-draft"
                                                  odd-or-even="even"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>

        <!-- setup for draft body pages -->
        <fo:page-sequence-master master-name="body-draft">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="blank-draft"
                                                  blank-or-not-blank="blank"/>
            <fo:conditional-page-master-reference master-reference="body-first-draft"
                                                  page-position="first"/>
            <fo:conditional-page-master-reference master-reference="body-odd-draft"
                                                  odd-or-even="odd"/>
            <fo:conditional-page-master-reference master-reference="body-even-draft"
                                                  odd-or-even="even"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>

        <!-- setup draft back matter -->
        <fo:page-sequence-master master-name="back-draft">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="blank-draft"
                                                  blank-or-not-blank="blank"/>
            <fo:conditional-page-master-reference master-reference="back-first-draft"
                                                  page-position="first"/>
            <fo:conditional-page-master-reference master-reference="back-odd-draft"
                                                  odd-or-even="odd"/>
            <fo:conditional-page-master-reference master-reference="back-even-draft"
                                                  odd-or-even="even"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>

        <!-- setup draft index pages -->
        <fo:page-sequence-master master-name="index-draft">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference master-reference="blank-draft"
                                                  blank-or-not-blank="blank"/>
            <fo:conditional-page-master-reference master-reference="index-first-draft"
                                                  page-position="first"/>
            <fo:conditional-page-master-reference master-reference="index-odd-draft"
                                                  odd-or-even="odd"/>
            <fo:conditional-page-master-reference master-reference="index-even-draft"
                                                  odd-or-even="even"/>
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>
      </xsl:if>

      <xsl:call-template name="user.pagemasters"/>

      </fo:layout-master-set>
  </xsl:template>

  <!-- marginbar-indent added -->
  <xsl:template name="footnote-separator">
    <fo:static-content flow-name="xsl-footnote-separator">
      <fo:block start-indent="{$marginbar.indent-size}">
        <fo:leader color="black" leader-pattern="rule" leader-length="1in" rule-thickness="0.1mm"/>
      </fo:block>
    </fo:static-content>
  </xsl:template>

  <!-- No header -->
  <xsl:template name="header.table"/>

  <!-- No footer, just page number -->
  <xsl:template name="footer.table">
    <xsl:param name="pageclass" select="''"/>
    <xsl:param name="sequence" select="''"/>
    <xsl:param name="gentext-key" select="''"/>

    <xsl:variable name="candidate">
      <fo:block text-align="right">
        <fo:page-number/>
      </fo:block>
    </xsl:variable>

    <!-- Really output a footer? -->
    <xsl:choose>
      <xsl:when test="$pageclass='titlepage' and $gentext-key='book'">
<!--                      and $sequence='first'"> -->
        <!-- no, book titlepages have no footers at all -->
      </xsl:when>
      <xsl:when test="$sequence = 'blank' and $footers.on.blank.pages = 0">
        <!-- no output -->
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$candidate"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
