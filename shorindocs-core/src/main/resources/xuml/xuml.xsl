<?xml version="1.0"?>
<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
   <xsl:output method="html"/>

   <xsl:template match="window">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>${title}</title>
        <link rel="stylesheet" type="text/css" href="${application.contextPath}/css/xuml.css"/>
        <script type="text/javascript" src="${application.contextPath}/js/xuml.js"></script>
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
   </xsl:template>

</xsl:stylesheet>
