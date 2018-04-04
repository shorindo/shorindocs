<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/1999/xhtml">

  <xsl:namespace-alias stylesheet-prefix="html" result-prefix="#default"/>
  <xsl:strip-space elements="*"/>
  <xsl:output method="html"/>

  <xsl:template match="/xuml">
    <xsl:choose>
      <xsl:when test="@wrapper">
        <todo>
          <xsl:apply-templates/>
        </todo>
      </xsl:when>
      <xsl:otherwise>
        <html>
          <head>
            <title><xsl:value-of select="@title"/></title>
            <link rel="stylesheet" type="text/css" href="{{{{application.contextPath}}}}/css/xuml.css"/>
            <script type="text/javascript" src="{{{{application.contextPath}}}}/js/xuml.js"></script>
          </head>
          <body>
            <xsl:attribute name="class">
              <xsl:choose>
                <xsl:when test="@width='fill'">xuml-width-fill </xsl:when>
                <xsl:when test="@width='auto'">xuml-width-auto </xsl:when>
                <xsl:otherwise>xuml-width-auto </xsl:otherwise>
              </xsl:choose>
              <xsl:choose>
                <xsl:when test=" @height='fill'">xuml-height-fill</xsl:when>
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
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="/*[name()!='xuml']">
  </xsl:template>

  <xsl:template match="window">
    <div class="xuml-window-pane">
      <div class="xuml-window">
        <xsl:attribute name="style">
          <xsl:value-of select="@style"/>
        </xsl:attribute>
        <div class="xuml-window-head">
          <xsl:value-of select="@title"/>
        </div>
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>
  
  <xsl:template match="dialog">
    <div class="xuml-dialog-pane">
      <div class="xuml-dialog">
        <xsl:attribute name="style">
          <xsl:value-of select="@style"/>
        </xsl:attribute>
        <div class="xuml-dialog-head"><xsl:value-of select="@title"/></div>
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

  <xsl:template match="hbox">
    <div class="xuml-hbox">
        <xsl:attribute name="style">
          <xsl:value-of select="@style"/>
        </xsl:attribute>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="vbox">
    <div class="xuml-vbox">
      <xsl:attribute name="style">width:<xsl:value-of select="@width"/>;</xsl:attribute>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="groupbox">
    <div class="layout-caption"><span><xsl:value-of select="."/></span></div>
  </xsl:template>

  <xsl:template match="listbox">
    <ul class="xuml-listbox">
      <xsl:attribute name="style">
        <xsl:value-of select="@style"/>
      </xsl:attribute>
      <xsl:apply-templates select="listitem|text()"/>
    </ul>
  </xsl:template>

  <xsl:template name="listitem" match="listitem">
    <li class="xuml-listitem">
      <xsl:apply-templates/>
    </li>
  </xsl:template>

  <xsl:template match="tree">
  </xsl:template>

  <xsl:template name="message" match="message">
    <div class="xuml-message">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="link">
    <a>
      <xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
      <xsl:apply-templates/>
    </a>
  </xsl:template>

  <xsl:template match="spacer">
    <div class="xuml-spacer"></div>
  </xsl:template>

  <xsl:template name="html" match="html:*">
    <xsl:element name="{local-name()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="text" match="text()|@*">
    <xsl:copy-of select="."/>
  </xsl:template>
</xsl:stylesheet>
