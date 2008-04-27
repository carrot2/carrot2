<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:include href="../fancy-common/page.xsl" />
  
  <xsl:output indent="yes" omit-xml-declaration="yes"
       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
       media-type="text/html" encoding="utf-8" />

  <xsl:template name="fancy-extra-css">
    <link rel="stylesheet" href="{$skin-path}/fancy-large/css/style.css" type="text/css" />
  </xsl:template>
</xsl:stylesheet>
