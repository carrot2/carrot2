

XSLT Filter for Java Web applications


HOW TO USE IT
-------------

Define the filter in Web application descriptor (web.xml) and map it to any extension which should
be processed. Something like this should do:

	<filter>
		<filter-name>xslt-filter</filter-name>
		<display-name>XSLT Filter</display-name>
		<filter-class>org.carrot2.util.xsltfilter.XSLTFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>xslt-filter</filter-name>
		<url-pattern>*.xml</url-pattern>
	</filter-mapping>

The filter applies only to streams that send XML MIME type (text/xml or application/xml) AND:
   
  - define an extended stylesheet header with application context-relative
    resource (ignored by browsers):

    <?ext-stylesheet resource="" ?> 

  OR

  - define an xml-stylesheet header:

    <?xml-stylesheet type="text/xsl" href="..." ?>

    Where 'href' attribute can be any of the following (in the recommended order):

    - current URI-relative path,
    - full external URL.

The stylesheet is cached in memory for subsequent use.
