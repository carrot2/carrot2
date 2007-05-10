Carrot2 Browser

INFORMATION
===========

Carrot2 Browser is developed using Eclipse RPC SDK.

CONFIGURING ECLIPSE AND RUNNING THE BROWSER
=====================

The browser requires setting some preferences in workspace, 
namely Plugin Development -> Target Platform. Only most basic plugins 
are required in target platform , they are in RCP-SDK distribution, 
available in "Other downloads for 3.2" section on eclipse.org site. 
Expand it in a convenient location, say c:\target.
 
After installing the RCP-SDK you should have similar directory structure:
c:
->target
--> eclipse
In Plugin Development -> Target Platform, point location to c:\target\eclipse 
directory. In Preferences -> Run/Debug -> String Substitution define variable 
named 'target_platform' and point it to c:\target directory. During running 
of application other directory besides eclipse will be created inside 
target directory. To run the browser use 'RPC Browser' launching configuration.

CONTACT
=======

If you have questions or problems, subscribe to:

carrot2-developers@lists.sourceforge.net


This e-mailing list will keep you up to date with major changes:

carrot2-news@lists.sourceforge.net


Commercial consulting is offered by a spin-off company Carrot Search:

info@carrot-search.com