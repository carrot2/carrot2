
This is Carrot2 manual in Docbook.

Actually, we use some extensions not available in plain DocBook, like file 
inclusion tags to split the manual into sections. Also, to simplify the build 
file, we use a DocBook-ANT styler package, freely available from Dawid Weiss' 
site:

http://www.cs.put.poznan.pl/dweiss/xml/projects/ant-docbook-styler/index.xml

The styler has an additional benefit of being able to detect xsltproc tool in 
your path and use it instead of the default Java XSLT processor. The speed 
increase is significant.

Edit your own 'local.properties' file and initialize it with two variables: 
'docbook.styler' and 'carrot2.cvs.dir'. These should point to a directory with 
Docbook-ANT styler and root of Carrot2 CVS checkout respectively. For example:

docbook.styler=F:\\Repositories\\home\\projects\\ant-docbook-styler\\ant-build-docbook.xml
carrot2.cvs.dir=F:\\Repositories\\sourceforge\\carrot2\\carrot2
