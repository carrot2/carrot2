<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version='1.0'>

  <!-- all templates: added from-parent() to start-indent and end-indent -->
  
  <xsl:template match="book|setindex" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:variable name="nodes" select="glossary|bibliography|preface|chapter
                                       |reference|part|article|appendix|index"/>

    <xsl:if test="$toc.section.depth &gt; 0 and $nodes">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {count(ancestor::*)*$toc.indent.width}pt">
        <xsl:apply-templates select="$nodes" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="part" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:variable name="nodes" select="chapter|appendix|preface|reference"/>

    <xsl:if test="$toc.section.depth &gt; 0 and $nodes">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {count(ancestor::*)*$toc.indent.width}pt">
        <xsl:apply-templates select="$nodes" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="reference" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:if test="$toc.section.depth &gt; 0 and refentry">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {count(ancestor::*)*$toc.indent.width}pt">
        <xsl:apply-templates select="refentry" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="preface|chapter|appendix|article"
                mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:variable name="nodes" select="section|sect1"/>

    <xsl:if test="$toc.section.depth &gt; 0 and $nodes">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {count(ancestor::*)*$toc.indent.width}pt">
        <xsl:apply-templates select="$nodes" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sect1" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:if test="$toc.section.depth &gt; 1 and sect2">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {count(ancestor::*)*$toc.indent.width}pt">
        <xsl:apply-templates select="sect2" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sect2" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:variable name="reldepth"
                  select="count(ancestor::*)-count($toc-context/ancestor::*)"/>

    <xsl:if test="$toc.section.depth &gt; 2 and sect3">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {$reldepth*$toc.indent.width}pt">
        <xsl:apply-templates select="sect3" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sect3" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:variable name="reldepth"
                  select="count(ancestor::*)-count($toc-context/ancestor::*)"/>

    <xsl:if test="$toc.section.depth &gt; 3 and sect4">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {$reldepth*$toc.indent.width}pt">
        <xsl:apply-templates select="sect4" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sect4" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="toc.line"/>

    <xsl:variable name="reldepth"
                  select="count(ancestor::*)-count($toc-context/ancestor::*)"/>

    <xsl:if test="$toc.section.depth &gt; 4 and sect5">
      <fo:block id="toc.{$cid}.{$id}"
                start-indent="from-parent() + {$reldepth*$toc.indent.width}pt">
        <xsl:apply-templates select="sect5" mode="toc">
          <xsl:with-param name="toc-context" select="$toc-context"/>
        </xsl:apply-templates>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="cid">
      <xsl:call-template name="object.id">
        <xsl:with-param name="object" select="$toc-context"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="depth" select="count(ancestor::section) + 1"/>
    <xsl:variable name="reldepth"
                  select="count(ancestor::*)-count($toc-context/ancestor::*)"/>

    <xsl:if test="$toc.section.depth &gt;= $depth">
      <xsl:call-template name="toc.line"/>

      <xsl:if test="$toc.section.depth &gt; $depth and section">
        <fo:block id="toc.{$cid}.{$id}"
                  start-indent="from-parent() + {$reldepth*$toc.indent.width}pt">
          <xsl:apply-templates select="section" mode="toc">
            <xsl:with-param name="toc-context" select="$toc-context"/>
          </xsl:apply-templates>
        </fo:block>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template name="toc.line">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>

    <xsl:variable name="label">
      <xsl:apply-templates select="." mode="label.markup"/>
    </xsl:variable>

    <fo:block text-align-last="justify"
              end-indent="from-parent() + {$toc.indent.width}pt"
              last-line-end-indent="-{$toc.indent.width}pt">
      <fo:inline keep-with-next.within-line="always">
        <fo:basic-link internal-destination="{$id}">
          <xsl:if test="$label != ''">
            <xsl:copy-of select="$label"/>
            <xsl:value-of select="$autotoc.label.separator"/>
          </xsl:if>
          <xsl:apply-templates select="." mode="title.markup"/>
        </fo:basic-link>
      </fo:inline>
      <fo:inline keep-together.within-line="always">
        <xsl:text> </xsl:text>
        <fo:leader leader-pattern="dots"
                   leader-pattern-width="3pt"
                   leader-alignment="reference-area"
                   keep-with-next.within-line="always"/>
        <xsl:text> </xsl:text> 
        <fo:basic-link internal-destination="{$id}">
          <fo:page-number-citation ref-id="{$id}"/>
        </fo:basic-link>
      </fo:inline>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>
