<%@page pageEncoding="UTF-8" %><%

	// response.setHeader("Content-Type", "application/x-java-jnlp-file");
	response.setHeader("Content-Type", "text/plain");
	response.setHeader("Cache-Control", "public, max-age=0");
	response.setHeader("Pragma", "x-no-cache");

	final String codebase;

	if (request.getParameter("codebase") == null) {
		String servletPath = request.getServletPath();
		servletPath = servletPath.substring(0, servletPath.lastIndexOf('/'));
		codebase = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath()
			+ servletPath;
	} else {
		codebase = request.getParameter("codebase");
	}
%>
<jnlp spec="1.0+" codebase="<%= codebase %>">
	<information>
		<title>Carrot2 Demo Application</title>
		<vendor>Carrot2 Project (www.carrot2.org)</vendor>

		<homepage href="http://www.carrot2.org/" />

		<description>Carrot2 Demo Application</description>
		<description kind="short">Carrot2 Demo Application for tuning and testing purposes.</description>
		<description kind="tooltip">Carrot2 Demo</description>

		<icon href="img/carrot2-64x64.gif" width="64" height="64" />

		<offline-allowed />
	</information>

	<resources>
		<j2se version="1.4+" />

		<jar href="carrot2-demo-browser.jar" />
		<jar href="lib/demo-resources.jar" />

		<%@include file="demo.inc" %>
	</resources>

	<resources os="Windows">
		<jar href="lib/windows/jdic.jar"/>
		<nativelib href="lib/windows/jdic-native.jar"/>
	</resources>

	<security>
		<all-permissions/>
	</security>

	<application-desc main-class="org.carrot2.demo.DemoSplash">
		<argument>/res/browser-splash.png</argument>
		<argument>4</argument>
		<argument>org.carrot2.demo.DemoSwing</argument>
		<argument>--resource</argument>
	</application-desc>
</jnlp>
