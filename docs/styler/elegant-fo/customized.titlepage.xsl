<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>


               
<xsl:include href="generated.titlepage.xsl" />


<xsl:template name="customized.book.verso.title">
    <fo:block>
        <xsl:apply-templates mode="titlepage.mode"/>
        
        <xsl:if test="following-sibling::subtitle
                     |following-sibling::bookinfo/subtitle">
                <xsl:apply-templates select="(following-sibling::subtitle
                                             |following-sibling::bookinfo/subtitle)[1]"
                                     mode="book.verso.subtitle.mode"/>
        </xsl:if>
    </fo:block>
</xsl:template>


<xsl:template name="customized.book.title">
    <fo:block>
        <xsl:apply-templates mode="titlepage.mode"/>
    </fo:block>
</xsl:template>


<xsl:template match="revhistory" mode="titlepage.mode">
  <fo:table table-layout="fixed">
    <fo:table-column column-number="1" column-width="2cm"/>
    <fo:table-column column-number="2" column-width="1.5cm"/>
    <fo:table-column column-number="3" column-width="2.5cm"/>
    <fo:table-column column-number="4" column-width="*"/>
    <fo:table-body>
      <fo:table-row keep-with-next="always">
        <fo:table-cell number-columns-spanned="4" padding-top="1cm" padding-bottom="1cm">
            <xsl:variable name="revname">
                <xsl:call-template name="gentext">
                  <xsl:with-param name="key" select="'RevHistory'"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="section.heading">
              <xsl:with-param name="level" select="'1'"/>
              <xsl:with-param name="title" select="$revname"/>
            </xsl:call-template>
        </fo:table-cell>
      </fo:table-row>
      
      <fo:table-row>
        <fo:table-cell>
          <fo:block background-color="black" color="white" 
                    text-align="center"  margin-right="0.1cm">
          Number
          </fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block background-color="black" color="white" 
                    text-align="center"  margin-right="0.1cm">
          Author
          </fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block background-color="black" color="white" 
                    text-align="center"  margin-right="0.1cm">
          Date
          </fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block background-color="black" color="white" 
                    text-align="center"  margin-right="0.1cm">
          Changes
          </fo:block>
        </fo:table-cell>
      </fo:table-row>

      <xsl:apply-templates mode="titlepage.mode"/>
    </fo:table-body>
  </fo:table>
</xsl:template>


<xsl:template match="revhistory/revision" mode="titlepage.mode">
  <xsl:variable name="revnumber" select=".//revnumber"/>
  <xsl:variable name="revdate"   select=".//date"/>
  <xsl:variable name="revauthor" select=".//authorinitials"/>
  <xsl:variable name="revremark" select=".//revremark|.//revdescription"/>
  <fo:table-row>
    <fo:table-cell>
      <fo:block text-align="center" margin-right="0.1cm">
        <xsl:if test="$revnumber">
          <xsl:apply-templates select="$revnumber[1]" mode="titlepage.mode"/>
        </xsl:if>
      </fo:block>
    </fo:table-cell>
    <fo:table-cell >
      <fo:block text-align="center" margin-right="0.1cm">
        <xsl:apply-templates select="$revauthor[1]" mode="titlepage.mode"/>
      </fo:block>
    </fo:table-cell>
    <fo:table-cell>
      <fo:block text-align="center" margin-right="0.1cm">
        <xsl:apply-templates select="$revdate[1]" mode="titlepage.mode"/>
      </fo:block>
    </fo:table-cell>
    <fo:table-cell >
      <fo:block text-align="left" margin-right="0.1cm" margin-bottom="3pt">
        <xsl:if test="$revremark">
          <xsl:apply-templates select="$revremark[1]" mode="titlepage.mode"/>
        </xsl:if>
      </fo:block>
    </fo:table-cell>
  </fo:table-row>
</xsl:template>

</xsl:stylesheet>


