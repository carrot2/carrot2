
INFO
====

This is a set of binary Carrot2 components.

Copy these folders to a Tomcat installation. Run Tomcat. Access:

http://localhost:8080/carrot2-remote-controller


IMPORTANT
=========

1. 

If your Tomcat is not set up to work on port 8080, you will have to manually edit
Component descriptors found in:

webapps/carrot2-remote-controller.war/WEB-INF/components

2.

If you are using Tomcat 5, you will encounter query encoding issues.

You must enforce proper decoding of URI parameters in the connector. You
can do it by adding this attribute to the connector spec. in server.xml

URIEncoding="utf-8"

3. To access the "admin" link in Carrot2 controller, add a user with role "carrot-admin"
to your tomcat users (/conf/tomcat-users.xml in Tomcat 5.x). For example:

<role rolename="carrot-admin"/>
<user username="c2admin" password="password:)" roles="carrot-admin"/>

--
Carrot2, http://carrot2.sf.net
