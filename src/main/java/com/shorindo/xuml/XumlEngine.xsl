<?xml version="1.0"?>
<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
   <xsl:output method="html"/>

  <xsl:template match="/xuml">
    <xsl:apply-templates/>
  </xsl:template>

   <xsl:template match="window">
    <html>
      <head>
        <title>{{title}}</title>
        <link rel="stylesheet" type="text/css" href="{{{{context}}}}/css/xuml.css"/>
        <script type="text/javascript" src="{{{{context}}}}/js/xuml.js"></script>
      </head>
      <body>
        <xsl:attribute name="class">
          <xsl:choose>
            <xsl:when test="@width='fill'">xuml-width-fill </xsl:when>
            <xsl:when test="@width='auto'">xuml-width-auto </xsl:when>
            <xsl:otherwise>xuml-width-auto </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="@height='fill'">xuml-height-fill</xsl:when>
            <xsl:when test="@height='auto'">xuml-height-auto</xsl:when>
            <xsl:otherwise>xuml-height-auto</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:attribute name="style">
          <xsl:value-of select="@style"/>
        </xsl:attribute>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="dialog">
    <div class="xuml-dialog-pane">
      <div class="xuml-dialog">
        <xsl:attribute name="style">
          <xsl:value-of select="@style"/>
        </xsl:attribute>
        <div class="xuml-dialog-head">{{title}}</div>
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="box">
    <div class="xuml-box">
        <xsl:attribute name="style">
          <xsl:value-of select="@style"/>
        </xsl:attribute>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="groupbox">
    <div class="layout-caption"><span><xsl:value-of select="."/></span></div>
  </xsl:template>

  <xsl:template match="listbox">
  </xsl:template>

  <xsl:template match="treebox">
  </xsl:template>

  <xsl:template name="message" match="message">
    <div class="xuml-message">{{message}}</div>
  </xsl:template>

  <xsl:template name="text" match="*|text()|@*">
    <xsl:copy-of select="."/>
  </xsl:template>
</xsl:stylesheet>
