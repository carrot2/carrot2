

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

The filter processes only streams which send a proper MIME type (text/xml or application/xml) 
and define an xml-stylesheet header:

<?xml-stylesheet type="text/xsl" href="..." ?>

The 'href' attribute can be any of the following:

    a) a full external URL
    b) a server-relative URL (starting with a '/' character)
    c) an application-context-relative URL (starting with a "@/" sequence)

The stylesheet is cached in memory for subsequent use.
