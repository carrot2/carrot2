
Carrot2 Project
See the license file for legal information.


INFORMATION
===========

Carrot2 is a component-based data processing framework, created
especially for the reason of clustering search results from
search engines (but not only).

See more at:
http://carrot.cs.put.poznan.pl


ACQUIRING BINARY RELEASE
========================

It is advised that you grap a binary release of the Carrot2 components from the 
nightly build drop-off zone at:

http://carrot.cs.put.poznan.pl/static/download/nightly/


BUILDING FROM SOURCES
=====================

In order to build Carrot2's components, you must have ANT in your path. We use 
ANT 1.6, lower versions are not supported but may work.

This command should build all the components for you and place them in tmp/dist.

ant build


THE WEB CONTROLLER COMPONENT
============================

The web controller component for Carrot2 uses the rest of the components to 
process user queries. The controller looks for services offered by components 
using 'descriptors'. Sample component descriptors (and process descriptors that 
bind them together) can be found in 'descriptors' folder. You should adjust the 
host and port in these descriptors to match your configuration (localhost for 
local deployment). You can also override these properties at build-time using 
the following variables:

ant -Ddeployment.port=80 -Ddeployment.host=myhost.com build.webcontroller


TEST SUITE AND DOCUMENTATION
============================

Tests suite and documentation are available as a separate CVS checkout, or on-
line at:

http://carrot.cs.put.poznan.pl


CONTACT
=======

If you have questions or problems, subscribe to:

carrot2-developers@lists.sourceforge.net
carrot2-cvscommits@lists.sourceforge.net

This e-mailing list will keep you up to date with changes:

carrot2-news@lists.sourceforge.net

Other inquiries can be directed to project coordinator:

Dawid Weiss <dawid.weiss@cs.put.poznan.pl>

